package com.ximper.objects;

public class TransactionDetailObject {

	private int oldBalance;
	private int newBalance;
	private int transactionType;
	private int productId;
	private int itemCount;
	private int itemPrice;
	private int loadBonus;
	private String tagId;
	private String transactionTime;
	public int getOldBalance() {
		return oldBalance;
	}
	public void setOldBalance(int oldBalance) {
		this.oldBalance = oldBalance;
	}
	public int getNewBalance() {
		return newBalance;
	}
	public void setNewBalance(int newBalance) {
		this.newBalance = newBalance;
	}
	public int getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	public int getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(int itemPrice) {
		this.itemPrice = itemPrice;
	}
	public int getLoadBonus() {
		return loadBonus;
	}
	public void setLoadBonus(int loadBonus) {
		this.loadBonus = loadBonus;
	}
	public String getTagId() {
		return tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	public String getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
}
