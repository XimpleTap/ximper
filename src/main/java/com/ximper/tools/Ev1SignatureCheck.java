package com.ximper.tools;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 * Helper class to perform signature check to MifareUltralight EV1
 */
public class Ev1SignatureCheck {
    private static final String TAG = Ev1SignatureCheck.class.getSimpleName();

    // NXP Public Key
    private final static String NXP_PUB_KEY = "0490933bdcd6e99b4e255e3da55389a827564e11718e017292faf23226a96614b8";

    /**
     * Perform originality check using ACS Reader library.
     *
     * @param reader The ACS Reader instance on which the originality check is performed.
     *
     * @return Returns true if Signature is valid, otherwise, false.
     */
    public static boolean doOriginalityCheck(final CardChannel channel, byte[] tagUid) throws IOException {
        boolean valid = false;

        final byte[] getSignature = new byte[] {(byte) 0xFF, 0x00, 0x00, 0x00, 0x04, (byte) 0xD4, 0x42, 0x3C, 0x00}; // CMD_READ_SIG

        try {
            ResponseAPDU response = channel.transmit(new CommandAPDU(getSignature));
            byte[] rawSignature = response.getBytes();

            // Extract only the response data by excluding D5:43:00:_______:90:00
            byte[] signature = new byte[32];
            System.arraycopy(rawSignature, 3, signature, 0, signature.length);

            Utils.Log.d(TAG, "signature[" + Utils.dumpBytes(signature) + "]");

            try {
                valid = checkEcdsaSignature(NXP_PUB_KEY, signature, tagUid);
            } catch (final NoSuchAlgorithmException e) {
            	Utils.Log.d(TAG, "Cannot verify signature (Android has no ECDSA support)");
            }

            if (valid) {
            	Utils.Log.d(TAG, "Signature verified with NXP public key");
            } else {
            	Utils.Log.d(TAG, "Signature cannot be verified");
            }

        } catch (CardException e) {
        	Utils.Log.d(TAG, "Tag does not support Read Signature Command");
        }

        return valid;
    }

    public static boolean checkEcdsaSignature(final String ecPubKey, final byte[] signature, final byte[] data) throws NoSuchAlgorithmException {
        final ECPublicKeySpec ecPubKeySpec = getEcPubKey(ecPubKey, getEcSecp128r1());
        return checkEcdsaSignature(ecPubKeySpec, signature, data);
    }

    public static boolean checkEcdsaSignature(final ECPublicKeySpec ecPubKey, final byte[] signature, final byte[] data)
            throws NoSuchAlgorithmException {
        KeyFactory keyFac = null;
        try {
            keyFac = KeyFactory.getInstance("EC");
        } catch (final NoSuchAlgorithmException e1) {
            keyFac = KeyFactory.getInstance("ECDSA");
        }

        if (keyFac != null) {
            try {
                final PublicKey publicKey = keyFac.generatePublic(ecPubKey);
                final Signature dsa = Signature.getInstance("NONEwithECDSA");
                dsa.initVerify(publicKey);
                dsa.update(data);
                return dsa.verify(derEncodeSignature(signature));
            } catch (SignatureException se) {
                se.printStackTrace();
            } catch (InvalidKeySpecException ikse) {
                ikse.printStackTrace();
            } catch (InvalidKeyException ike) {
                ike.printStackTrace();
            }
        } else {
            Utils.Log.d(TAG, "keyFac is NULL");
        }

        return false;
    }

    public static ECPublicKeySpec getEcPubKey(final String key, final ECParameterSpec curve) {
        if (key == null || key.length() != 2 * 33 || !key.startsWith("04")) {
            return null;
        }

        final String keyX = key.substring(2 * 1, 2 * 17);
        final String keyY = key.substring(2 * 17, 2 * 33);
        final BigInteger affineX = new BigInteger(keyX, 16);
        final BigInteger affineY = new BigInteger(keyY, 16);
        final ECPoint w = new ECPoint(affineX, affineY);
        return new ECPublicKeySpec(w, curve);
    }

    public static ECParameterSpec getEcSecp128r1() {
        // EC definition of "secp128r1":
        final BigInteger p = new BigInteger("fffffffdffffffffffffffffffffffff", 16);
        final ECFieldFp field = new ECFieldFp(p);
        final BigInteger a = new BigInteger("fffffffdfffffffffffffffffffffffc", 16);
        final BigInteger b = new BigInteger("e87579c11079f43dd824993c2cee5ed3", 16);
        final EllipticCurve curve = new EllipticCurve(field, a, b);
        final BigInteger genX = new BigInteger("161ff7528b899b2d0c28607ca52c5b86", 16);
        final BigInteger genY = new BigInteger("cf5ac8395bafeb13c02da292dded7a83", 16);
        final ECPoint generator = new ECPoint(genX, genY);
        final BigInteger order = new BigInteger("fffffffe0000000075a30d1b9038a115", 16);
        final int cofactor = 1;

        return new ECParameterSpec(curve, generator, order, cofactor);
    }

    public static byte[] derEncodeSignature(final byte[] signature) {
        // split into r and s
        final byte[] r = Arrays.copyOfRange(signature, 0, 16);
        final byte[] s = Arrays.copyOfRange(signature, 16, 32);
        int rLen = r.length;
        int sLen = s.length;
        if ((r[0] & 0x80) != 0) {
            rLen++;
        }

        if ((s[0] & 0x80) != 0) {
            sLen++;
        }

        final byte[] encodedSig = new byte[rLen + sLen + 6]; // 6 T and L bytes
        encodedSig[0] = 0x30; // SEQUENCE
        encodedSig[1] = (byte) (4 + rLen + sLen);
        encodedSig[2] = 0x02; // INTEGER
        encodedSig[3] = (byte) rLen;
        encodedSig[4 + rLen] = 0x02; // INTEGER
        encodedSig[4 + rLen + 1] = (byte) sLen;

        // copy in r and s
        encodedSig[4] = 0;
        encodedSig[4 + rLen + 2] = 0;
        System.arraycopy(r, 0, encodedSig, 4 + rLen - r.length, r.length);
        System.arraycopy(s, 0, encodedSig, 4 + rLen + 2 + sLen - s.length, s.length);

        return encodedSig;
    }

}
