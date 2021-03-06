package com.ximper.reader;
import java.io.IOException;
import java.security.Security;
import javax.smartcardio.*;
import com.ximper.configurations.CardReaderMessages;
import com.ximper.tools.Utils;

import jnasmartcardio.Smartcardio;

public class LoyaltyCardReader implements ILoyaltyCardReader{

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

	@Override
	public boolean lockTag(byte[] eventKey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean authenticate(byte[] eventKey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assignTag(int tokens, int ticketType, int eventId, byte[] eventKey, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setTokens(int tokens) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setAccess(String page, int accessQty) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int readEventId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean cleanTag(int toPage) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean resetTag() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setName(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String readName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUid(CardChannel channel) throws Exception {
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
			ResponseAPDU responseApdu = channel.transmit(new CommandAPDU(cmd));
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


	
}
