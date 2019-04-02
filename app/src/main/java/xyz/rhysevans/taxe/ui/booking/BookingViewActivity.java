/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.booking;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import xyz.rhysevans.taxe.R;

/**
 * BookingViewActivity.java
 *
 * View an individual booking by loading the BookingOverviewFragment
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class BookingViewActivity extends AppCompatActivity {

    private String bookingId;

    /**
     * Called when the activity is created to load the fragment etc.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_view);

        // Get booking ID from bundle
        Bundle args = getIntent().getExtras();

        // Create new booking overview fragment and pass the booking ID..
        BookingOverviewFragment bookingOverviewFragment = new BookingOverviewFragment();
        if(args != null){
            bookingOverviewFragment.setArguments(args);
            bookingId = args.getString(BookingOverviewFragment.BOOKING_ID_KEY);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.booking_fragment_container, bookingOverviewFragment);
        ft.commit();

        initToolbar();
    }

    /**
     * Initialize the activity's toolbar
     */
    private void initToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set title
        TextView toolbarTitle = findViewById(R.id.title);
        toolbarTitle.setText(bookingId);

        // Remove Submit Button
        ImageView submitBtn = findViewById(R.id.submit_btn);
        submitBtn.setVisibility(View.GONE);

        // Replace Close Button with back button
        ImageView closeBtn = findViewById(R.id.close_btn);
        closeBtn.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        // Add behaviour to back button
        closeBtn.setOnClickListener(v -> {
            finish();
        });

    }
}
