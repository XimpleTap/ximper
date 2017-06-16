package com.ximper.objects;

public class ClaimTransactionDetailObject {
	private String tagId;
	private int cashierId;
	private int rewardId;
	private int pointsBeforeClaim;
	private int pointsAfterClaim;
	private int requiredPoints;
	private int itemCount;
	private int estimatedPrice;
	private String transactionTime;
	public String getTagId() {
		return tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	public int getCashierId() {
		return cashierId;
	}
	public void setCashierId(int cashierId) {
		this.cashierId = cashierId;
	}
	public int getRewardId() {
		return rewardId;
	}
	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}
	public int getPointsBeforeClaim() {
		return pointsBeforeClaim;
	}
	public void setPointsBeforeClaim(int pointsBeforeClaim) {
		this.pointsBeforeClaim = pointsBeforeClaim;
	}
	public int getPointsAfterClaim() {
		return pointsAfterClaim;
	}
	public void setPointsAfterClaim(int pointsAfterClaim) {
		this.pointsAfterClaim = pointsAfterClaim;
	}
	public int getRequiredPoints() {
		return requiredPoints;
	}
	public void setRequiredPoints(int requiredPoints) {
		this.requiredPoints = requiredPoints;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	public int getEstimatedPrice() {
		return estimatedPrice;
	}
	public void setEstimatedPrice(int estimatedPrice) {
		this.estimatedPrice = estimatedPrice;
	}
	public String getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
}
