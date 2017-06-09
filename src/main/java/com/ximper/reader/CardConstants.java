package com.ximper.reader;

import com.ximper.tools.Constants;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
/**
 * Interface for unified card reader integration
 */
public class CardConstants {
	public static byte[] MASTER_KEY = {
	        (byte) 0x30, Constants.ENCRYPTION_KEY_2D, (byte) 0x69, (byte) 0x48,
	        (byte) 0x38, (byte) 0x59, (byte) 0x68, (byte) 0x74,
	        (byte) 0x35, (byte) 0x63, (byte) 0x69, (byte) 0x68,
	        (byte) 0x36, (byte) 0x64, (byte) 0x4F, Constants.ENCRYPTION_KEY_2E
	};
	
	
	public static int MIFARE_NOT_SUPPORTED = -1;
	public static int MIFARE_ULTRALIGHT_C = 0x02;       // MF0ICU2
	public static int MIFARE_ULTRALIGHT_EV1_11 = 0x11;  // MF0UL11
	public static int MIFARE_ULTRALIGHT_EV1_21 = 0x21;  // MF0UL21
	
	//version constants
	public static int VERSION_RESPONSE_LENGTH = 0x08;
	public static int VENDOR_ID_NXP = 0x04;          // vendor ID
	public static int PRODUCT_TYPE_MF0UL = 0x03;     // product type MIFARE ULTRALIGHT
	public static int PRODUCT_SUB_TYPE_17PF = 0x01;  // product subtype(17pF)
	public static int PRODUCT_SUB_TYPE_50PF = 0x02;  // product subtype(50pF)
	public static int MAJOR_PRODUCT_VERSION_EV1 = 0x01;  // major product version(01h -> EV1)
	public static int MINOR_PRODUCT_VERSION_V0 = 0x00;  // minor product version(00h -> V0)
	public static int STORAGE_SIZE_EV1_11 = 0x0B;      // storage size (0Bh -> 48 bytes -> EV11)
	public static int STORAGE_SIZE_EV1_21 = 0x0E;      // storage size (0Eh -> 128 bytes -> EV21)
	public static int PROTOCOL_TYPE_ISOIEC_14443_3 = 0x03;          // protocol type (03h -> ISO/IEC 14443-3 compliant)
	
	// Dedicated Page(Binary Block) Constants
	public static int BUSINESS_ID_PAGE = 0x04;
	public static int LOAD_PAGE = 0x05;
	public static int ADDITIONAL_ON_NEXT_RELOAD_PAGE = 0x06;	
	public static int POINTS_PAGE = 0x07;
	public static int FUTURE_POINTS_PAGE = 0x08;
	public static int MAX_NAME_LENGTH = 8;
	public static int REFERENCE_NUM_LENGTH = 4;

	// MIFARE ULTRALIGHT C 3DES DESedeEncryption Key
	public static byte[] key2C = {(byte) 0x74, (byte) 0x68, (byte) 0x59, (byte) 0x38};
	public static byte[] key2D = {(byte) 0x48, (byte) 0x69, Constants.ENCRYPTION_KEY_2D, (byte) 0x30};
	public static byte[] key2E = {Constants.ENCRYPTION_KEY_2E, (byte) 0x4F, (byte) 0x64, (byte) 0x36};
	public static byte[] key2F = {(byte) 0x68, (byte) 0x69, (byte) 0x63, (byte) 0x35};
	public static byte[] ivByte = {(byte) 0x00, (byte) 0x00, (byte) 0x00,
	        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,};
	
	public static byte[] pad = {(byte) 0xAF};
}
