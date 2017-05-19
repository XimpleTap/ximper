package com.ximper.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

	private static final byte[] HEX_CHAR = new byte[]{'0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	/**
     * Converts the contents of buffer into HEX String representation.
     *
     * @param buffer       the buffer.
     * @param bufferLength the buffer length.
     */
    public static String byteArrayToStr(byte[] buffer, int bufferLength) {

        String bufferString = "";

        for (int i = 0; i < bufferLength; i++) {
        	try{
            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }

            bufferString += hexChar.toUpperCase() + "";
        	}catch(Exception e){
        		e.getMessage();
        	}
        }

        return bufferString;
    }

    /**
     * Truncates a string into multiple line(s) of texts given a max character count per line.
     *
     * @param text            The original text to truncate.
     * @param maxCountPerLine Maximum characters allowed per line of text.
     * @return Returns a truncated string divided by '\n' to denote each line of text
     */
    public static String strTruncateToMultipleLines(String text, int maxCountPerLine) {
        String[] words = text.split("\\s|\\n");
        int wordLen = words.length;

        int len = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordLen; i++) {
            if (len + words[i].length() <= maxCountPerLine) {
                sb.append(words[i]);
                len += words[i].length();

                if (len < maxCountPerLine) {
                    sb.append(" ");
                    len++;
                }
            } else {
                sb.append("\n");
                len = 0;
                i--;
            }
        }

//        Log.d(TAG, "truncated = " + sb.toString());

        return sb.toString();
    }
    
    /**
     * Converts the HEX string to byte array.
     *
     * @param hexString
     *            the HEX string.
     * @return the byte array.
     */
    public static byte[] hexStrToByteArray(String hexString) {

        int hexStringLength = hexString.length();
        byte[] byteArray = null;
        int count = 0;
        char c;
        int i;

        // Count number of hex characters
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f') {
                count++;
            }
        }

        byteArray = new byte[(count + 1) / 2];
        boolean first = true;
        int len = 0;
        int value;
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {

                if (first) {

                    byteArray[len] = (byte) (value << 4);

                } else {

                    byteArray[len] |= value;
                    len++;
                }

                first = !first;
            }
        }

        return byteArray;
    }
    
    public static class Log {
    	
    	static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss MMM/dd/yyyy");
    	
    	public static void d(String TAG, String message) {
    		System.out.println(String.format("%s - %s : %s", TAG, sdf.format(Calendar.getInstance().getTime()), message));
    	}
    }
	
    public static final String dumpBytes(byte[] buffer) {
        if (buffer == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        for (int i = 0; i < buffer.length; i++) {
            sb.append((char) (HEX_CHAR[(buffer[i] & 0x00F0) >> 4])).append(
                    (char) (HEX_CHAR[buffer[i] & 0x000F]));
        }
        return sb.toString();
    }
    
    public static final int SCARD_CTL_CODE(int command) {
        boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        if (isWindows) {
            return 0x00310000 | (command << 2);
        } else {
            return 0x42000000 | command;
        }
    }
    
}
