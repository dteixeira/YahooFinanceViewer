package org.cmov.stockviewer.requests;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class HttpRequestAsyncTask extends AsyncTask<Void, Void, Void> {

	/**
	 * Constants.
	 */
	private static final String TAG = HttpRequestAsyncTask.class.getSimpleName();

	/**
	 * Instance variables.
	 */
	private ProgressDialog progressDialog = null;
	private Activity callingActivity = null;
	private List<NameValuePair> requestData = null;
	private String requestUrl = null;
	private String progressMessage = null;
	private String requestResult = null;
	private int requestCode = 0;

	/**
	 * Creates a new async task instance.
	 */
	public HttpRequestAsyncTask(HttpRequestResultCallback activity,
			List<NameValuePair> requestData,
			String requestUrl,
			String progressMessage,
			int requestCode) {
		this.callingActivity = (Activity) activity;
		this.requestData = requestData;
		this.requestUrl = requestUrl;
		this.progressMessage = progressMessage;
		this.requestCode = requestCode;
		this.progressDialog = new ProgressDialog(this.callingActivity);
	}

	/**
	 * Sets up a progress dialog.
	 */
	@Override
	protected void onPreExecute() {

		// Show a progress modal dialog.
		progressDialog.setMessage(this.progressMessage);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	/**
	 * Accesses the REST remote API.
	 */
	@Override
	protected Void doInBackground(Void... params) {

		HttpContext localContext = new BasicHttpContext();
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			if(this.requestData != null) {
				this.requestUrl += "?" + URLEncodedUtils.format(this.requestData, "utf-8");
			}
			HttpGet get = new HttpGet(new URI(this.requestUrl));
			response = client.execute(get, localContext);
		} catch (Exception e) {
			Log.e(TAG, "Get failed.", e);
		}
		if(response != null) {
			try {
				this.requestResult = EntityUtils.toString(response.getEntity());
			} catch (Exception e) {
				Log.e(TAG, "No response.", e);
			}
		}
		return null;
	}

	/**
	 * Closes the progress dialog and informs the activity that
	 * the task ended.
	 */
	@Override
	protected void onPostExecute(Void result) {

		try {
			// Dismiss the progress dialog.
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {}

		// Callback the task executor.
		((HttpRequestResultCallback) callingActivity).onRequestResult(
				requestResult != null,
				requestResult,
				requestCode);
	}

}