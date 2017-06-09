package com.ximper.reader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Security;
import javax.smartcardio.*;

import com.ximper.configurations.CardReaderMessages;
import com.ximper.tools.Utils;

import jnasmartcardio.Smartcardio;

public class LoyaltyCardReader{

	@SuppressWarnings("restriction")
	private Card cardConnection;
	
	@SuppressWarnings("restriction")
	private CardTerminal cardTerminal;
	
	@SuppressWarnings("restriction")
	private CardTerminals cardTerminals;
	
	@SuppressWarnings("restriction")
	private CardChannel cardChannel;
	
	private boolean isCardPresent;
	
	@SuppressWarnings("restriction")
	public ReaderStatusObject connect(){
			ReaderStatusObject statusObject=new ReaderStatusObject();
			Security.addProvider(new Smartcardio());
			TerminalFactory terminalFactory=TerminalFactory.getDefault();
			cardTerminals=terminalFactory.terminals();
			try {
				if(cardTerminals.list().isEmpty()){
				}else{
					cardTerminal=cardTerminals.list().get(0);
					isCardPresent=cardTerminal.waitForCardPresent(10000);
					if(isCardPresent){
						cardConnection=cardTerminal.connect("*");
						cardChannel=cardConnection.getBasicChannel();
						statusObject.setCardChannel(cardChannel);
						statusObject.setConnectionMesssage(CardReaderMessages.CONNECTION_OK);
					}else{
						statusObject.setCardChannel(null);
						statusObject.setConnectionMesssage(CardReaderMessages.CARD_NOT_PRESENT);
					}
				}
			} catch (CardException e) {
				e.printStackTrace();
				statusObject.setCardChannel(null);
				statusObject.setConnectionMesssage(CardReaderMessages.NO_READER_ATTACHED);
			}
		
		return statusObject;
	}

	public String getUid(CardChannel channel) throws Exception {
		connect();
		String deviceId;

		try {
			/*
			 * APDU Command: "GET_DATA" Example: FF CA 00 00 00 - 06 is the page
			 * number - 10 is the response data length(must be multiple of 16
			 * bytes)
			 */
			byte[] cmd = new byte[] { (byte) 0xFF, (byte) 0xCA, 0x00, (byte) 0x00, (byte) 0x00 };
			//Log.d("readTagId()", "Command: " + Utils.byteArrayToStr(cmd, cmd.length));

			/*
			 * byte[] response = new byte[128]; int responseLength =
			 * this.reader.transmit(0, cmd, cmd.length, response,
			 * response.length);
			 */
			ResponseAPDU responseApdu = cardChannel.transmit(new CommandAPDU(cmd));
			byte[] response = responseApdu.getBytes();

			/*
			 * Sample Response: 04 10 5F 2A A1 43 80 [90 00] 90 00 - SUCCESS
			 * Otherwise - FAIL
			 *
			 * Validate Response
			 */
			if (responseApdu.getSW() != 0x9000) { // Response Code FAILED
				throw new IOException("Read device ID failed...");
			}

			deviceId = Utils.byteArrayToStr(response, 7).trim();
			//Log.d(TAG, "TAG ID: " + deviceId);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			deviceId = "";
		} catch (CardException e) {
			e.printStackTrace();
			deviceId = "";
		}

		return deviceId;
	}

	public boolean reload(int loadAmount, int rewardPoint, int futureLoad, int futurePoint) {
		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writeToCardMemory(4,loadAmount);
	}
	
	@SuppressWarnings("restriction")
	public boolean writeToCardMemory(int pageNumber, int dataToWrite) {
		boolean result;
		try {
			/*
			 * APDU Command: "WRITE_DATA" Example: FF D6 00 06 04 00 00 00 01 -
			 * 06 is the page number - 04 is the input data length - 00 00 00 01
			 * is the input data
			 */
			byte[] dataBytes = ByteBuffer.allocate(4).putInt(dataToWrite).array();
			byte[] prefix = new byte[] { (byte) 0xFF, (byte) 0xD6, 0x00,(byte) pageNumber};
			ByteArrayOutputStream payload = new ByteArrayOutputStream();
			payload.write(prefix);
			payload.write(dataBytes.length);
			payload.write(dataBytes);

			byte[] cmd = payload.toByteArray();
			ResponseAPDU responseApdu = cardChannel.transmit(new CommandAPDU(cmd));
			byte[] response = responseApdu.getBytes();
			if (responseApdu.getSW() != 0x9000) {
				throw new IOException();
			}
			result = true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			result = false;
		} catch (CardException ce) {
			ce.printStackTrace();
			result = false;
		}

		return result;
	}


	public int readDataFromMemory(int pageNumber) {
		int result = 0;

		try {
			/*
			 * APDU Command: "GET_DATA" Example: FF B0 00 06 10 - 06 is the page
			 * number - 10 is the response data length(must be multiple of 16
			 * bytes)
			 */
			byte[] cmd = new byte[] { (byte) 0xFF, (byte) 0xB0, 0x00, (byte) pageNumber,
					(byte) 0x10 /* Multiple of 16 bytes */ };
			ResponseAPDU responseApdu = cardChannel.transmit(new CommandAPDU(cmd));
			byte[] response = responseApdu.getBytes();
			if (responseApdu.getSW() != 0x9000) {
				throw new IOException("GET_DATA failed...");
			}
			byte[] value = new byte[4];
			value[0] = response[0];
			value[1] = response[1];
			value[2] = response[2];
			value[3] = response[3];

			ByteBuffer bb = ByteBuffer.wrap(value);

			result = bb.getInt();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (CardException ce) {
			ce.printStackTrace();
		}

		return result;
	}

}
