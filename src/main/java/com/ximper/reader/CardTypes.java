package com.ximper.reader;

public enum CardTypes {
	MIFARE_ULTRALIGHT_C(7),
	MIFARE_CLASSIC_1K(4);
	
	private int uidLength;
	public int getUidLength() {
		return uidLength;
	}
	CardTypes(int uidLength){
		this.uidLength=uidLength;
	}
}
