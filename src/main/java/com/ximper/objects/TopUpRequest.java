package com.ximper.objects;

public class TopUpRequest {

	private int denomId;
	private int cashierId;
	private String transactionTime;
	public int getDenomId() {
		return denomId;
	}
	public void setDenomId(int denomId) {
		this.denomId = denomId;
	}
	public int getCashierId() {
		return cashierId;
	}
	public void setCashierId(int cashierId) {
		this.cashierId = cashierId;
	}
	public String getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
}
