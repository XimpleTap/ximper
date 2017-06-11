package com.ximper.tools;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES128Encryption {

    private final static String TAG = AES128Encryption.class.getSimpleName();

    private MessageDigest sha;
    private byte[] diversifiedKey;
    public AES128Encryption(byte[] encryptionKey, byte[] tagId, byte[] cardKey) throws IOException {
        try {
            sha = MessageDigest.getInstance("SHA-1");
            byte[] divInput = new byte[12];
            divInput[0] = 0x01;
            System.arraycopy(tagId, 0, divInput, 1, tagId.length);
            System.arraycopy(cardKey, 0, divInput, 8, cardKey.length);
            Utils.Log.d(TAG, "divInput = " + Utils.dumpBytes(divInput));

            diversifiedKey = generateDiverseKey(encryptionKey, divInput);
            if(diversifiedKey == null || diversifiedKey.length != 16)
                throw new IOException("Error generating AES diversified key");

        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e.getMessage());
        }
    }

    public byte[] generateDiversePassword() {
        return new byte[] {
                diversifiedKey[diversifiedKey.length - 4],
                diversifiedKey[diversifiedKey.length - 3],
                diversifiedKey[diversifiedKey.length - 2],
                diversifiedKey[diversifiedKey.length - 1]
        };
    }

    public byte[] generateDiversePasswordAck() {
        return new byte[] {
                diversifiedKey[diversifiedKey.length - 6],
                diversifiedKey[diversifiedKey.length - 5]
        };
    }

    private byte[] aesEncrypt(byte[] rawKey, IvParameterSpec iv, byte[] data) {
        byte[] encrypted = null;

        byte[] shaKey = sha.digest(rawKey);
        byte[] aesKey = Arrays.copyOfRange(shaKey, 0, 16);
        SecretKeySpec secKey = new SecretKeySpec(aesKey, "AES");

        try {
            Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, secKey, iv);
            encrypted = c.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return encrypted;
    }

    private byte[] shiftLeftOneBit(byte[] b) {
        byte[] r = new byte[b.length];
        byte carry = 0;

        for (int i = b.length - 1; i >= 0; i--)
        {
            byte u = (byte)(b[i] << 1);
            r[i] = (byte)((u & 0xff) + carry);
            carry = (byte)((u & 0xff00) >> 8);
        }

        return r;
    }

    private byte[] generateDiverseKey(byte[] masterKey, byte[] divInput) {
        if(divInput == null || divInput[0] != 0x01)
            return null;

        IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);
        byte[] initSubKey = aesEncrypt(masterKey, ivSpec, new byte[16]);
        if(initSubKey == null)
            return null;

        byte[] firstSubkey = shiftLeftOneBit(initSubKey);
        if ((initSubKey[0] & 0x80) == 0x80)
            firstSubkey[15] ^= 0x87;


        byte[] secondSubkey = shiftLeftOneBit(firstSubkey);
        if ((firstSubkey[0] & 0x80) == 0x80)
            secondSubkey[15] ^= 0x87;

        byte[] padding = new byte[32 - divInput.length];
        padding[0] = (byte) 0x80;

        byte[] temp = new byte[divInput.length + padding.length];
        System.arraycopy(divInput, 0, temp, 0, divInput.length);
        System.arraycopy(padding, 0, temp, divInput.length, padding.length);
        divInput = temp;

        for (int j = 0; j < secondSubkey.length; j++)
            divInput[divInput.length - 16 + j] ^= secondSubkey[j];

        byte[] encResult = aesEncrypt(masterKey, ivSpec, divInput);
        if(encResult == null)
            return null;

        byte[] diversifiedKey = new byte[16];
        System.arraycopy(encResult, encResult.length - diversifiedKey.length, diversifiedKey, 0, diversifiedKey.length);
        return diversifiedKey;
    }


}
