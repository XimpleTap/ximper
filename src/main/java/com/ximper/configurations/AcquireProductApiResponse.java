package com.ximper.configurations;

import java.util.List;

import com.ximper.objects.AcquireProductResult;

public class AcquireProductApiResponse {
	private String status;
	private String statusCode;
	private String transactionType;
	private List<AcquireProductResult> apiData;
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
	public List<AcquireProductResult> getApiData() {
		return apiData;
	}
	public void setApiData(List<AcquireProductResult> apiData) {
		this.apiData = apiData;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

}