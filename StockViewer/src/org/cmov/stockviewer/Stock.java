package org.cmov.stockviewer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.achartengine.model.TimeSeries;

import android.util.Pair;

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
	private boolean changePositive = false;
	private int stockMultiplier = 1;
	private StockView stockView = StockView.STANDARD;
	private ArrayList<Pair<Date, Double>> historicData = new ArrayList<Pair<Date,Double>>();
	
	public static enum StockView {
		STANDARD,
		CHART,
		SETTINGS,
		NAVIGATION
	}

	public void updateStock(String csv) {
		String[] tokens = csv.split(",");
		for(int i = 0; i < tokens.length; ++i) {
			tokens[i] = tokens[i].replace("\"", "");
		}
		stockMultiplier = 1;
		name = tokens[0];
		tick = tokens[1];
		stockValue = Double.parseDouble(tokens[2]);
		changePositive = tokens[3].contains("+");
		change = Double.parseDouble(tokens[3].replace("+", "").replace("-", ""));
		changePercentage = Double.parseDouble(tokens[4].replace("+", "").replace("-", "").replace("%", ""));
		date = new Date();
		totalValue = nStocks * stockValue;
	}
	
	public void updateHistoricData(String csv) {
		// TODO
	}
	
	public TimeSeries getHistoricDataAsTimeSeries() {
		TimeSeries series = new TimeSeries("");
		for(Pair<Date, Double> pair : historicData) {
			series.add(pair.first, pair.second);
		}
		return series;
	}
	
	public boolean isChangePositive() {
		return changePositive;
	}

	public void setChangePositive(boolean isChangePositive) {
		this.changePositive = isChangePositive;
	}

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

	public StockView getStockView() {
		return stockView;
	}

	public void setStockView(StockView stockView) {
		this.stockView = stockView;
	}
	
	public int getStockMultiplier() {
		return stockMultiplier;
	}

	public void setStockMultiplier(int stockMultiplier) {
		this.stockMultiplier = stockMultiplier;
	}

	public ArrayList<Pair<Date, Double>> getHistoricData() {
		return historicData;
	}

	public void setHistoricData(ArrayList<Pair<Date, Double>> historicData) {
		this.historicData = historicData;
	}

}
