package org.cmov.stockviewer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class TaskFragment extends Fragment {

	static interface TaskCallbacks {
		void onPreExecute();
		void onProgressUpdate(int percent);
		void onCancelled();
		void onPostExecute(Object object);
	}

	private static final String TAG = TaskFragment.class.getSimpleName();
	private TaskCallbacks mCallbacks;
	private ArrayList<String> stocks = null;
	private int stocksIndex = 0;
	private AtomicBoolean running = new AtomicBoolean(false);
	private HashMap<String, String> results = new HashMap<String, String>();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (TaskCallbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	public boolean isRunning() {
		return running.get();
	}

	public void setRunning(boolean running) {
		this.running.set(running);
	}
	
	public void startUpdate(ArrayList<String> stocks) {
		// Only start a new update if the last one finished.
		if(!running.get() && stocks.size() > 0) {
			running.set(true);
			stocksIndex = 0;
			this.stocks = stocks;
			new QuotesTask().execute((Void[]) null);
		}
	}
	
	private class HistoricQuotesTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// Build parameters.
			String requestUrl = "http://ichart.yahoo.com/table.csv";
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("s", stocks.get(stocksIndex)));
			paramList.add(new BasicNameValuePair("g", "d"));
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			paramList.add(new BasicNameValuePair("e", "" + c.get(Calendar.DAY_OF_MONTH)));
			paramList.add(new BasicNameValuePair("d", "" + c.get(Calendar.MONTH)));
			paramList.add(new BasicNameValuePair("f", "" + c.get(Calendar.YEAR)));
			c.add(Calendar.DAY_OF_MONTH, -30);
			paramList.add(new BasicNameValuePair("b", "" + c.get(Calendar.DAY_OF_MONTH)));
			paramList.add(new BasicNameValuePair("a", "" + c.get(Calendar.MONTH)));
			paramList.add(new BasicNameValuePair("c", "" + c.get(Calendar.YEAR)));
			
			// Make request.
			HttpContext localContext = new BasicHttpContext();
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = null;
			try {
				if(paramList != null) {
					requestUrl += "?" + URLEncodedUtils.format(paramList, "utf-8");
				}
				HttpGet get = new HttpGet(new URI(requestUrl));
				response = client.execute(get, localContext);
			} catch (Exception e) {
				Log.e(TAG, "Get failed.", e);
			}
			if(response != null) {
				try {
					return EntityUtils.toString(response.getEntity());
				} catch (Exception e) {
					Log.e(TAG, "No response.", e);
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result == null) {
				running.set(false);
				return;
			} else {
				String stock = stocks.get(stocksIndex);
				String prevValue = results.get(stock);
				results.put(stock, prevValue + ";" + result);
				++stocksIndex;
				
				// All requests made, return results.
				if(stocksIndex == stocks.size()) {
					mCallbacks.onPostExecute(results);
					running.set(false);
				} else {
					new HistoricQuotesTask().execute((Void[]) null);
				}
			}
		}
	}
	
	private class QuotesTask extends AsyncTask<Void, Void, String> {
		
		@Override
		protected void onPreExecute() {
			mCallbacks.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			// Build parameters.
			String requestUrl = "http://finance.yahoo.com/d/quotes.csv";
			String ticks = stocks.get(0);
			for(int i = 1; i < stocks.size(); ++i) {
				ticks += "," + stocks.get(i);
			}
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("s", ticks));
			paramList.add(new BasicNameValuePair("f", "n0s0l1c1p2"));
			
			HttpContext localContext = new BasicHttpContext();
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = null;
			try {
				if(paramList != null) {
					requestUrl += "?" + URLEncodedUtils.format(paramList, "utf-8");
				}
				HttpGet get = new HttpGet(new URI(requestUrl));
				response = client.execute(get, localContext);
			} catch (Exception e) {
				Log.e(TAG, "Get failed.", e);
			}
			if(response != null) {
				try {
					return EntityUtils.toString(response.getEntity());
				} catch (Exception e) {
					Log.e(TAG, "No response.", e);
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result == null) {
				running.set(false);
				return;
			} else {
				// Parse results.
				String[] parts = result.split("\n");
				for(int i = 0; i < stocks.size(); ++i) {
					results.put(stocks.get(i), parts[i]);
				}
				
				// Start new requests.
				new HistoricQuotesTask().execute((Void[]) null);
			}
		}
	}
}