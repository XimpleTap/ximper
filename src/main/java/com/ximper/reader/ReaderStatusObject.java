package com.ximper.reader;

import javax.smartcardio.CardChannel;

@SuppressWarnings("restriction")
public class ReaderStatusObject {
	private CardChannel cardChannel;
	private String connectionMesssage;
	public CardChannel getCardChannel() {
		return cardChannel;
	}
	public void setCardChannel(CardChannel cardChannel) {
		this.cardChannel = cardChannel;
	}
	public String getConnectionMesssage() {
		return connectionMesssage;
	}
	public void setConnectionMesssage(String connectionMesssage) {
		this.connectionMesssage = connectionMesssage;
	}
}
