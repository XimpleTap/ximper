package com.ximper.objects;

import java.util.List;

public class AcquireProductRequest {
	
	private int cashierId;
	private String transactionTime;
	private List<ProductToAcquire> productsToAcquire;
	
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
	public List<ProductToAcquire> getProductsToAcquire() {
		return productsToAcquire;
	}
	public void setProductsToAcquire(List<ProductToAcquire> productsToAcquire) {
		this.productsToAcquire = productsToAcquire;
	}
}
