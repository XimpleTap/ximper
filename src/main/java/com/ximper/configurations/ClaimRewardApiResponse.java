package com.ximper.configurations;

import java.util.List;

import com.ximper.objects.AcquireProductResult;
import com.ximper.objects.ClaimRewardResult;

public class ClaimRewardApiResponse {
	private String status;
	private String statusCode;
	private String transactionType;
	private List<ClaimRewardResult> apiData;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public List<ClaimRewardResult> getApiData() {
		return apiData;
	}
	public void setApiData(List<ClaimRewardResult> apiData) {
		this.apiData = apiData;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

}