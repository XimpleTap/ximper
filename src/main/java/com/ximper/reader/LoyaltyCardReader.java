package com.ximper.reader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Security;
import java.util.Arrays;

import javax.smartcardio.*;

import com.ximper.configurations.CardReaderMessages;
import com.ximper.tools.AES128Encryption;
import com.ximper.tools.Utils;

import jnasmartcardio.Smartcardio;

public class LoyaltyCardReader {

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
	public ReaderStatusObject connect() {
		ReaderStatusObject statusObject = new ReaderStatusObject();
		Security.addProvider(new Smartcardio());
		TerminalFactory terminalFactory = TerminalFactory.getDefault();
		cardTerminals = terminalFactory.terminals();
		try {
			if (cardTerminals.list().isEmpty()) {
			} else {
				cardTerminal = cardTerminals.list().get(0);
				isCardPresent = cardTerminal.waitForCardPresent(10000);
				if (isCardPresent) {
					cardConnection = cardTerminal.connect("*");
					cardChannel = cardConnection.getBasicChannel();
					statusObject.setCardChannel(cardChannel);
					statusObject.setConnectionMesssage(CardReaderMessages.CONNECTION_OK);
				} else {
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

	@SuppressWarnings("restriction")
	public String getTagId() throws Exception {
		String tagId;
		try {
			// APDU Command: "GET_DATA" Example: FF CA 00 00 00 - 06 is the page
			byte[] cmd = new byte[] { (byte) 0xFF, (byte) 0xCA, 0x00, (byte) 0x00, (byte) 0x00 };

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
			byte[] prefix = new byte[] { (byte) 0xFF, (byte) 0xD6, 0x00, (byte) pageNumber };
			// byte[] prefix = new byte[] { (byte) 0xFF, (byte) 0xA2,
			// 0x00,(byte) pageNumber};
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

	@SuppressWarnings("restriction")
	public int readDataFromMemory(int pageNumber) {
		int result = 0;

		try {
			/*
			 * APDU Command: "GET_DATA" Example: FF B0 00 06 10 - 06 is the page
			 * number - 10 is the response data length(must be multiple of 16
			 * bytes)
			 */
			byte[] cmd = new byte[] { (byte) 0xFF, (byte) 0xB0, 0x00, (byte) pageNumber,
					(byte) 0x04 /* Multiple of 16 bytes */ };
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

	@SuppressWarnings("restriction")
	public boolean protectCardMemory() throws Exception {
		byte[] cardKey = CardConstants.CARD_KEY;
		byte[] tagId = Utils.hexStrToByteArray(getTagId());
		;

		boolean result = false;

		byte modConfigAddress = (byte) 0x25;
		byte accessConfigAddress = (byte) 0x26;
		byte passwordAddress = (byte) 0x27;
		byte passwordAckAddress = (byte) 0x28;

		try {
			AES128Encryption encrypter = new AES128Encryption(CardConstants.AUTHENTICATION_MASTER_KEY, tagId, cardKey);
			byte[] password = encrypter.generateDiversePassword();
			byte[] passwordAck = encrypter.generateDiversePasswordAck();

			// set command for password
			byte[] setPasswordCommand = new byte[] { (byte) 0xFF, (byte) 0xD6, 0x00, passwordAddress };
			ByteArrayOutputStream setPasswordByteArray = new ByteArrayOutputStream();
			setPasswordByteArray.write(setPasswordCommand);
			setPasswordByteArray.write(password.length);
			setPasswordByteArray.write(password);

			ResponseAPDU setPasswordResponse = cardChannel
					.transmit(new CommandAPDU(setPasswordByteArray.toByteArray()));
			if (setPasswordResponse.getSW() != 0x9000) {
				throw new CardException("");
			}

			// set command for password ack
			byte[] setPasswordAckCommand = new byte[] { (byte) 0xFF, (byte) 0xD6, 0x00, passwordAckAddress };
			ByteArrayOutputStream setPasswordAckByteArray = new ByteArrayOutputStream();
			setPasswordAckByteArray.write(setPasswordAckCommand);
			setPasswordAckByteArray.write(passwordAck.length + 2);
			setPasswordAckByteArray.write(passwordAck);
			setPasswordAckByteArray.write(new byte[] { 0x00, 0x00 });

			ResponseAPDU setPasswordAckResponse = cardChannel
					.transmit(new CommandAPDU(setPasswordAckByteArray.toByteArray()));
			if (setPasswordAckResponse.getSW() != 0x9000) {
				throw new CardException("");
			}

			byte[] modConfig = new byte[] { 0x00, 0x00, 0x00, (byte) CardConstants.PWD_PROTECT_PAGE };
			byte[] modConfigCommand = new byte[] { (byte) 0xFF, (byte) 0xD6, 0x00, modConfigAddress };
			ByteArrayOutputStream baSetModConf = new ByteArrayOutputStream();
			baSetModConf.write(modConfigCommand);
			baSetModConf.write(modConfig.length);
			baSetModConf.write(modConfig);

			ResponseAPDU setModConfigResponse = cardChannel.transmit(new CommandAPDU(baSetModConf.toByteArray()));
			if (setModConfigResponse.getSW() != 0x9000) {
				throw new CardException("");
			}

			byte[] accessConfig = new byte[] { CardConstants.AUTH_CONFIG, 0x05, 0x00, 0x00 };
			byte[] accessConfigCmd = new byte[] { (byte) 0xFF, (byte) 0xD6, 0x00, accessConfigAddress };

			ByteArrayOutputStream setAccessByteArray = new ByteArrayOutputStream();
			setAccessByteArray.write(accessConfigCmd);
			setAccessByteArray.write(accessConfig.length);
			setAccessByteArray.write(accessConfig);

			ResponseAPDU setAccessResponse = cardChannel.transmit(new CommandAPDU(setAccessByteArray.toByteArray()));
			if (setAccessResponse.getSW() != 0x9000) {
				throw new CardException("");
			}
			/*
			 * byte[] pwdAuthCmd = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x00,
			 * 0x07, (byte) 0xD4, 0x42, (byte) 0x1B }; ByteArrayOutputStream
			 * baPwdAuth = new ByteArrayOutputStream();
			 * baPwdAuth.write(pwdAuthCmd); baPwdAuth.write(password);
			 * 
			 * ResponseAPDU raPwdAuth = cardChannel.transmit(new
			 * CommandAPDU(baPwdAuth.toByteArray())); if (raPwdAuth.getSW() !=
			 * 0x9000) { throw new CardException(""); }
			 * 
			 * byte[] rawPwdAuthResponse = raPwdAuth.getBytes(); byte[]
			 * pwdAuthResponse = new byte[2];
			 * System.arraycopy(rawPwdAuthResponse, 3, pwdAuthResponse, 0,
			 * pwdAuthResponse.length);
			 * 
			 * if (Arrays.equals(pwdAuthResponse, passwordAck)) { byte[]
			 * accessConfig = new byte[] { CardConstants.AUTH_CONFIG, 0x05,
			 * 0x00, 0x00 }; byte[] accessConfigCmd = new byte[] { (byte) 0xFF,
			 * (byte) 0xD6, 0x00, accessConfigAddress};
			 * 
			 * ByteArrayOutputStream baSetAccessConf = new
			 * ByteArrayOutputStream(); baSetAccessConf.write(accessConfigCmd);
			 * baSetAccessConf.write(accessConfig.length);
			 * baSetAccessConf.write(accessConfig);
			 * 
			 * ResponseAPDU raSetAccess = cardChannel.transmit(new
			 * CommandAPDU(baSetAccessConf.toByteArray())); if
			 * (raSetAccess.getSW() != 0x9000) { throw new CardException(""); }
			 * 
			 * result = true; } else { throw new IOException(""); }
			 */
		} catch (CardException | IOException exc) {
			result = false;
		}

		return result;
	}

	@SuppressWarnings("restriction")
	public boolean isAuthenticatedBeforeAccess(byte[] authenticationKey) throws Exception {
		boolean result;

		try {
			byte[] tagId = Utils.hexStrToByteArray(getTagId());
			AES128Encryption aes128 = new AES128Encryption(CardConstants.AUTHENTICATION_MASTER_KEY, tagId,
					authenticationKey);

			byte[] authenticateCommand = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x00, (byte) 0x07, (byte) 0xD4,
					(byte) 0x42, (byte) 0x1B };
			ByteArrayOutputStream baPwdAuth = new ByteArrayOutputStream();
			baPwdAuth.write(authenticateCommand);
			baPwdAuth.write(aes128.generateDiversePassword());

			ResponseAPDU response = cardChannel.transmit(new CommandAPDU(baPwdAuth.toByteArray()));
			byte[] rawPwdAuthResponse = response.getBytes();

			byte[] pwdAuthResponse = new byte[2];
			System.arraycopy(rawPwdAuthResponse, 3, pwdAuthResponse, 0, pwdAuthResponse.length);

			if (Arrays.equals(pwdAuthResponse, aes128.generateDiversePasswordAck())) {
				result = true;
			} else {
				throw new IOException("");
			}
		} catch (CardException | IOException ioe) {
			result = false;
		}

		return result;
	}
}
