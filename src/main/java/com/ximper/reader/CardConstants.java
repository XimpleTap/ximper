package com.ximper.reader;

import com.ximper.tools.Constants;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
/**
 * Interface for unified card reader integration
 */
public class CardConstants {
	public static byte[] AUTHENTICATION_MASTER_KEY = {
	        (byte) 0x48, (byte) 0x29, (byte) 0x29, (byte) 0xAE,
	        (byte) 0x58, (byte) 0x69, (byte) 0x08, (byte) 0x2A,
	        (byte) 0x25, (byte) 0x7E, (byte) 0x2F, (byte) 0x3C,
	        (byte) 0x1F, (byte) 0x84, (byte) 0xEE, (byte) 0xAC
	};
	
	
	//public static int MIFARE_NOT_SUPPORTED = -1;
	//public static int MIFARE_ULTRALIGHT_C = 0x02;       
	//public static int MIFARE_ULTRALIGHT_EV1_11 = 0x11;
	public static int MIFARE_ULTRALIGHT_EV1_21 = 0x21;
	
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
	public static int ADDITIONAL_ON_NEXT_ACQUIRE_PAGE = 0x08;
	public static int CARD_EXPIRY_MONTH_AND_DAY = 0x09;
	public static int CARD_EXPIRY_YEAR = 0x10;
	public static int PWD_PROTECT_PAGE = 4;
	public static int MAX_NAME_LENGTH = 8;
	public static int REFERENCE_NUM_LENGTH = 4;

	
	public static byte[] pad = {(byte) 0xAF};
	
	public static byte AUTH_CONFIG = (byte) 0xC7;
	
	public static byte[] CARD_KEY=new byte[]{(byte) 0xAA,(byte) 0xCA, (byte) 0x12, (byte) 0xC2};
	
	
	
}
