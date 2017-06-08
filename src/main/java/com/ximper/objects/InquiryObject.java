package com.ximper.objects;

public class InquiryObject {
	private double cardLoadBalance;
	private double cardPointsBalance;
	private long cardId;
	private long userId;
	private int businessId;
	private String serialNumber;
	private String cardHolderName;
	
	public double getCardLoadBalance() {
		return cardLoadBalance;
	}
	public void setCardLoadBalance(double cardLoadBalance) {
		this.cardLoadBalance = cardLoadBalance;
	}
	public double getCardPointsBalance() {
		return cardPointsBalance;
	}
	public void setCardPointsBalance(double cardPointsBalance) {
		this.cardPointsBalance = cardPointsBalance;
	}
	public long getCardId() {
		return cardId;
	}
	public void setCardId(long cardId) {
		this.cardId = cardId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getBusinessId() {
		return businessId;
	}
	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getCardHolderName() {
		return cardHolderName;
	}
	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}
}
