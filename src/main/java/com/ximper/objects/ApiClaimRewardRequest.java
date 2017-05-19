package com.ximper.objects;

import java.util.List;

public class ApiClaimRewardRequest {
	private String cardNumber;
	private int businessId;
	private int cashierId;
	
	private List<RewardToClaim> rewardsToClaim;
	public List<RewardToClaim> getRewardsToClaim() {
		return rewardsToClaim;
	}
	public void setRewardsToClaim(List<RewardToClaim> rewardsToClaim) {
		this.rewardsToClaim = rewardsToClaim;
	}
	public int getBusinessId() {
		return businessId;
	}
	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}
	
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public int getCashierId() {
		return cashierId;
	}
	public void setCashierId(int cashierId) {
		this.cashierId = cashierId;
	}
	
}
