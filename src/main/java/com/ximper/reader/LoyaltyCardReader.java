package com.ximper.reader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Security;
import java.util.Arrays;

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

	public String getTagId() throws Exception {
		String tagId;
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

			if (responseApdu.getSW() != 0x9000) { // Response Code FAILED
				throw new IOException("Read device ID failed...");
			}
			
			tagId = Utils.byteArrayToStr(response, 7).trim();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			tagId = "";
		} catch (CardException e) {
			e.printStackTrace();
			tagId = "";
		}

		return tagId;
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

	private boolean protectCardMemory(byte[] eventKey) throws Exception{
		byte[] tagId= getTagId().getBytes();
		boolean result = false;

		byte pwdAddress = (byte) 0x27;
		byte packAddress = (byte) 0x28;
		byte modConfAddress = (byte) 0x25;
		byte accessConfAddress = (byte) 0x26;

		try {
			com.ximper.tools.AES128Encryption aes128 = new com.ximper.tools.AES128Encryption(CardConstants.MASTER_KEY, tagId, eventKey);
			byte[] password = aes128.fetchDiversifiedPassword();
			byte[] pack = aes128.fetchDiversifiedPack();

			/*
			 * 1.) SET PASSWORD (on 0x12) [A2:12:##:##:##:##] - Set a 4-byte
			 * password
			 */
			byte[] setPassCmd = new byte[] { (byte) 0xFF, (byte) 0xD6, 0x00, pwdAddress };
			ByteArrayOutputStream baSetPassword = new ByteArrayOutputStream();
			baSetPassword.write(setPassCmd);
			baSetPassword.write(password.length);
			baSetPassword.write(password);

			ResponseAPDU raSetPass = cardChannel.transmit(new CommandAPDU(baSetPassword.toByteArray()));
			// Validate if operation is successful
			if (raSetPass.getSW() != 0x9000) {
				throw new CardException("");
			}

			byte[] setPassResponse = raSetPass.getBytes();
			/*
			 * 2.) SET Password Acknowledge (on 0x13) [A2:13:##:##:00:00] - Set
			 * expected response returned by PWD_AUTH operation
			 */
			byte[] packCmd = new byte[] { (byte) 0xFF, (byte) 0xD6, 0x00, packAddress };
			ByteArrayOutputStream baSetPack = new ByteArrayOutputStream();
			baSetPack.write(packCmd);
			baSetPack.write(pack.length + 2);
			baSetPack.write(/* MF0ULx1_PWD_ACK */pack);
			baSetPack.write(new byte[] { 0x00, 0x00 });

			ResponseAPDU raSetPack = cardChannel.transmit(new CommandAPDU(baSetPack.toByteArray()));
			// Validate if operation is successful
			if (raSetPack.getSW() != 0x9000) {
				throw new CardException("");
			}

			byte[] setPackResponse = raSetPack.getBytes();
			byte[] modConfig = new byte[] { 0x00, 0x00, 0x00,
					(byte) CardConstants.PWD_PROTECT_PAGE };
			byte[] modConfigCmd = new byte[] { (byte) 0xFF, (byte) 0xD6, 0x00, modConfAddress };
			ByteArrayOutputStream baSetModConf = new ByteArrayOutputStream();
			baSetModConf.write(modConfigCmd);
			baSetModConf.write(modConfig.length);
			baSetModConf.write(modConfig);

			ResponseAPDU raModConf = cardChannel.transmit(new CommandAPDU(baSetModConf.toByteArray()));
			// Validate if operation is successful
			if (raModConf.getSW() != 0x9000) {
				throw new CardException("");
			}

			byte[] modConfResponse = raModConf.getBytes();
			byte[] pwdAuthCmd = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x00, 0x07, (byte) 0xD4, 0x42, (byte) 0x1B };
			ByteArrayOutputStream baPwdAuth = new ByteArrayOutputStream();
			baPwdAuth.write(pwdAuthCmd);
			baPwdAuth.write(password);

			ResponseAPDU raPwdAuth = cardChannel.transmit(new CommandAPDU(baPwdAuth.toByteArray()));
			if (raPwdAuth.getSW() != 0x9000) {
				throw new CardException("");
			}

			byte[] rawPwdAuthResponse = raPwdAuth.getBytes();
			byte[] pwdAuthResponse = new byte[2];
			System.arraycopy(rawPwdAuthResponse, 3, pwdAuthResponse, 0, pwdAuthResponse.length);

			if (Arrays.equals(pwdAuthResponse, pack)) {
				byte[] accessConfig = new byte[] {
						CardConstants.AUTH_CONFIG, 0x05, 0x00, 0x00 };
				byte[] accessConfigCmd = new byte[] { (byte) 0xFF, (byte) 0xD6, 0x00, accessConfAddress };

				ByteArrayOutputStream baSetAccessConf = new ByteArrayOutputStream();
				baSetAccessConf.write(accessConfigCmd);
				baSetAccessConf.write(accessConfig.length);
				baSetAccessConf.write(accessConfig);

				ResponseAPDU raSetAccess = cardChannel.transmit(new CommandAPDU(baSetAccessConf.toByteArray()));
				// Validate if operation is successful
				if (raSetAccess.getSW() != 0x9000) {
					throw new CardException("");
				}

				byte[] setAccessResponse = raSetAccess.getBytes();
				result = true;
			} else {
				throw new IOException("Invalid PACK response");
			}
		} catch (CardException | IOException exc) {
			result = false;
		}

		return result;
	}
}
