package org.cmov.stockviewer.animations;

import android.widget.AbsListView;
import android.widget.ListView;

/**
 * The callback interface used by {@link SwipeDismissListViewTouchListener} to
 * inform its client about a successful dismissal of one or more list item
 * positions.
 */
public interface CustomOnDismissCallback {
        /**
         * Called when the user has indicated they she would like to dismiss one or
         * more list item positions.
         * 
         * @param listView
         *            The originating {@link ListView}.
         * @param reverseSortedPositions
         *            An array of positions to dismiss, sorted in descending order
         *            for convenience.
         */
        void onDismiss(AbsListView listView, int[] reverseSortedPositions);
        
        boolean isDismissable(int position);
}