package com.ximper.reader;
import java.security.Security;
import javax.smartcardio.*;

import com.ximper.configurations.CardReaderMessages;
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
	
	@SuppressWarnings("restriction")
	public String getTagId(CardChannel cardChannel) throws CardException{
		String tagId="";
		byte[] getTagCommand = new byte[] { (byte) 0xFF, (byte) 0xCA, 0x00, (byte) 0x00, (byte) 0x00 };
		ResponseAPDU responseApdu=cardChannel.transmit(new CommandAPDU(getTagCommand));
		byte[] response=responseApdu.getBytes();
		if (responseApdu.getSW() != 0x9000){
			return "";
		}else{
			tagId=Utils.byteArrayToStr(response, CardTypes.MIFARE_CLASSIC_1K.getUidLength()).trim();
		}
		cardConnection.disconnect(true);
		return tagId;
	}
}
