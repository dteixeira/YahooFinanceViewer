package org.cmov.stockviewer.requests;

public interface HttpRequestResultCallback {

	public void onRequestResult(boolean result, String data, int requestCode);

}