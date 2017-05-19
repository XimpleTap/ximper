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

    /**
     * Construct an instance of AES-128 Encryption.
     * @param masterKey The secret key used in AES-128 Encryption process.
     * @param tagId 7-byte Tag UID
     * @param evKey 4-byte Event-specific
     */
    public AES128Encryption(byte[] masterKey, byte[] tagId, byte[] evKey) throws IOException {
        try {
            sha = MessageDigest.getInstance("SHA-1");
            byte[] divInput = new byte[12];
            divInput[0] = 0x01;
            System.arraycopy(tagId, 0, divInput, 1, tagId.length);
            System.arraycopy(evKey, 0, divInput, 8, evKey.length);
            Utils.Log.d(TAG, "divInput = " + Utils.dumpBytes(divInput));

            diversifiedKey = generateDiversifiedKey(masterKey, divInput);
            if(diversifiedKey == null || diversifiedKey.length != 16)
                throw new IOException("Error generating AES diversified key");

        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Password is the last 4 bytes of the generated diversified key.
     * @return Unique password used for PWD_AUTH
     */
    public byte[] fetchDiversifiedPassword() {
        return new byte[] {
                diversifiedKey[diversifiedKey.length - 4],
                diversifiedKey[diversifiedKey.length - 3],
                diversifiedKey[diversifiedKey.length - 2],
                diversifiedKey[diversifiedKey.length - 1]
        };
    }

    /**
     * PACK(Password Acknowledge) is the 5th and 6th to the last bytes of the generated diversified key.
     * @return Unique PACK used to check the response returned by PWD_AUTH
     */
    public byte[] fetchDiversifiedPack() {
        return new byte[] {
                diversifiedKey[diversifiedKey.length - 6],
                diversifiedKey[diversifiedKey.length - 5]
        };
    }

    /**
     * Helper method to apply AES encryption on the data provided.
     *
     */
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

    /**
     * Helper method to shift one bit to the left
     */
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

    /**
     * Generates a diversified key unique to each Mifare ULTRALIGHT EV1 tag. Diversification input includes div constant 0x01 followed by 7-byte tag UID and 4-byte event ID to generate a unique diversification key for each tag, to a specific event.
     * For reference, please see AN10922, section 2.2.1.
     * @param masterKey The secret key used in AES Encryption process
     * @param divInput The diversification input, 0x01##############******** -> # refers to 7-byte UID, * refers to the 4-byte padding
     */
    private byte[] generateDiversifiedKey(byte[] masterKey, byte[] divInput) {
        if(divInput == null || divInput[0] != 0x01)
            return null;

        IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);

        // SubKey generation
        // step 1, AES-128 with key K is applied to an all-zero input block.
        byte[] initSubKey = aesEncrypt(masterKey, ivSpec, new byte[16]);
        if(initSubKey == null)
            return null;
        Utils.Log.d(TAG, "initSubKey: " + Utils.dumpBytes(initSubKey));

        // step 2, K1 is derived through the following operation:
        byte[] firstSubkey = shiftLeftOneBit(initSubKey); //If the most significant bit of L is equal to 0, K1 is the left-shift of L by 1 bit.
        if ((initSubKey[0] & 0x80) == 0x80)
            firstSubkey[15] ^= 0x87; // Otherwise, K1 is the exclusive-OR of const_Rb and the left-shift of L by 1 bit.
        Utils.Log.d(TAG, "firstSubkey: " + Utils.dumpBytes(firstSubkey));

        // step 3, K2 is derived through the following operation:
        byte[] secondSubkey = shiftLeftOneBit(firstSubkey); // If the most significant bit of K1 is equal to 0, K2 is the left-shift of K1 by 1 bit.
        if ((firstSubkey[0] & 0x80) == 0x80)
            secondSubkey[15] ^= 0x87; // Otherwise, K2 is the exclusive-OR of const_Rb and the left-shift of K1 by 1 bit.
        Utils.Log.d(TAG, "secondSubkey: " + Utils.dumpBytes(secondSubkey));

        // MAC computing
        // The last block shall be padded in order to complete 32-bytes of diversification input
        byte[] padding = new byte[32 - divInput.length];
        padding[0] = (byte) 0x80;

        // Concatenate/Join Diversification Input and Padding
        byte[] temp = new byte[divInput.length + padding.length];
        System.arraycopy(divInput, 0, temp, 0, divInput.length);
        System.arraycopy(padding, 0, temp, divInput.length, padding.length);
        divInput = temp;

        // and exclusive-OR'ed with K2
        for (int j = 0; j < secondSubkey.length; j++)
            divInput[divInput.length - 16 + j] ^= secondSubkey[j];
        Utils.Log.d(TAG, "divInput: " + Utils.dumpBytes(divInput));

        // The result of the previous process will be the input of the last encryption.
        // Standard AES encryption with IV = 00s in CBC mode
        byte[] encResult = aesEncrypt(masterKey, ivSpec, divInput);
        if(encResult == null)
            return null;
        Utils.Log.d(TAG, "encResult: " + Utils.dumpBytes(encResult));

        byte[] diversifiedKey = new byte[16];   // Last 16-byte block. (CMAC)
        System.arraycopy(encResult, encResult.length - diversifiedKey.length, diversifiedKey, 0, diversifiedKey.length);
        Utils.Log.d(TAG, "diversifiedKey: " + Utils.dumpBytes(diversifiedKey));

        return diversifiedKey;
    }


}
