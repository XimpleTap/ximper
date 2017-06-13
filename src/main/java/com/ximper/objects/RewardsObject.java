package com.ximper.objects;

public class RewardsObject {
	private int rewardId;
	private String businessName;
	private double requiredPoints;
	private String rewardName;
	private int rewardCountLimit;
	private int maxSingleClaim;
	
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public double getRequiredPoints() {
		return requiredPoints;
	}
	public void setRequiredPoints(double requiredPoints) {
		this.requiredPoints = requiredPoints;
	}
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
	public int getRewardCountLimit() {
		return rewardCountLimit;
	}
	public void setRewardCountLimit(int rewardCountLimit) {
		this.rewardCountLimit = rewardCountLimit;
	}
	public int getMaxSingleClaim() {
		return maxSingleClaim;
	}
	public void setMaxSingleClaim(int maxSingleClaim) {
		this.maxSingleClaim = maxSingleClaim;
	}
}
