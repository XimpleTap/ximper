package com.ximper.objects;

public class TopUpResultObject {
	private double balanceBeforeReload;
	private double balanceAfterReload;
	private int pointsBeforeReload;
	private int pointsAfterReload;
	private int loadAmount;
	
	public double getBalanceBeforeReload() {
		return balanceBeforeReload;
	}
	public void setBalanceBeforeReload(double balanceBeforeReload) {
		this.balanceBeforeReload = balanceBeforeReload;
	}
	public double getBalanceAfterReload() {
		return balanceAfterReload;
	}
	public void setBalanceAfterReload(double balanceAfterReload) {
		this.balanceAfterReload = balanceAfterReload;
	}
	public int getPointsBeforeReload() {
		return pointsBeforeReload;
	}
	public void setPointsBeforeReload(int pointsBeforeReload) {
		this.pointsBeforeReload = pointsBeforeReload;
	}
	public int getPointsAfterReload() {
		return pointsAfterReload;
	}
	public void setPointsAfterReload(int pointsAfterReload) {
		this.pointsAfterReload = pointsAfterReload;
	}
	public int getLoadAmount() {
		return loadAmount;
	}
	public void setLoadAmount(int loadAmount) {
		this.loadAmount = loadAmount;
	}
	
}
