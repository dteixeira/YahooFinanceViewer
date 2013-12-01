package org.cmov.stockviewer;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.achartengine.model.TimeSeries;
import org.cmov.stockviewer.utils.HistoricData;

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
	private ArrayList<HistoricData> historicData = new ArrayList<HistoricData>();
	private boolean specialStockViewChange = false;
	
	public static enum StockView {
		STANDARD,
		CHART,
		SETTINGS,
		NAVIGATION,
		LOADING
	}

	public void updateStock(String csv, Date nDate) {
		String[] tokens = csv.split(",");
		for(int i = 0; i < tokens.length; ++i) {
			tokens[i] = tokens[i].replace("\"", "");
		}
		if(tokens.length > 5) {
			tokens[1] += tokens[2];
			tokens[2] = tokens[3];
			tokens[3] = tokens[4];
			tokens[4] = tokens[5];
		}
		specialStockViewChange = false;
		stockMultiplier = 1;
		name = tokens[0];
		tick = tokens[1];
		stockValue = Double.parseDouble(tokens[2]);
		changePositive = tokens[3].contains("+");
		change = Double.parseDouble(tokens[3].replace("+", "").replace("-", ""));
		changePercentage = Double.parseDouble(tokens[4].replace("+", "").replace("-", "").replace("%", ""));
		date = nDate;
		totalValue = nStocks * stockValue;
	}
	
	public void updateHistoricData(String csv, Date date) throws NumberFormatException, ParseException {
		String[] tokens = csv.split("\n");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		ArrayList<HistoricData> temp = new ArrayList<HistoricData>();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.US);
		String today = fmt.format(date);
		for(int i = 1; i < tokens.length; ++i) {
			String[] values = tokens[i].split(",");
			HistoricData hd = new HistoricData(df.parse(values[0]), Double.parseDouble(values[4]));
			String day = fmt.format(hd.getDate());
			
			// Update values.
			if(today.equals(day)) {
				hd.setnStocks(this.nStocks);
			} else {
				for(HistoricData h : historicData) {
					if(fmt.format(h.getDate()).equals(day)) {
						hd.setnStocks(h.getnStocks());
					}
				}
			}
			
			// Add new value.
			temp.add(hd);
		}
		historicData = temp;
	}
	
	public TimeSeries getHistoricDataAsTimeSeries() {
		TimeSeries series = new TimeSeries("");
		for(HistoricData pair : historicData) {
			series.add(pair.getDate(), pair.getValue());
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

	public ArrayList<HistoricData> getHistoricData() {
		return historicData;
	}

	public void setHistoricData(ArrayList<HistoricData> historicData) {
		this.historicData = historicData;
	}

	public boolean isSpecialStockViewChange() {
		return specialStockViewChange;
	}

	public void setSpecialStockViewChange(boolean specialStockViewChange) {
		this.specialStockViewChange = specialStockViewChange;
	}

}
