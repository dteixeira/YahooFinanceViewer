package org.cmov.stockviewer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.cmov.stockviewer.Stock.StockView;
import org.cmov.stockviewer.utils.HistoricData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class StockListAdapter extends BaseAdapter {

	public static final String EXTRA_STOCK_LIST_REMOVE_POSITION = "StockListRemovePosition";
	public static final String SAVED_UNDO_INDEX = "UndoIndex";
	public static final String SAVED_UNDO_STOCK = "UndoStock";
	public static final String SAVED_STOCKS = "Stocks";

	private Activity owner = null;
	private View portfolioView = null;
	private LayoutInflater inflater = null;
	private Context context = null;
	private ArrayList<Stock> stocks = null;
	private Stock undoStock = null;
	private int undoIndex = 0;

	public StockListAdapter(Activity owner, Context context, ArrayList<Stock> stocks) {
		this.context = context;
		this.stocks = stocks;
		this.owner = owner;
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
		// Find view.
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.stock_chart_layout);

		// Build chart data sets.
		XYMultipleSeriesDataset series = new XYMultipleSeriesDataset();
		TimeSeries values = stock.getHistoricDataAsTimeSeries();
		series.addSeries(values);
		TimeSeries offset = new TimeSeries("");
		offset.add(values.getMinX(), stock.getStockValue());
		offset.add(values.getMaxX(), stock.getStockValue());
		series.addSeries(offset);

		// Build and setup chart renderer.
		XYMultipleSeriesRenderer fullRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setLineWidth(context.getResources().getDimension(R.dimen.stock_graph_line_width));
		renderer.setColor(Color.parseColor("#5d8aa9"));
		XYSeriesRenderer offsetRenderer = new XYSeriesRenderer();
		offsetRenderer.setLineWidth(context.getResources().getDimension(R.dimen.stock_graph_line_width));
		offsetRenderer.setColor(Color.parseColor("#02a020"));
		fullRenderer.setLabelsTextSize(context.getResources().getDimension(R.dimen.stock_graph_label_text_size));
		fullRenderer.addSeriesRenderer(renderer);
		fullRenderer.addSeriesRenderer(offsetRenderer);
		fullRenderer.setYLabelsPadding(context.getResources().getDimension(R.dimen.stock_graph_y_labels_padding));
		fullRenderer.setYLabelsAlign(Align.RIGHT);
		fullRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
		fullRenderer.setShowLegend(false);
		fullRenderer.setShowGridX(true);
		fullRenderer.setGridColor(Color.parseColor("#5d8aa9"));
		fullRenderer.setXLabelsColor(Color.parseColor("#333333"));
		fullRenderer.setYLabelsColor(0, Color.parseColor("#333333"));
		fullRenderer.setMargins(new int[] {
				(int) context.getResources().getDimension(R.dimen.stock_graph_margin_top),
				(int) context.getResources().getDimension(R.dimen.stock_graph_margin_left),
				(int) context.getResources().getDimension(R.dimen.stock_graph_margin_bottom),
				(int) context.getResources().getDimension(R.dimen.stock_graph_margin_right)

		});
		fullRenderer.setPanEnabled(false);

		// Create and set chart.
		GraphicalView mChartView = ChartFactory.getTimeChartView(context, series, fullRenderer, "dd/MM");
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
					TextView totalValue = ((TextView) parent.findViewById(R.id.stock_total_value));

					// Update stocks.
					int startInt = stock.getnStocks() / stock.getStockMultiplier() / 100 * (100 * stock.getStockMultiplier());
					int lastInt = stock.getnStocks() % stock.getStockMultiplier();
					stock.setnStocks(startInt + progress * stock.getStockMultiplier() + lastInt);
					stock.setTotalValue(stock.getStockValue() * stock.getnStocks());

					// Set values.
					NumberFormat nf = NumberFormat.getInstance();
					nf.setMinimumFractionDigits(2);
					nf.setMaximumFractionDigits(2);
					nStocksSettings.setText(NumberFormat.getNumberInstance(Locale.US).format(stock.getnStocks()));
					nStocks.setText(NumberFormat.getNumberInstance(Locale.US).format(stock.getnStocks()));
					totalValue.setText(nf.format(stock.getTotalValue()));
					SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.US);
					String d = fmt.format(stock.getDate());
					for(HistoricData hd : stock.getHistoricData()) {
						if(d.equals(fmt.format(hd.getDate()))) {
							hd.setnStocks(stock.getnStocks());
							break;
						}
					}

					// Update the global view.
					getPortfolioView(portfolioView);
					getPortfolioChart(portfolioView);
				}
			}
		};
	}

	private void getNavigationView(final Stock stock, View view) {
		// Find views.
		Button standardLayoutButton = (Button) view.findViewById(R.id.stock_navigation_standard_layout);
		Button chartLayoutButton = (Button) view.findViewById(R.id.stock_navigation_chart_layout);
		Button settingsLayoutButton = (Button) view.findViewById(R.id.stock_navigation_settings_layout);
		final ImageButton navigationButton = (ImageButton) view.findViewById(R.id.stock_show_navigation_button);

		// Set listeners.
		standardLayoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stock.setStockView(StockView.STANDARD);
				stock.setSpecialStockViewChange(true);
				navigationButton.performClick();
			}
		});
		chartLayoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stock.setStockView(StockView.CHART);	
				stock.setSpecialStockViewChange(true);
				navigationButton.performClick();
			}
		});
		settingsLayoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stock.setStockView(StockView.SETTINGS);
				stock.setSpecialStockViewChange(true);	
				navigationButton.performClick();
			}
		});
	}

	private void getDefaultView(Stock stock, View view) {
		// Find views.
		TextView tick = (TextView) view.findViewById(R.id.stock_tick);
		TextView name = (TextView) view.findViewById(R.id.stock_name);
		ImageButton navigationButton = (ImageButton) view.findViewById(R.id.stock_show_navigation_button);

		// Set values.
		tick.setText(stock.getTick());
		name.setText(stock.getName());

		// Set listeners.
		navigationButton.setOnClickListener(getNavigationButtonOnClickListener(stock));
	}

	private OnClickListener getNavigationButtonOnClickListener(final Stock stock) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Already on navigation, return to standard view.
				if(stock.getStockView() == StockView.NAVIGATION) {
					stock.setStockView(StockView.STANDARD);
				} else if(stock.isSpecialStockViewChange()) {
					stock.setSpecialStockViewChange(false);
				} else {
					stock.setStockView(StockView.NAVIGATION);
				}

				// Set animation listener.
				View card = (View) v.getParent().getParent().getParent();
				Animation flipOutAnimation = AnimationUtils.loadAnimation(context, R.anim.to_middle);
				flipOutAnimation.setAnimationListener(getFlipOutAnimationListener(card, stock));
				card.clearAnimation();
				card.setAnimation(flipOutAnimation);
				card.animate();
			}
		};
	}

	private AnimationListener getFlipOutAnimationListener(final View view, final Stock stock) {
		return new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				setViewVisibility(view, stock);
				Animation flipInAnimation = AnimationUtils.loadAnimation(context, R.anim.from_middle);
				view.clearAnimation();
				view.setAnimation(flipInAnimation);
				view.animate();
			}
		};
	}

	public void setViewVisibility(View view, Stock stock) {
		// Find views.
		View standardView = view.findViewById(R.id.stock_standard_layout);
		View chartView = view.findViewById(R.id.stock_chart_layout);
		View settingsView = view.findViewById(R.id.stock_settings_layout);
		View navigationView = view.findViewById(R.id.stock_navigation_layout);
		//View loadingView = view.findViewById(R.id.stock_loading_layout);

		// Hide views.
		standardView.setVisibility(View.GONE);
		chartView.setVisibility(View.GONE);
		settingsView.setVisibility(View.GONE);
		navigationView.setVisibility(View.GONE);
		//loadingView.setVisibility(View.GONE);

		// Show needed view.
		switch(stock.getStockView()) {
		case CHART:
			chartView.setVisibility(View.VISIBLE);
			break;
		case LOADING:
			//loadingView.setVisibility(View.VISIBLE);
			break;
		case NAVIGATION:
			navigationView.setVisibility(View.VISIBLE);
			break;
		case SETTINGS:
			settingsView.setVisibility(View.VISIBLE);
			break;
		case STANDARD:
			standardView.setVisibility(View.VISIBLE);
			break;
		}
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
			setViewVisibility(view, stock);
			return view;
		} else {
			view = inflater.inflate(R.layout.stock_list_first_row, null);
			portfolioView = view;
			getPortfolioView(view);
		}

		return view;
	}

	private void getPortfolioView(View view) {
		// Generate values.
		int totalStocks = 0;
		double totalValue = 0.0;
		for(Stock stock : stocks) {
			totalStocks += stock.getnStocks();
			totalValue += stock.getTotalValue();
		}

		// Find views.
		TextView nStocks = (TextView) view.findViewById(R.id.stock_first_n_stocks);
		TextView value = (TextView) view.findViewById(R.id.stock_first_total_value);
		TextView date = (TextView) view.findViewById(R.id.stock_first_date);

		// Set values.
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		value.setText(nf.format(totalValue));
		nStocks.setText(NumberFormat.getNumberInstance(Locale.US).format(totalStocks));
		Date uDate = stocks.size() > 0 ? stocks.get(0).getDate() : new Date();
		date.setText(new SimpleDateFormat("HH:mm, MMMM d", Locale.US).format(uDate));
		getPortfolioChart(view);
		getPortfolioListeners(view);
	}

	private void getPortfolioListeners(View view) {
		// Find views.
		ImageButton updateButton = (ImageButton) view.findViewById(R.id.stock_update_button);
		ImageButton addButton = (ImageButton) view.findViewById(R.id.stock_add_button);

		// Add listeners.
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(owner);
				builder.setTitle("Add stocks");
				final View view = owner.getLayoutInflater().inflate(R.layout.dialog_add_stock, null);
				builder.setView(view);
				builder.setNegativeButton("Cancel", null);
				builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

					@SuppressLint("DefaultLocale")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Get input values.
						EditText customStockEditText = (EditText) view.findViewById(R.id.stock_tick_edittext);
						Spinner stockSpinner = (Spinner) view.findViewById(R.id.stock_tick_spinner);
						String customTick = customStockEditText.getText().toString().toUpperCase(Locale.US).trim();
						if("".equals(customTick)) {
							customTick = stockSpinner.getSelectedItem().toString();
						}

						// Check uniqueness.
						for(Stock stock : stocks) {
							if(stock.getTick().equals(customTick)) {
								Toast.makeText(context, "Sorry, that stock has already been added.", Toast.LENGTH_SHORT).show();
								return;
							}
						}

						// Send request.
						addNewStock(customTick);
					}
				});
				builder.show();
			}
		});
		updateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) owner).startUpdate();
			}
		});
	}

	private void addNewStock(String tick) {
		Stock stock = new Stock();
		stock.setTick(tick);
		stocks.add(stock);
		((MainActivity) owner).startUpdate();
	}

	private void getPortfolioChart(View view) {
		// Create data.
		// TODO START USING CORRECT VALUES
		HashMap<Date, Double> values = new HashMap<Date, Double>();
		for(Stock stock : stocks) {
			ArrayList<HistoricData> individual = stock.getHistoricData();
			for(HistoricData pair : individual) {
				Double value = values.get(pair.getDate());
				values.put(pair.getDate(), (value == null ? 0.0 : value) + pair.getValue() * pair.getnStocks());
			}
		}

		// Hide views if no data.
		View chartView = view.findViewById(R.id.stock_first_chart_layout);
		View noDataView = view.findViewById(R.id.stock_first_no_data_layout);
		if(values.size() > 0) {
			noDataView.setVisibility(View.GONE);
			chartView.setVisibility(View.VISIBLE);
		} else {
			noDataView.setVisibility(View.VISIBLE);
			chartView.setVisibility(View.GONE);
		}

		// Compute average and normalize values.
		double average = 0.0;
		double total = 0.0;
		for(Double v : values.values()) {
			if(v > 0) {
				average += v;
				total += 1.0;
			}
		}
		average /= total;
		average /= 10000.0;
		int avg = (int) average;
		int magnitude = 0;
		while(avg > 0) { 
			magnitude++; 
			avg /= 10; 
		};
		double normalizer = Math.pow(10, magnitude);
		for(Date d : values.keySet()) {
			Double v = values.get(d);
			values.put(d, v / normalizer);
		}
		TextView scale = (TextView) view.findViewById(R.id.stock_first_scale);
		scale.setText("x" + NumberFormat.getNumberInstance(Locale.US).format(((int) normalizer)));

		// Convert to time series.
		TimeSeries timeSeries = new TimeSeries("");
		for (Date key : values.keySet()) {
			timeSeries.add(key, values.get(key));
		}

		// Find view.
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.stock_first_chart);

		// Build chart data sets.
		XYMultipleSeriesDataset series = new XYMultipleSeriesDataset();
		series.addSeries(timeSeries);

		// Build and setup chart renderer.
		XYMultipleSeriesRenderer fullRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setLineWidth(context.getResources().getDimension(R.dimen.stock_graph_line_width));
		renderer.setColor(Color.parseColor("#5d8aa9"));
		fullRenderer.setLabelsTextSize(context.getResources().getDimension(R.dimen.stock_graph_label_text_size));
		fullRenderer.addSeriesRenderer(renderer);
		fullRenderer.setYLabelsPadding(context.getResources().getDimension(R.dimen.stock_graph_y_labels_padding));
		fullRenderer.setYLabelsAlign(Align.RIGHT);
		fullRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
		fullRenderer.setShowLegend(false);
		fullRenderer.setShowGridX(true);
		fullRenderer.setGridColor(Color.parseColor("#5d8aa9"));
		fullRenderer.setXLabelsColor(Color.parseColor("#333333"));
		fullRenderer.setYLabelsColor(0, Color.parseColor("#333333"));
		fullRenderer.setMargins(new int[] {
				(int) context.getResources().getDimension(R.dimen.stock_graph_margin_top),
				(int) context.getResources().getDimension(R.dimen.stock_graph_margin_left),
				(int) context.getResources().getDimension(R.dimen.stock_graph_margin_bottom),
				(int) context.getResources().getDimension(R.dimen.stock_graph_margin_right)

		});
		fullRenderer.setPanEnabled(false);

		// Create and set chart.
		GraphicalView mChartView = ChartFactory.getTimeChartView(context, series, fullRenderer, "dd/MM");
		layout.removeAllViews();
		layout.addView(mChartView);


	}

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
			for(Stock stock : stocks) {
				stock.setSpecialStockViewChange(false);
			}
		}
	}

	public ArrayList<Stock> getItems() {
		return stocks;
	}

}
