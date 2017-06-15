package com.ximper.objects;

public class ClaimBreakdown {
	private String rewardName;
	private int rewardId;
	private int rewardCount;
	private int rewardEstimatedPrice;
	private int totalEstimatedPrice;
	private int rewardPoints;
	private int pointsUsedToClaim;
	public String getRewardName() {
		return rewardName;
	}
	public void setRewardName(String rewardName) {
		this.rewardName = rewardName;
	}
	public int getRewardId() {
		return rewardId;
	}
	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}
	public int getRewardCount() {
		return rewardCount;
	}
	public void setRewardCount(int rewardCount) {
		this.rewardCount = rewardCount;
	}
	public int getRewardEstimatedPrice() {
		return rewardEstimatedPrice;
	}
	public void setRewardEstimatedPrice(int rewardEstimatedPrice) {
		this.rewardEstimatedPrice = rewardEstimatedPrice;
	}
	public int getTotalEstimatedPrice() {
		return totalEstimatedPrice;
	}
	public void setTotalEstimatedPrice(int totalEstimatedPrice) {
		this.totalEstimatedPrice = totalEstimatedPrice;
	}
	public int getRewardPoints() {
		return rewardPoints;
	}
	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
	public int getPointsUsedToClaim() {
		return pointsUsedToClaim;
	}
	public void setPointsUsedToClaim(int pointsUsedToClaim) {
		this.pointsUsedToClaim = pointsUsedToClaim;
	}
	
}
