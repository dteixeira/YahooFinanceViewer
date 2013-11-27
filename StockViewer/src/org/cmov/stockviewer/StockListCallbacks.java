package org.cmov.stockviewer;

import org.cmov.stockviewer.UndoBarController.UndoListener;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import com.example.stockviewer.R;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;

public class StockListCallbacks implements OnDismissCallback, UndoListener {
	
	private BaseAdapter mListAdapter = null;
	private StockListAdapter mStockListAdapter = null;
	private UndoBarController mUndoBarController = null;
	private Context context = null;
	
	public StockListCallbacks(Context context) {
		this.context = context;
	}

	public BaseAdapter getmListAdapter() {
		return mListAdapter;
	}

	public void setmListAdapter(BaseAdapter mListAdapter) {
		this.mListAdapter = mListAdapter;
	}

	public StockListAdapter getmStockListAdapter() {
		return mStockListAdapter;
	}

	public void setmStockListAdapter(StockListAdapter mStockListAdapter) {
		this.mStockListAdapter = mStockListAdapter;
	}

	public UndoBarController getmUndoBarController() {
		return mUndoBarController;
	}

	public void setmUndoBarController(UndoBarController mUndoBarController) {
		this.mUndoBarController = mUndoBarController;
	}

	@Override
	public void onUndo(Parcelable token) {
		int position = mStockListAdapter.onUndo(token);
		mListAdapter.notifyDataSetChanged();
		if(position >= 0) {
			((SwingRightInAnimationAdapter) mListAdapter).setShouldAnimateFromPosition(position);
		}
	}

	@Override
	public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
		int position = reverseSortedPositions.length > 0 ? reverseSortedPositions[0] : -1;
		Intent intent = new Intent();
		intent.putExtra(StockListAdapter.EXTRA_STOCK_LIST_REMOVE_POSITION, position);
		mStockListAdapter.removeItem(position);
		mUndoBarController.hideUndoBar(true);
		mUndoBarController.showUndoBar(false, context.getString(R.string.undo_stock_remove), intent);
		mListAdapter.notifyDataSetChanged();
	}

}
