package com.ximper.objects;

public class ApiCardSalesRequest {

	private String cardNumber;
	private int businessId;
	private long cashierId;
	private int cardGroupId;
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public int getBusinessId() {
		return businessId;
	}
	public void setBusinessId(int businessId) {
		this.businessId = businessId;
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
