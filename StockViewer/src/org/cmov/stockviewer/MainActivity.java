package org.cmov.stockviewer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.cmov.stockviewer.Stock.StockView;
import org.cmov.stockviewer.TaskFragment.TaskCallbacks;
import org.cmov.stockviewer.animations.CustomSwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements TaskCallbacks {
	
	private static final String SAVE_FILE = "StocksSave";
	
	private UndoBarController mUndoBarController = null;
	private StockListAdapter mStockListAdapter = null;
	private StockListCallbacks mStockListCallbacks = null;
	private BaseAdapter mAnimationAdapter = null;
	private ListView stockList = null;
	private TaskFragment mTaskFragment = null;
	private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Create progress dialog.
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Retrieving data");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        
        // Set HTTP request fragment.
        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag("task");
        if (mTaskFragment == null) {
        	mTaskFragment = new TaskFragment();
        	fm.beginTransaction().add(mTaskFragment, "task").commit();
        }
        
        // Launch the progress dialog if a task is running.
        if(mTaskFragment.isRunning()) {
        	onPreExecute();
        }
        
        // Set stock list.
        stockList = (ListView) findViewById(R.id.stock_list);
        stockList.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        setupAdapters(savedInstanceState);
    }
    
    @SuppressWarnings("unchecked")
	private void setupAdapters(Bundle savedInstanceState) {
    	if(savedInstanceState == null) {
    		try {
	    		FileInputStream used = openFileInput(SAVE_FILE);
	            ObjectInputStream oUsed = new ObjectInputStream(used);
	            ArrayList<Stock> stocks = (ArrayList<Stock>) oUsed.readObject();
	            oUsed.close();
	            used.close();
	            if(stocks == null) {
	            	stocks = new ArrayList<Stock>();
	            } else {
	            	for(Stock stock : stocks) {
	            		stock.setStockView(StockView.STANDARD);
	            		stock.setSpecialStockViewChange(false);
	            	}
	            }
	    		mStockListAdapter = new StockListAdapter(this, getApplicationContext(), stocks);
    		} catch(Exception e) {
    			mStockListAdapter = new StockListAdapter(this, getApplicationContext(), new ArrayList<Stock>());
    		}
    	} else {
    		mStockListAdapter = new StockListAdapter(this, getApplicationContext(), new ArrayList<Stock>());
    	}
        mStockListCallbacks = new StockListCallbacks(getApplicationContext());
        mUndoBarController = new UndoBarController(findViewById(R.id.undobar), mStockListCallbacks);
        mAnimationAdapter = new CustomSwipeDismissAdapter(mStockListAdapter, mStockListCallbacks);
        mAnimationAdapter = new SwingBottomInAnimationAdapter(mAnimationAdapter);
        ((SwingBottomInAnimationAdapter) mAnimationAdapter).setAbsListView(stockList);
        stockList.setAdapter(mAnimationAdapter);
        mStockListCallbacks.setmListAdapter(mAnimationAdapter);
        mStockListCallbacks.setmStockListAdapter(mStockListAdapter);
        mStockListCallbacks.setmUndoBarController(mUndoBarController);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUndoBarController.onSaveInstanceState(outState);
        mStockListAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUndoBarController.onRestoreInstanceState(savedInstanceState);
        mStockListAdapter.onRestoreInstanceState(savedInstanceState);
        mStockListAdapter.notifyDataSetChanged();
        ((SwingBottomInAnimationAdapter) mAnimationAdapter).setShouldAnimateFromPosition(0);
    }
    
    public void startUpdate() {
    	ArrayList<String> list = new ArrayList<String>();
    	for(Stock stock : mStockListAdapter.getItems()) {
    		list.add(stock.getTick());
    	}
    	mTaskFragment.startUpdate(list);
    }

	@Override
	public void onPreExecute() {
		mProgressDialog.show();
	}

	@Override
	public void onProgressUpdate(int percent) {}

	@Override
	public void onCancelled() {}

	@SuppressWarnings("unchecked")
	@Override
	public void onPostExecute(Object object) {
		// Update stocks.
		HashMap<String, String> result = (HashMap<String, String>) object;
		Date date = new Date();
		boolean failed = false;
		ArrayList<Stock> items = mStockListAdapter.getItems();
		for(String key : result.keySet()) {
			Iterator<Stock> it = items.iterator();
			while(it.hasNext()) {
				Stock stock = it.next();
				if(stock.getTick().equals(key)) {
					String [] parts = result.get(key).split(";");
					try {
						stock.updateStock(parts[0], date);
						stock.updateHistoricData(parts[1], date);
					} catch(Exception e) {
						failed = true;
						it.remove();
					}
				}
			}
		}
		mStockListAdapter.notifyDataSetChanged();
		mProgressDialog.hide();
		((SwingBottomInAnimationAdapter) mAnimationAdapter).setShouldAnimateFromPosition(0);
		if(failed) {
			Toast.makeText(getApplicationContext(), "Sorry, it was not possible to add the requested stock.", Toast.LENGTH_SHORT).show(); 
		}
	}
	
	@Override
	protected void onStop() {
		try {
			FileOutputStream osRecent = openFileOutput(SAVE_FILE, MODE_PRIVATE);
	        ObjectOutputStream oosRecent = new ObjectOutputStream(osRecent);
	        oosRecent.writeObject(mStockListAdapter.getItems());
	        oosRecent.close();
	        osRecent.close();
		} catch (Exception e) {}
		super.onStop();
	}
}
