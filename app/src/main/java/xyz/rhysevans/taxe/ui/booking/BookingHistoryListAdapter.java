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
import xyz.rhysevans.taxe.databinding.ListViewBookingEntryBinding;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.util.RecyclerViewItemClickListener;

/**
 * BookingHistoryListAdapter.java
 *
 * List adapter used to populate the recycler view within booking history
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class BookingHistoryListAdapter extends RecyclerView.Adapter<BookingHistoryListAdapter.ViewHolder> {

    private ArrayList<Booking> bookings;
    private RecyclerViewItemClickListener itemClickListener;

    /**
     * Default Constructor to initialize empty booking list
     */
    public BookingHistoryListAdapter(){
        bookings = new ArrayList<>();

    }

    /**
     * Overloaded Constructor to initialize bookings list
     * @param bookings
     */
    public BookingHistoryListAdapter(ArrayList<Booking> bookings){
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
     * Create the individual entry's views using view holder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public @NonNull BookingHistoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        // Create a new booking entry view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_booking_entry, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position){
        if(bookings != null){
            viewHolder.bind(bookings.get(position));
        }
    }


    /**
     * Static inner class for the recycler view's view holder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ListViewBookingEntryBinding dataBinding;

        /**
         * Constructor to create view holder with data binder
         * @param dataBinding
         */
        ViewHolder(ListViewBookingEntryBinding dataBinding){
            this(dataBinding.getRoot());
            this.dataBinding = dataBinding;
        }

        /**
         * Default Constructor
         * @param itemView
         */
        ViewHolder(@NonNull View itemView) {
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
