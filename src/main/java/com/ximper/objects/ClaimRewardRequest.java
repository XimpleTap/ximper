package com.ximper.objects;

import java.util.List;

public class ClaimRewardRequest {
	private int cashierId;
	private String transactionTime;
	private List<RewardToClaim> rewardsToClaim;
	
	public List<RewardToClaim> getRewardsToClaim() {
		return rewardsToClaim;
	}
	public void setRewardsToClaim(List<RewardToClaim> rewardsToClaim) {
		this.rewardsToClaim = rewardsToClaim;
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
