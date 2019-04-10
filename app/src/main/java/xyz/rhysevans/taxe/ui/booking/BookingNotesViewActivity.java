/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.booking;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.model.Response;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;
import xyz.rhysevans.taxe.viewmodel.BookingViewModel;

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
    private View view;

    private ArrayList<String> notes;
    private String bookingId;

    private BookingNotesListAdapter bookingNotesListAdapter;

    private CompositeSubscription subscriptions;
    private BookingViewModel bookingViewModel;
    private ErrorHandler errorHandler;
    private SharedPreferencesManager sharedPreferencesManager;

    /**
     * Called when the activity is created to initialize all the UI elements
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_notes_view);

        subscriptions = new CompositeSubscription();
        bookingViewModel = new BookingViewModel();
        errorHandler = new ErrorHandler();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        
        if(savedInstanceState != null){
            notes = savedInstanceState.getStringArrayList(BookingOverviewFragment.BOOKING_NOTES_KEY);
        }

        // Get info from arguments
        Bundle args = getIntent().getExtras();
        if(args != null){
            notes = args.getStringArrayList(BookingOverviewFragment.BOOKING_NOTES_KEY);
            bookingId = args.getString(BookingOverviewFragment.BOOKING_ID_KEY);

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
        fab.setOnClickListener(v -> showNoteInputDialog());

        // Root view
        view = findViewById(android.R.id.content);

        // Initialize List
        notesList = findViewById(R.id.notes_list);
        bookingNotesListAdapter = new BookingNotesListAdapter(notes);
        notesList.setAdapter(bookingNotesListAdapter);
        notesList.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Show the dialog for the user to input their new note
     */
    private void showNoteInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.new_note));

        // Create the Edit Text element to place in the dialog
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            // Add new note to list
            String newNote = input.getText().toString();
            if(!StringUtils.isEmpty(newNote) && newNote.trim().length() > 0){
                notes.add(newNote);
                // Send request to API
                addNote(newNote);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Change button colors
        Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getColor(R.color.colorPrimary));
        Button negativeButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(getColor(R.color.colorPrimary));
    }

    /**
     * Send a request to the API to update the booking's notes
     * @param note - The note to add
     */
    private void addNote(String note){
        // Lock Screen Orientation
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        }

        // Create booking object to send with PATCH request
        Booking booking = new Booking();
        booking.setNote(note);

        // Send HTTP request
        subscriptions.add(bookingViewModel.editBooking(sharedPreferencesManager.getToken(), bookingId, booking)
            .subscribe(this::handleSuccess, this::handleError));
    }

    /**
     * Handle successful update of the booking's notes
     * @param response
     */
    private void handleSuccess(Response response){
        // Show snackbar message
        Snackbar.make(view, R.string.note_successfully_added, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Handle error in updating the booking's notes
     * @param error
     */
    private void handleError(Throwable error){
        errorHandler.handle(error, this, view);
    }

    /**
     * Called just before fragment is removed to save instance state
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(BookingOverviewFragment.BOOKING_NOTES_KEY, notes);
    }
}
