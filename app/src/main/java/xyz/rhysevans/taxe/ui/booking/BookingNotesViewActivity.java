/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.booking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

    /**
     * Called when the activity is created to initialize all the UI elements
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_notes_view);
    }
}
