package com.ximper.configurations;

public class ApiResponse {
	private String status;
	private String statusCode;
	private Object apiData;
	private String transactionType;
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
	public Object getApiData() {
		return apiData;
	}
	public void setApiData(Object apiData) {
		this.apiData = apiData;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
}