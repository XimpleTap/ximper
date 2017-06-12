package com.ximper.objects;

import java.util.List;

public class AcquireProductRequest {
	
	private String transactionTime;
	private List<ProductToAcquire> productsToAcquire;
	
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
