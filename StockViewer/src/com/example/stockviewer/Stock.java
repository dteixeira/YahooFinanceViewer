package com.example.stockviewer;

import java.io.Serializable;
import java.util.Date;

public class Stock implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String tick = null;
	private String name = null;
	private Date date = null;
	private int nStocks = 0;
	private double stockValue = 0;
	private double totalValue = 0;
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

	public int getnStocks() {
		return nStocks;
	}

	public void setnStocks(int nShares) {
		this.nStocks = nShares;
	}
	public double getStockValue() {
		return stockValue;
	}

	public void setStockValue(double shareValue) {
		this.stockValue = shareValue;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(double totalValue) {
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
