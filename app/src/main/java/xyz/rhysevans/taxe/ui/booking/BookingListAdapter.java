/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.booking;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.databinding.ListViewActiveBookingsEntryBinding;
import xyz.rhysevans.taxe.databinding.ListViewBookingHistoryEntryBinding;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.util.RecyclerViewItemClickListener;

/**
 * BookingListAdapter.java
 *
 * List adapter used to populate booking history recycler view
 * and active bookings recycler view
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class BookingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Booking> bookings;
    private RecyclerViewItemClickListener itemClickListener;
    // Flag to indicate which view should be used to display the list
    // (history view or active bookings view)
    private boolean activeBookingsView;

    /**
     * Default Constructor to initialize empty booking list and retrieve the 'active' flag
     */
    public BookingListAdapter(boolean activeBookingsView){
        bookings = new ArrayList<>();
        this.activeBookingsView = activeBookingsView;
    }

    /**
     * Overloaded Constructor to initialize bookings list
     * @param bookings
     */
    public BookingListAdapter(ArrayList<Booking> bookings){
        this.bookings = bookings;
    }

    /**
     * Populate the list of bookings
     * @param bookings
     */
    public void populateList(ArrayList<Booking> bookings){
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    /**
     * Add a booking to the list
     * @param booking
     */
    public void add(Booking booking){
        bookings.add(booking);
        notifyItemInserted(bookings.size() - 1);
    }

    /**
     * Returns the number of items in the list view
     * @return
     */
    @Override
    public int getItemCount(){
        return bookings.size();
    }

    /**
     * Returns the unique ID of the item
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position){
        return bookings.get(position).getUniqueId();
    }

    /**
     * Initialize the adapter's item click listener
     * @param itemClickListener
     */
    public void setItemClickListener(RecyclerViewItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    /**
     * Create the individual entry's views using the correct view holder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public @NonNull RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        // Create a new booking entry view
        View view;
        RecyclerView.ViewHolder viewHolder;

        // Decide which ViewHolder to use based on activeBookings flag
        if(activeBookingsView){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_active_bookings_entry, parent, false);
            viewHolder = new ViewHolderActive(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_booking_history_entry, parent, false);
            viewHolder = new ViewHolderHistory(view);
        }

        // Initialize the onClickListener for the view
        if(itemClickListener != null){
            view.setOnClickListener(v -> {
                itemClickListener.onItemClick(v, viewHolder.getAdapterPosition());
            });
        }

        return viewHolder;
    }

    /**
     * Called when the view holder is bound to replace the contents of the
     * booking entry
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position){
        // Use the correct data binder based on the ViewHolder initialized
        if(bookings != null){
            if(activeBookingsView){
                ViewHolderActive viewHolderActive = (ViewHolderActive)viewHolder;
                viewHolderActive.bind(bookings.get(position));

            }else{
                ViewHolderHistory viewHolderHistory = (ViewHolderHistory)viewHolder;
                viewHolderHistory.bind(bookings.get(position));
            }
        }
    }


    /**
     * Static inner class for the recycler view's HISTORY view holder
     */
    static class ViewHolderHistory extends RecyclerView.ViewHolder {
        ListViewBookingHistoryEntryBinding dataBinding;

        /**
         * Default Constructor
         * @param itemView
         */
        ViewHolderHistory(@NonNull View itemView) {
            super(itemView);
            dataBinding = DataBindingUtil.bind(itemView);
        }

        /**
         * Bind the contents of the bookings to the view
         * @param booking
         */
        void bind(@NonNull Booking booking){
            dataBinding.setBooking(booking);
        }
    }

    /**
     * Static inner class for the recycler view's ACTIVE view holder
     */
    static class ViewHolderActive extends RecyclerView.ViewHolder {
        ListViewActiveBookingsEntryBinding dataBinding;

        /**
         * Default Constructor
         * @param itemView
         */
        ViewHolderActive(@NonNull View itemView) {
            super(itemView);
            dataBinding = DataBindingUtil.bind(itemView);
        }

        /**
         * Bind the contents of the bookings to the view
         * @param booking
         */
        void bind(@NonNull Booking booking){
            dataBinding.setBooking(booking);
        }
    }
}
