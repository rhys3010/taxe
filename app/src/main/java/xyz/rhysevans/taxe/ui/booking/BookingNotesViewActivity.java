/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.booking;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import xyz.rhysevans.taxe.R;

/**
 * BookingNotesViewActivity.java
 *
 * Activity to view all notes associated with a booking and to create new notes
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class BookingNotesViewActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView notesList;

    private ArrayList<String> notes;
    private BookingNotesListAdapter bookingNotesListAdapter;

    /**
     * Called when the activity is created to initialize all the UI elements
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_notes_view);

        // Get notes array from arguments
        Bundle args = getIntent().getExtras();
        if(args != null){
            notes = args.getStringArrayList(BookingOverviewFragment.BOOKING_NOTES_KEY);
            // Reverse array
            Collections.reverse(notes);
        }

        initToolbar();
        initViews();
    }

    /**
     * Initialize the activity's toolbar
     */
    private void initToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set Title
        TextView toolarTitle = findViewById(R.id.title);
        toolarTitle.setText(getString(R.string.booking_notes));

        // Remove Submit Button
        ImageView submitBtn = findViewById(R.id.submit_btn);
        submitBtn.setVisibility(View.GONE);

        // Replace Close Button with back button
        ImageView closeBtn = findViewById(R.id.close_btn);
        closeBtn.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        // Add behaviour to back button
        closeBtn.setOnClickListener(v -> {
            // Set the result of the exit to force refresh
            setResult(BookingOverviewFragment.BOOKING_NOTES_REQUEST_CODE);
            finish();
        });
    }

    /**
     * Initialize all the remaining UI elements
     */
    private void initViews(){
        fab = findViewById(R.id.fab);

        // Initialize List
        notesList = findViewById(R.id.notes_list);
        bookingNotesListAdapter = new BookingNotesListAdapter(notes);
        notesList.setAdapter(bookingNotesListAdapter);
        notesList.setLayoutManager(new LinearLayoutManager(this));
    }
}
