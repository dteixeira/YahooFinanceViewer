package org.cmov.stockviewer.utils;

import java.io.Serializable;
import java.util.Date;

public class HistoricData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Date date = null;
	private double value = 0.0;
	private int nStocks = 0;
	
	public HistoricData() {
	}
	
	public HistoricData(Date date, double value) {
		this.date = date;
		this.value = value;
	}
	
	public HistoricData(Date date, double value, int nStocks) {
		this.date = date;
		this.value = value;
		this.nStocks = nStocks;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getnStocks() {
		return nStocks;
	}

	public void setnStocks(int nStocks) {
		this.nStocks = nStocks;
	}

}
