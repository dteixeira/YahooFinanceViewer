package org.cmov.stockviewer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.cmov.stockviewer.animations.CustomSwipeDismissAdapter;
import org.cmov.stockviewer.requests.HttpRequestAsyncTask;
import org.cmov.stockviewer.requests.HttpRequestResultCallback;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements HttpRequestResultCallback {
	
	private UndoBarController mUndoBarController = null;
	private StockListAdapter mStockListAdapter = null;
	private StockListCallbacks mStockListCallbacks = null;
	private BaseAdapter mAnimationAdapter = null;
	private ListView stockList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Set stock list.
        stockList = (ListView) findViewById(R.id.stock_list);
        stockList.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        setupAdapters(savedInstanceState);
    }
    
    private void setupAdapters(Bundle savedInstanceState) {
    	if(savedInstanceState == null) {
    		mStockListAdapter = new StockListAdapter(getApplicationContext(), getDummyStocks(10));
    	} else {
    		mStockListAdapter = new StockListAdapter(getApplicationContext(), new ArrayList<Stock>());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
    }
    
    // TODO REMOVE
    private ArrayList<Stock> getDummyStocks(int n) {
    	ArrayList<Stock> list = new ArrayList<Stock>();
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("s", "GOOG,MSFT,AAPL,DELL"));
		paramList.add(new BasicNameValuePair("f", "n0s0l1c1p2"));
		HttpRequestAsyncTask task =  new HttpRequestAsyncTask(
				this, 
				paramList, 
				"http://finance.yahoo.com/d/quotes.csv", 
				"Me liga vai", 
				1);
		task.execute((Void[]) null);
    	return list;
    }

	@Override
	public void onRequestResult(boolean result, String data, int requestCode) {
		if(result) {
			String[] lines = data.split("\n");
			for(String line : lines) {
				if(!line.equals("")) {
					Stock stock = new Stock();
					stock.setnStocks(134654);
					stock.updateStock(line);
					mStockListAdapter.addItem(stock);
					mAnimationAdapter.notifyDataSetInvalidated();
				}
			}
		}
	}
}
