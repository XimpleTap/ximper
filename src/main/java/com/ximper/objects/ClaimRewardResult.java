package com.ximper.objects;

import java.util.List;

public class ClaimRewardResult {
	private int totalItemsClaimed;
	private int totalEstimatedPrice;
	private int totalPoints;
	private List<ClaimBreakdown> claimBreakdown;
	public int getTotalItemsClaimed() {
		return totalItemsClaimed;
	}
	public void setTotalItemsClaimed(int totalItemsClaimed) {
		this.totalItemsClaimed = totalItemsClaimed;
	}
	public int getTotalEstimatedPrice() {
		return totalEstimatedPrice;
	}
	public void setTotalEstimatedPrice(int totalEstimatedPrice) {
		this.totalEstimatedPrice = totalEstimatedPrice;
	}
	public int getTotalPoints() {
		return totalPoints;
	}
	public void setTotalPoints(int totalPoints) {
		this.totalPoints = totalPoints;
	}
	public List<ClaimBreakdown> getClaimBreakdown() {
		return claimBreakdown;
	}
	public void setClaimBreakdown(List<ClaimBreakdown> claimBreakdown) {
		this.claimBreakdown = claimBreakdown;
	}
	
	
}
