package xyz.rhysevans.taxe.util;

import android.view.View;

/**
 * RecyclerViewItemClickListener.java
 *
 * Custom item click listener interface for recycler view
 *
 * @author Rhys Evans
 * @version 0.1
 */
public interface RecyclerViewItemClickListener {

    /**
     * Called when an item is clicked, to receive item's position in list
     * and the item's view.
     * @param v
     * @param position
     */
    public void onItemClick(View v, int position);
}
