/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.booking;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.text.WordUtils;

import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.databinding.FragmentBookingOverviewBinding;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.model.Response;
import xyz.rhysevans.taxe.ui.authentication.LoginFragment;
import xyz.rhysevans.taxe.util.BookingStatus;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;
import xyz.rhysevans.taxe.viewmodel.BookingViewModel;
import xyz.rhysevans.taxe.viewmodel.UserViewModel;

/**
 * BookingOverviewFragment.java
 *
 * Manage the booking overview screen. By getting info through API using View Model
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class BookingOverviewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = LoginFragment.class.getSimpleName();
    public static final String BOOKING_ID_KEY = "BOOKING_ID_KEY";

    // A potential booking ID to display
    private String id;

    // The container for the empty booking view
    private FrameLayout emptyBookingContainer;

    // The Container for the populate booking view
    private CardView activeBookingContainer;

    private View progressIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Cancel Button
    private TextView cancelBtn;
    // Notes Button
    private TextView notesBtn;

    private SharedPreferencesManager sharedPreferencesManager;
    private ErrorHandler errorHandler;
    private CompositeSubscription subscriptions;
    private UserViewModel userViewModel;
    private BookingViewModel bookingViewModel;
    private FragmentBookingOverviewBinding dataBinding;

    /**
     * Default Constructor
     */
    public BookingOverviewFragment() {
    }


    /**
     * Called when the fragment is created to initialize all the views
     * (depending on if an active booking was found)
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment using data binding
        dataBinding = FragmentBookingOverviewBinding.inflate(inflater, container, false);

        View view = dataBinding.getRoot();

        // Initialize all the components
        initViews(view);

        // Initialize Shared Preferences
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());

        // Initialize Error Handler
        errorHandler = new ErrorHandler();

        // Initialize View Model
        userViewModel = new UserViewModel();
        bookingViewModel = new BookingViewModel();

        // Initialize subscriptions
        subscriptions = new CompositeSubscription();

        // Check if ID is present in saved instance state
        if(savedInstanceState != null){
            id = savedInstanceState.getString(BOOKING_ID_KEY);
        }

        // Check if a booking ID has been passed as an argument to the fragment
        if(getArguments() != null){
            id = getArguments().getString(BOOKING_ID_KEY);
        }

        // Load the booking
        loadBooking();

        return view;
    }

    /**
     * Initailize all the needed views for the active booking display
     * @param view
     */
    private void initViews(View view){
        // Initialize Refresh Layout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);

        emptyBookingContainer = view.findViewById(R.id.empty_booking);
        activeBookingContainer = view.findViewById(R.id.booking_overview);

        progressIndicator = view.findViewById(R.id.progress_indicator);

        cancelBtn = view.findViewById(R.id.cancel_btn);
        notesBtn = view.findViewById(R.id.view_notes_btn);

        cancelBtn.setOnClickListener(v -> onCancelClick());
        notesBtn.setOnClickListener(v -> onNotesClick());
    }

    /**
     * Load the booking using view model
     */
    private void loadBooking(){
        // Show Progress Bar
        progressIndicator.setVisibility(View.VISIBLE);
        // Lock Screen Orientation
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        }

        // Check if an ID is present, if so, just load that booking
        if(id != null){
            subscriptions.add(bookingViewModel.getBooking(sharedPreferencesManager.getToken(), id)
            .subscribe(this::handleBookingLoad, this::handleError));
        }else{
            // Get most recent booking from View Model
            subscriptions.add(userViewModel.getMostRecentBooking(sharedPreferencesManager.getToken(),
                    sharedPreferencesManager.getUser().getId()).subscribe(this::handleBookingLoad, this::handleError));
        }
    }

    /**
     * Called when the cancel button is pressed to show confirmation dialog
     */
    private void onCancelClick(){
        // Show Confirmation Dialog
        // Confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(getString(R.string.cancel_booking_confirmation));
        // When users confirms dialog, end activity
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            cancelBooking();
        });

        // Do nothing if cancel
        builder.setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Change button colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getActivity().getColor(R.color.colorPrimary));
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(getActivity().getColor(R.color.colorPrimary));
    }

    /**
     * Called when the notes button is pressed to show notes activity
     */
    private void onNotesClick(){
        Intent intent = new Intent(getActivity(), BookingNotesViewActivity.class);
        startActivity(intent);
    }

    /**
     * Actually Cancel a Booking (Send API Request Etc)
     */
    private void cancelBooking(){
        // Disable Cancel Button
        cancelBtn.setEnabled(false);
        // Lock Screen Orientation
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        }

        // Show Progress Bar
        activeBookingContainer.setVisibility(View.GONE);
        emptyBookingContainer.setVisibility(View.GONE);
        progressIndicator.setVisibility(View.VISIBLE);

        // Send Request
        Booking updatedBooking = new Booking();
        updatedBooking.setStatus(BookingStatus.Cancelled);
        subscriptions.add(bookingViewModel.editBooking(sharedPreferencesManager.getToken(), id, updatedBooking)
            .subscribe(this::handleBookingCancellation, this::handleError));
    }

    /**
     * Handle successful retrieval of bookings
     * @param booking
     */
    private void handleBookingLoad(Booking booking){
        // Hide Progress Bar
        progressIndicator.setVisibility(View.GONE);
        // Unlock screen orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        // If booking is no longer active, remove cancel button
        if(booking.getStatus() == BookingStatus.Cancelled || booking.getStatus() == BookingStatus.Finished){
            cancelBtn.setVisibility(View.GONE);
        }else{
            cancelBtn.setVisibility(View.VISIBLE);
        }

        // If booking is successfully loaded, hide empty view
        activeBookingContainer.setVisibility(View.VISIBLE);
        emptyBookingContainer.setVisibility(View.GONE);

        // Store the ID of the booking for easier reloading
        this.id = booking.getId();

        // Send model to the view using Data Binding
        dataBinding.setBooking(beautifyBooking(booking));
    }

    /**
     * Handle successful cancellation of booking
     * @param response
     */
    private void handleBookingCancellation(Response response){
        // Re-enable Buton
        cancelBtn.setEnabled(true);
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        // Hide Progress Bar
        progressIndicator.setVisibility(View.GONE);

        // Refresh View
        loadBooking();
        // Show Toast
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.booking_cancelled, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 150);
        toast.show();
    }

    /**
     * Handle Error in HTTP Request
     * @param error
     */
    private void handleError(Throwable error){
        // Hide Progress Bar
        progressIndicator.setVisibility(View.GONE);
        // Unlock screen orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        // Handle Errors using Error Handler
        errorHandler.handle(error, getContext(), getView());

        // If an error has occurred, show the empty view
        activeBookingContainer.setVisibility(View.GONE);
        emptyBookingContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Make all strings within a booking presentable
     * (use proper casing etc)
     * @param booking
     */
    private Booking beautifyBooking(Booking booking){
        // Apply proper capitalization to destination and pickup location
        String prettyPickupLocation = WordUtils.capitalizeFully(booking.getPickupLocation());
        String prettyDestination = WordUtils.capitalizeFully(booking.getDestination());


        // Apply proper capitalization to driver's name (if exists)
        if(booking.getDriver() != null){
            String prettyDriverName = WordUtils.capitalizeFully(booking.getDriver().getName());
            booking.getDriver().setName(prettyDriverName);
        }

        booking.setPickupLocation(prettyPickupLocation);
        booking.setDestination(prettyDestination);

        return booking;
    }

    /**
     * Called just before the fragment is destroyed to save booking ID
     * @param outState
     */
    public void onSaveInstanceState(@NonNull Bundle outState){
        if(id != null){
            outState.putString(BOOKING_ID_KEY, id);
        }
    }

    /**
     * Called when the screen is refreshed by SwipeRefreshLayout
     */
    @Override
    public void onRefresh(){
        loadBooking();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Called when fragment is destroyed to unsubscribe
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        subscriptions.unsubscribe();
    }
}
