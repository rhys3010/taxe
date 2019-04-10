/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.booking;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.databinding.ListViewBookingNoteEntryBinding;

/**
 * BookingNotesListAdapter.java
 *
 * List adapter used to populate the recycler view to display a booking's notes
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class BookingNotesListAdapter extends RecyclerView.Adapter<BookingNotesListAdapter.ViewHolder> {

    private ArrayList<String> notes;

    /**
     * Default constructor to initialize notes list
     */
    public BookingNotesListAdapter(){
        notes = new ArrayList<>();
    }

    /**
     * Overloaded constructor to initailize adapter with a list of notes
     * @param notes
     */
    public BookingNotesListAdapter(ArrayList<String> notes){
        this.notes = notes;
    }

    /**
     * Method to populate the list with a new set of notes
     * @param notes
     */
    public void populateList(ArrayList<String> notes){
        this.notes = notes;
        notifyDataSetChanged();
    }

    /**
     * Add an entry into the notes list
     * @param note
     */
    public void add(String note){
        notes.add(note);
        notifyItemInserted(notes.size() - 1);
    }

    /**
     * Returns the amount of items in the list
     * @return
     */
    @Override
    public int getItemCount(){
        return notes.size();
    }

    /**
     * Create the note entry's view using view holder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public @NonNull BookingNotesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        // Create a new booking entry view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_booking_note_entry, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Called when the view holder is bound to replace contents of the entry
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position){
        if(notes != null){
            viewHolder.bind(notes.get(position), position);
        }
    }

    /**
     * Static inner class for the recycler view's view holder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ListViewBookingNoteEntryBinding dataBinding;

        /**
         * Constructor to create view holder with data binder
         * @param dataBinding
         */
        ViewHolder(ListViewBookingNoteEntryBinding dataBinding){
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
         * Bind the contents of the note to the view
         * @param note
         * @param position
         */
        void bind(@NonNull String note, int position){
            dataBinding.setNote(note);
            dataBinding.setPosition(String.valueOf(position+1));
        }
    }
}
