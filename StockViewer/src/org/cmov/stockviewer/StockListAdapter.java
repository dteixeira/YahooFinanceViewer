package org.cmov.stockviewer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.example.stockviewer.R;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class StockListAdapter extends BaseAdapter {

	public static final String EXTRA_STOCK_LIST_REMOVE_POSITION = "StockListRemovePosition";
	public static final String SAVED_UNDO_INDEX = "UndoIndex";
	public static final String SAVED_UNDO_STOCK = "UndoStock";
	public static final String SAVED_STOCKS = "Stocks";
	
	private LayoutInflater inflater = null;
	private Context context = null;
	private ArrayList<Stock> stocks = null;
	private Stock undoStock = null;
	private int undoIndex = 0;
	
	public StockListAdapter(Context context, ArrayList<Stock> stocks) {
		this.context = context;
		this.stocks = stocks;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return stocks == null ? 0 : stocks.size() + 1;
	}

	@Override
	public Object getItem(int index) {
		return stocks == null ? null : stocks.get(index - 1);
	}
	
	private void getStandardView(Stock stock, View view) {
		// Find views.
		TextView date = (TextView) view.findViewById(R.id.stock_date);
		TextView stockValue = (TextView) view.findViewById(R.id.stock_stock_value);
		TextView change = (TextView) view.findViewById(R.id.stock_change);
		TextView changePercentage = (TextView) view.findViewById(R.id.stock_change_percentage);
		TextView nStocks = (TextView) view.findViewById(R.id.stock_n_stocks);
		TextView totalValue = (TextView) view.findViewById(R.id.stock_total_value);
		
		// Set values.
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		date.setText(new SimpleDateFormat("HH:mm, MMMM d", Locale.US).format(stock.getDate()));
		stockValue.setText(nf.format(stock.getStockValue()));
		change.setText(nf.format(stock.getChange()));
		changePercentage.setText(nf.format(stock.getChangePercentage()) + "%");
		totalValue.setText(nf.format(stock.getTotalValue()));
		nStocks.setText(NumberFormat.getNumberInstance(Locale.US).format(stock.getnStocks()));
		
		// Change colors.
		if(stock.isChangePositive()) {
			change.setTextColor(context.getResources().getColor(R.color.color_stock_change_positive));
			changePercentage.setTextColor(context.getResources().getColor(R.color.color_stock_change_positive));
		} else {
			change.setTextColor(context.getResources().getColor(R.color.color_stock_change_negative));
			changePercentage.setTextColor(context.getResources().getColor(R.color.color_stock_change_negative));
		}
	}
	
	private void getChartView(Stock stock, View view) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.stock_chart_layout);
		XYMultipleSeriesDataset series = new XYMultipleSeriesDataset();
		TimeSeries serie = new TimeSeries("");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		serie.add(c.getTime(), 400);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 500);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 550);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 440);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 560);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 500);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 400);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 500);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 550);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 440);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 560);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 500);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 400);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 500);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 550);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 440);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 560);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 500);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		serie.add(c.getTime(), 445);
		c.add(Calendar.DATE, 1);
		series.addSeries(serie);
		XYMultipleSeriesRenderer renderers = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setLineWidth(5);
		renderer.setColor(Color.parseColor("#5d8aa9"));
		renderers.setLabelsTextSize(30);
		renderers.addSeriesRenderer(renderer);
		renderers.setYLabelsPadding(10);
		renderers.setYLabelsAlign(Align.RIGHT);
		renderers.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
		renderers.setShowLegend(false);
		renderers.setShowGridX(true);
		renderers.setGridColor(Color.parseColor("#5d8aa9"));
		renderers.setXLabelsColor(Color.parseColor("#333333"));
		renderers.setYLabelsColor(0, Color.parseColor("#333333"));
		renderers.setMargins(new int[] { 50, 80, 0, 20 });
		//renderers.setXAxisMax(31);
		renderers.setPanEnabled(false);
		GraphicalView mChartView = ChartFactory.getTimeChartView(context, series, renderers, "dd/MM");
        layout.addView(mChartView);
	}
	
	private void getSettingsView(Stock stock, View view) {

		// Find views.
		TextView nStocks = ((TextView) view.findViewById(R.id.stock_n_stocks_settings));
		SeekBar multiplierSeekBar = ((SeekBar) view.findViewById(R.id.stock_multiplier_seek));
		SeekBar nStocksSeekBar = ((SeekBar) view.findViewById(R.id.stock_n_stocks_seek));
		
		// Set values.
		switch(stock.getStockMultiplier()) {
			case 1:
				multiplierSeekBar.setProgress(0);
				break;
			case 100:
				multiplierSeekBar.setProgress(1);
				break;
			case 10000:
				multiplierSeekBar.setProgress(2);
				break;
			case 1000000:
				multiplierSeekBar.setProgress(3);
				break;
		}
		nStocks.setText(NumberFormat.getNumberInstance(Locale.US).format(stock.getnStocks()));
		nStocksSeekBar.setProgress(stock.getnStocks() / stock.getStockMultiplier() % 100);
		
		// Set listeners.
		multiplierSeekBar.setOnSeekBarChangeListener(getMultiplierSeekBarOnSeekBarChangeListener(stock));
		nStocksSeekBar.setOnSeekBarChangeListener(getnStocksSeekBarOnSeekBarChangeListener(stock));
	}
	
	private OnSeekBarChangeListener getMultiplierSeekBarOnSeekBarChangeListener(final Stock stock) {
		return new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// Find views.
				View parent = (View) seekBar.getParent();
				TextView nStocksSeekMin = ((TextView) parent.findViewById(R.id.stock_n_stocks_seek_min));
				TextView nStocksSeekMax = ((TextView) parent.findViewById(R.id.stock_n_stocks_seek_max));
				SeekBar nStocksSeekBar = ((SeekBar) parent.findViewById(R.id.stock_n_stocks_seek));
				
				// Set values.
				switch(progress) {
					case 0:
						stock.setStockMultiplier(1);
						break;
					case 1:
						stock.setStockMultiplier(100);
						break;
					case 2:
						stock.setStockMultiplier(10000);
						break;
					case 3:
						stock.setStockMultiplier(1000000);
						break;
				}
				nStocksSeekMin.setText("" + stock.getStockMultiplier());
				nStocksSeekMax.setText("" + (stock.getStockMultiplier() * 99));
				nStocksSeekBar.setProgress(stock.getnStocks() / stock.getStockMultiplier() % 100);
			}
		};
	}
	
	private OnSeekBarChangeListener getnStocksSeekBarOnSeekBarChangeListener(final Stock stock) {
		return new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// Only update on user input.
				if(fromUser) {
					// Find views.
					View parent = (View) seekBar.getParent().getParent();
					TextView nStocksSettings = ((TextView) parent.findViewById(R.id.stock_n_stocks_settings));
					TextView nStocks = ((TextView) parent.findViewById(R.id.stock_n_stocks));
					
					// Update stocks.
					int startInt = stock.getnStocks() / stock.getStockMultiplier() / 100 * (100 * stock.getStockMultiplier());
					int lastInt = stock.getnStocks() % stock.getStockMultiplier();
					stock.setnStocks(startInt + progress * stock.getStockMultiplier() + lastInt);
					stock.setTotalValue(stock.getStockValue() * stock.getnStocks());
					
					// Set values.
					nStocksSettings.setText(NumberFormat.getNumberInstance(Locale.US).format(stock.getnStocks()));
					nStocks.setText(NumberFormat.getNumberInstance(Locale.US).format(stock.getnStocks()));
				}
			}
		};
	}
	
	private void getNavigationView(Stock stock, View view) {
		// TODO
	}
	
	private void getDefaultView(Stock stock, View view) {
		// Find views.
		TextView tick = (TextView) view.findViewById(R.id.stock_tick);
		TextView name = (TextView) view.findViewById(R.id.stock_name);
		
		// Set values.
		tick.setText(stock.getTick());
		name.setText(stock.getName());
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		if(position > 0) {
			Stock stock = stocks.get(position - 1);
			view = inflater.inflate(R.layout.stock_list_row, null);
			getDefaultView(stock, view);
			getStandardView(stock, view);
			getChartView(stock, view);
			getSettingsView(stock, view);
			getNavigationView(stock, view);
			return view;
			
			/*// Find fields.
			TextView tick = (TextView) view.findViewById(R.id.stock_tick);
			TextView name = (TextView) view.findViewById(R.id.stock_name);
			TextView date = (TextView) view.findViewById(R.id.stock_date);
			TextView stockValue = (TextView) view.findViewById(R.id.stock_stock_value);
			TextView change = (TextView) view.findViewById(R.id.stock_change);
			TextView changePercentage = (TextView) view.findViewById(R.id.stock_change_percentage);
			TextView nStocks = (TextView) view.findViewById(R.id.stock_n_stocks);
			TextView totalValue = (TextView) view.findViewById(R.id.stock_total_value);
			ImageButton standardButton = (ImageButton) view.findViewById(R.id.stock_show_graph_button);
			//ImageButton graphButton = (ImageButton) view.findViewById(R.id.stock_show_standard_button);
			
			// Set fields' values.
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMinimumFractionDigits(2);
			nf.setMaximumFractionDigits(2);
			tick.setText(stock.getTick());
			name.setText(stock.getName());
			date.setText(new SimpleDateFormat("HH:mm, MMMM d", Locale.US).format(stock.getDate()));
			stockValue.setText(nf.format(stock.getStockValue()));
			change.setText(nf.format(stock.getChange()));
			changePercentage.setText(nf.format(stock.getChangePercentage()) + "%");
			totalValue.setText(nf.format(stock.getTotalValue()));
			nStocks.setText(NumberFormat.getNumberInstance(Locale.US).format(stock.getnStocks()));
			standardButton.setOnClickListener(getStandardOnClickListener(stock));
			//graphButton.setOnClickListener(getGraphOnClickListener(stock));
			
			LinearLayout layout = (LinearLayout) view.findViewById(R.id.stock_chart_layout);
			XYMultipleSeriesDataset series = new XYMultipleSeriesDataset();
			TimeSeries serie = new TimeSeries("");
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			serie.add(c.getTime(), 400);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 500);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 550);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 440);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 560);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 500);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 400);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 500);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 550);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 440);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 560);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 500);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 400);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 500);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 550);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 440);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 560);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 500);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			serie.add(c.getTime(), 445);
			c.add(Calendar.DATE, 1);
			series.addSeries(serie);
			XYMultipleSeriesRenderer renderers = new XYMultipleSeriesRenderer();
			XYSeriesRenderer renderer = new XYSeriesRenderer();
			renderer.setLineWidth(5);
			renderer.setColor(Color.parseColor("#5d8aa9"));
			renderers.setLabelsTextSize(30);
			renderers.addSeriesRenderer(renderer);
			renderers.setYLabelsPadding(10);
			renderers.setYLabelsAlign(Align.RIGHT);
			renderers.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
			renderers.setShowLegend(false);
			renderers.setShowGridX(true);
			renderers.setGridColor(Color.parseColor("#5d8aa9"));
			renderers.setXLabelsColor(Color.parseColor("#333333"));
			renderers.setYLabelsColor(0, Color.parseColor("#333333"));
			renderers.setMargins(new int[] { 50, 80, 0, 20 });
			//renderers.setXAxisMax(31);
			renderers.setPanEnabled(false);
			GraphicalView mChartView = ChartFactory.getTimeChartView(context, series, renderers, "dd/MM");
	        layout.addView(mChartView);
		    
			// Dirty trick to cheat view bug.
			if(stock.isFlipped()) {
				standardButton.performClick();
			}
			
			// Change colors.
			if(stock.isChangePositive()) {
				change.setTextColor(context.getResources().getColor(R.color.color_stock_change_positive));
				changePercentage.setTextColor(context.getResources().getColor(R.color.color_stock_change_positive));
			} else {
				change.setTextColor(context.getResources().getColor(R.color.color_stock_change_negative));
				changePercentage.setTextColor(context.getResources().getColor(R.color.color_stock_change_negative));
			}*/
		} else {
			// TODO SETUP FIRST ROW
			view = inflater.inflate(R.layout.stock_list_first_row, null);
		}
		
		return view;
	}
	
	/*private OnClickListener getStandardOnClickListener(final Stock stock) {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final View view = (View) v.getParent().getParent();
				final View graph = ((View) view.getParent()).findViewById(R.id.stock_settings_layout);
				/*((TextView) graph.findViewById(R.id.stock_name_graph)).setText(stock.getName());
				((TextView) graph.findViewById(R.id.stock_tick_graph)).setText(stock.getTick());
				((TextView) graph.findViewById(R.id.stock_n_stocks_graph)).setText(NumberFormat.getNumberInstance(Locale.US).format(stock.getnStocks()));
				SeekBar multiplierSeekBar = ((SeekBar) graph.findViewById(R.id.stock_multiplier_seek));
				SeekBar nStocksSeekBar = ((SeekBar) graph.findViewById(R.id.stock_n_stocks_seek));
				switch(stock.getStockMultiplier()) {
					case 1:
						multiplierSeekBar.setProgress(0);
						break;
					case 100:
						multiplierSeekBar.setProgress(1);
						break;
					case 10000:
						multiplierSeekBar.setProgress(2);
						break;
					case 1000000:
						multiplierSeekBar.setProgress(3);
						break;
				}
				nStocksSeekBar.setProgress(stock.getnStocks() / stock.getStockMultiplier() % 100);
				multiplierSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						switch(progress) {
							case 0:
								stock.setStockMultiplier(1);
								break;
							case 1:
								stock.setStockMultiplier(100);
								break;
							case 2:
								stock.setStockMultiplier(10000);
								break;
							case 3:
								stock.setStockMultiplier(1000000);
								break;
						}
						View parent = (View) seekBar.getParent();
						((TextView) parent.findViewById(R.id.stock_n_stocks_seek_min)).setText("" + stock.getStockMultiplier());
						((TextView) parent.findViewById(R.id.stock_n_stocks_seek_max)).setText("" + (stock.getStockMultiplier() * 99));
						((SeekBar) parent.findViewById(R.id.stock_n_stocks_seek)).setProgress(
								stock.getnStocks() / stock.getStockMultiplier() % 100);
					}
				});
				nStocksSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						if(fromUser) {
							int startInt = stock.getnStocks() / stock.getStockMultiplier() / 100 * (100 * stock.getStockMultiplier());
							int lastInt = stock.getnStocks() % stock.getStockMultiplier();
							stock.setnStocks(startInt + progress * stock.getStockMultiplier() + lastInt);
							stock.setTotalValue(stock.getStockValue() * stock.getnStocks());
							View parent = (View) seekBar.getParent();
							((TextView) parent.findViewById(R.id.stock_n_stocks_graph)).setText(NumberFormat.getNumberInstance(Locale.US).format(stock.getnStocks()));
						}
					}
				});
				
				// Flip animation.
				if(stock.isFlipped()) {
					view.setVisibility(View.VISIBLE);
					graph.setVisibility(View.VISIBLE);
				} else {
					stock.setFlipped(true);
					Animation animation = AnimationUtils.loadAnimation(context, R.anim.to_middle);
				    animation.setDuration(250);
				    view.setEnabled(false);
				    ((View) view.getParent()).clearAnimation();
		            animation.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {}
						
						@Override
						public void onAnimationRepeat(Animation animation) {}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							view.setVisibility(View.GONE);
							graph.setVisibility(View.VISIBLE);
							Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.from_middle);
						    animation1.setDuration(250);
						    ((View) view.getParent()).setAnimation(animation1);
						    ((View) view.getParent()).animate();
						}
					});
		            ((View) view.getParent()).setAnimation(animation);
		            ((View) view.getParent()).startAnimation(animation);
				}
			}
		};
	}
	
	private OnClickListener getGraphOnClickListener(final Stock stock) {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final View view = (View) v.getParent().getParent().getParent();
				final View standard = ((View) view.getParent()).findViewById(R.id.stock_standard_layout);
				((TextView) standard.findViewById(R.id.stock_n_stocks)).setText(NumberFormat.getNumberInstance(Locale.US).format(stock.getnStocks()));
				((TextView) standard.findViewById(R.id.stock_total_value)).setText(NumberFormat.getNumberInstance(Locale.US).format(stock.getTotalValue()));
				
				// Flip animation.
				stock.setFlipped(false);
				Animation animation = AnimationUtils.loadAnimation(context, R.anim.to_middle);
			    animation.setDuration(250);
			    view.setEnabled(false);
			    ((View) view.getParent()).clearAnimation();
	            animation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {}
					
					@Override
					public void onAnimationRepeat(Animation animation) {}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						view.setVisibility(View.GONE);
						standard.setVisibility(View.VISIBLE);
						Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.from_middle);
					    animation1.setDuration(250);
					    ((View) view.getParent()).setAnimation(animation1);
					    ((View) view.getParent()).animate();
					}
				});
	            ((View) view.getParent()).setAnimation(animation);
	            ((View) view.getParent()).startAnimation(animation);
			}
		};
	}*/

	@Override
	public long getItemId(int index) {
		return index;
	}

	public int onUndo(Parcelable token) {
		int position = token == null ? -1 : ((Intent) token).getIntExtra(EXTRA_STOCK_LIST_REMOVE_POSITION, -1);
		
		// Valid undo command.
		if(position == undoIndex) {
			stocks.add(undoIndex, undoStock);
			undoIndex = -1;
			undoStock = null;
		}
		return position;
	}
	
	public void removeItem(int index) {
		if(index >= 0 && index < stocks.size()) {
			undoIndex = index;
			undoStock = stocks.remove(index);
		}
	}
	
	public void addItem(Stock stock) {
		stocks.add(stock);
	}
	
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(SAVED_UNDO_INDEX, undoIndex);
		outState.putSerializable(SAVED_UNDO_STOCK, undoStock);
		outState.putSerializable(SAVED_STOCKS, stocks);
	}
	
	@SuppressWarnings("unchecked")
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			undoIndex = savedInstanceState.getInt(SAVED_UNDO_INDEX);
			undoStock = (Stock) savedInstanceState.getSerializable(SAVED_UNDO_STOCK);
			stocks = (ArrayList<Stock>) savedInstanceState.getSerializable(SAVED_STOCKS);
		}
	}

}
