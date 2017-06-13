package com.ximper.objects;

public class AcquireProductResult {

	private int totalItems;
	private int totalPrice;
	private int oldBalance;
	private int remainingBalance;
	private int totalPointsAcquired;
	private int newPointBalance;
	private int pointsBeforeAcquire;
	public int getTotalItems() {
		return totalItems;
	}
	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}
	public int getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	public int getOldBalance() {
		return oldBalance;
	}
	public void setOldBalance(int oldBalance) {
		this.oldBalance = oldBalance;
	}
	public int getRemainingBalance() {
		return remainingBalance;
	}
	public void setRemainingBalance(int remainingBalance) {
		this.remainingBalance = remainingBalance;
	}
	public int getTotalPointsAcquired() {
		return totalPointsAcquired;
	}
	public void setTotalPointsAcquired(int totalPointsAcquired) {
		this.totalPointsAcquired = totalPointsAcquired;
	}
	public int getNewPointBalance() {
		return newPointBalance;
	}
	public void setNewPointBalance(int newPointBalance) {
		this.newPointBalance = newPointBalance;
	}
	public int getPointsBeforeAcquire() {
		return pointsBeforeAcquire;
	}
	public void setPointsBeforeAcquire(int pointsBeforeAcquire) {
		this.pointsBeforeAcquire = pointsBeforeAcquire;
	}
}
