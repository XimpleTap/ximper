package com.ximper.configurations;

import com.ximper.objects.InquiryObject;

public class InquiryApiResponse {
	private String status;
	private String statusCode;
	private String transactionType;
	private InquiryObject apiData;
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
	public InquiryObject getApiData() {
		return apiData;
	}
	public void setApiData(InquiryObject apiData) {
		this.apiData = apiData;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
}