package org.cmov.stockviewer.animations;

import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.haarman.listviewanimations.ArrayAdapter;
import com.haarman.listviewanimations.BaseAdapterDecorator;

/**
 * Adds an option to swipe items in a ListView away. This does nothing more than
 * setting a new SwipeDismissListViewTouchListener to the ListView.
 */
public class CustomSwipeDismissAdapter extends BaseAdapterDecorator {

	protected CustomOnDismissCallback mCallback;
	protected CustomSwipeDismissListViewTouchListener mSwipeDismissListViewTouchListener;

	protected CustomSwipeOnScrollListener mOnScroll;

	public CustomSwipeDismissAdapter(BaseAdapter baseAdapter, CustomOnDismissCallback callback) {
		// add a default OnScrollListener
		this(baseAdapter, callback, new CustomSwipeOnScrollListener());
	}

	public CustomSwipeDismissAdapter(BaseAdapter baseAdapter, CustomOnDismissCallback callback, CustomSwipeOnScrollListener onScroll) {
		super(baseAdapter);
		mCallback = callback;
		mOnScroll = onScroll;
	}

	/**
	 * Override-able 
	 * @param listView
	 * @return SwipeDismissListViewTouchListener
	 */
	protected CustomSwipeDismissListViewTouchListener createListViewTouchListener(AbsListView listView) {
		return new CustomSwipeDismissListViewTouchListener(listView, mCallback, mOnScroll);
	}

	@Override
	public void setAbsListView(AbsListView listView) {
		super.setAbsListView(listView);
		if (mDecoratedBaseAdapter instanceof ArrayAdapter<?>) {
			// fix #35 dirty trick !
			// if ArrayAdapter we assume that items manipulation will come from it
			((ArrayAdapter<?>)mDecoratedBaseAdapter).propagateNotifyDataSetChanged(this);
		}
		mSwipeDismissListViewTouchListener = createListViewTouchListener(listView);
		mSwipeDismissListViewTouchListener.setIsParentHorizontalScrollContainer(isParentHorizontalScrollContainer());
		mSwipeDismissListViewTouchListener.setTouchChild(getTouchChild());
		listView.setOnTouchListener(mSwipeDismissListViewTouchListener);
	}

	@Override
	public void setIsParentHorizontalScrollContainer(boolean isParentHorizontalScrollContainer) {
		super.setIsParentHorizontalScrollContainer(isParentHorizontalScrollContainer);
		if (mSwipeDismissListViewTouchListener != null) {
			mSwipeDismissListViewTouchListener.setIsParentHorizontalScrollContainer(isParentHorizontalScrollContainer);
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (mSwipeDismissListViewTouchListener != null) {
			mSwipeDismissListViewTouchListener.notifyDataSetChanged();
		}
	}

	@Override
	public void setTouchChild(int childResId) {
		super.setTouchChild(childResId);
		if (mSwipeDismissListViewTouchListener != null) {
			mSwipeDismissListViewTouchListener.setTouchChild(childResId);
		}
	}
}