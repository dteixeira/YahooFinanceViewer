package com.example.stockviewer;

import java.io.Serializable;
import java.util.Date;

public class Stock implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String tick = null;
	private String name = null;
	private Date date = null;
	private int nShares = 0;
	private int shareValue = 0;
	private int totalValue = 0;
	private double change = 0.0;
	private double changePercentage = 0.0;

	public String getTick() {
		return tick;
	}
	public void setTick(String tick) {
		this.tick = tick;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getnShares() {
		return nShares;
	}
	public void setnShares(int nShares) {
		this.nShares = nShares;
	}
	public int getShareValue() {
		return shareValue;
	}
	public void setShareValue(int shareValue) {
		this.shareValue = shareValue;
	}
	public int getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(int totalValue) {
		this.totalValue = totalValue;
	}
	public double getChange() {
		return change;
	}
	public void setChange(double change) {
		this.change = change;
	}
	public double getChangePercentage() {
		return changePercentage;
	}
	public void setChangePercentage(double changePercentage) {
		this.changePercentage = changePercentage;
	}

}
