package org.cmov.stockviewer.animations;

import android.widget.AbsListView;

public class CustomSwipeOnScrollListener implements AbsListView.OnScrollListener {

	CustomSwipeDismissListViewTouchListener mTouchListener;

	public void setTouchListener(CustomSwipeDismissListViewTouchListener touchListener) {
		mTouchListener = touchListener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			mTouchListener.disallowSwipe();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}
}