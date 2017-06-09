package com.ximper.reader;

import com.ximper.tools.Constants;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
/**
 * Interface for unified card reader integration
 */
public interface ILoyaltyCardReader {
	byte[] MASTER_KEY = {
	        (byte) 0x30, Constants.ENCRYPTION_KEY_2D, (byte) 0x69, (byte) 0x48,
	        (byte) 0x38, (byte) 0x59, (byte) 0x68, (byte) 0x74,
	        (byte) 0x35, (byte) 0x63, (byte) 0x69, (byte) 0x68,
	        (byte) 0x36, (byte) 0x64, (byte) 0x4F, Constants.ENCRYPTION_KEY_2E
	};
	
	
	int MIFARE_NOT_SUPPORTED = -1;
	int MIFARE_ULTRALIGHT_C = 0x02;       // MF0ICU2
	int MIFARE_ULTRALIGHT_EV1_11 = 0x11;  // MF0UL11
	int MIFARE_ULTRALIGHT_EV1_21 = 0x21;  // MF0UL21
	
	//version constants
	int VERSION_RESPONSE_LENGTH = 0x08;
	int VENDOR_ID_NXP = 0x04;          // vendor ID
	int PRODUCT_TYPE_MF0UL = 0x03;     // product type MIFARE ULTRALIGHT
	int PRODUCT_SUB_TYPE_17PF = 0x01;  // product subtype(17pF)
	int PRODUCT_SUB_TYPE_50PF = 0x02;  // product subtype(50pF)
	int MAJOR_PRODUCT_VERSION_EV1 = 0x01;  // major product version(01h -> EV1)
	int MINOR_PRODUCT_VERSION_V0 = 0x00;  // minor product version(00h -> V0)
	int STORAGE_SIZE_EV1_11 = 0x0B;      // storage size (0Bh -> 48 bytes -> EV11)
	int STORAGE_SIZE_EV1_21 = 0x0E;      // storage size (0Eh -> 128 bytes -> EV21)
	int PROTOCOL_TYPE_ISOIEC_14443_3 = 0x03;          // protocol type (03h -> ISO/IEC 14443-3 compliant)
	
	// Dedicated Page(Binary Block) Constants
	int BUSINESS_ID_PAGE = 0x04;
	int LOAD_PAGE = 0x05;
	int FUTURE_LOAD_PAGE = 0x06;	
	int POINTS_PAGE = 0x07;
	int FUTURE_POINTS_PAGE = 0x08;
	int MAX_NAME_LENGTH = 8;
	int REFERENCE_NUM_LENGTH = 4;

	// MIFARE ULTRALIGHT C 3DES DESedeEncryption Key
	byte[] key2C = {(byte) 0x74, (byte) 0x68, (byte) 0x59, (byte) 0x38};
	byte[] key2D = {(byte) 0x48, (byte) 0x69, Constants.ENCRYPTION_KEY_2D, (byte) 0x30};
	byte[] key2E = {Constants.ENCRYPTION_KEY_2E, (byte) 0x4F, (byte) 0x64, (byte) 0x36};
	byte[] key2F = {(byte) 0x68, (byte) 0x69, (byte) 0x63, (byte) 0x35};
	byte[] ivByte = {(byte) 0x00, (byte) 0x00, (byte) 0x00,
	        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,};
	
	byte[] pad = {(byte) 0xAF};
    
    String getUid(CardChannel channel) throws Exception;
    
    boolean lockTag(byte[] eventKey);
    
    boolean authenticate(byte[] eventKey);

    boolean assignTag(int tokens, int ticketType, int eventId, byte[] eventKey, String name);

    boolean reload(int tokens);

    boolean setAccess(String page, int accessQty);

    int readEventId();

    boolean cleanTag(int toPage);

    boolean resetTag();

    boolean setName(String name);

    String readName();
    
}
