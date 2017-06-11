package com.ximper.objects;

public class CardSalesObject {

	private String cardNumber;
	private long cashierId;
	private int cardGroupId;
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public long getCashierId() {
		return cashierId;
	}
	public void setCashierId(long cashierId) {
		this.cashierId = cashierId;
	}
	public int getCardGroupId() {
		return cardGroupId;
	}
	public void setCardGroupId(int cardGroupId) {
		this.cardGroupId = cardGroupId;
	}
}
