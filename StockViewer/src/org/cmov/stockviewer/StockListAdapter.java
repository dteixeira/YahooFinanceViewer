package org.cmov.stockviewer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import com.example.stockviewer.R;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
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

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		if(position > 0) {
			Stock stock = stocks.get(position - 1);
			view = inflater.inflate(R.layout.stock_list_row, null);
			
			// Find fields.
			TextView tick = (TextView) view.findViewById(R.id.stock_tick);
			TextView name = (TextView) view.findViewById(R.id.stock_name);
			TextView date = (TextView) view.findViewById(R.id.stock_date);
			TextView stockValue = (TextView) view.findViewById(R.id.stock_stock_value);
			TextView change = (TextView) view.findViewById(R.id.stock_change);
			TextView changePercentage = (TextView) view.findViewById(R.id.stock_change_percentage);
			TextView nStocks = (TextView) view.findViewById(R.id.stock_n_stocks);
			TextView totalValue = (TextView) view.findViewById(R.id.stock_total_value);
			ImageButton standardButton = (ImageButton) view.findViewById(R.id.stock_show_graph_button);
			ImageButton graphButton = (ImageButton) view.findViewById(R.id.stock_show_standard_button);
			
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
			graphButton.setOnClickListener(getGraphOnClickListener(stock));
			
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
			}
		} else {
			view = inflater.inflate(R.layout.stock_list_first_row, null);
		}
		
		return view;
	}
	
	private OnClickListener getStandardOnClickListener(final Stock stock) {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final View view = (View) v.getParent().getParent().getParent();
				final View graph = ((View) view.getParent()).findViewById(R.id.stock_graph_layout);
				((TextView) graph.findViewById(R.id.stock_name_graph)).setText(stock.getName());
				((TextView) graph.findViewById(R.id.stock_tick_graph)).setText(stock.getTick());
				((TextView) graph.findViewById(R.id.stock_date_graph)).setText(new SimpleDateFormat("HH:mm, MMMM d", Locale.US).format(new Date()));
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
					view.setVisibility(View.GONE);
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
		}
	}

}
