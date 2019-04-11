/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.booking;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.ui.authentication.LoginFragment;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;
import xyz.rhysevans.taxe.viewmodel.UserViewModel;

/**
 * BookingListFragment.java
 *
 * Manage the any booking list fragments (Booking History & Driver's Active Bookings)
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class BookingListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public static final String TAG = LoginFragment.class.getSimpleName();
    public static final String ACTIVE_BOOKING_KEY = "ACTIVE_BOOKING_KEY";

    // A flag to determine whether the list should load all bookings
    // or only 'active' bookings
    private boolean activeBookings;

    private ArrayList<Booking> bookings;

    private ErrorHandler errorHandler;
    private CompositeSubscription subscriptions;
    private UserViewModel userViewModel;
    private SharedPreferencesManager sharedPreferencesManager;

    private BookingListAdapter bookingListAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView bookingHistoryList;
    private View emptyBookingView;
    private View view;


    public BookingListFragment() {
        // Required empty public constructor
    }


    /**
     * Called when the fragment is created to initialize list adapter, rx subscriptions etc.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_booking_history, container, false);

        // Initialize Error Handler
        errorHandler = new ErrorHandler();

        // Initialize Subscriptions
        subscriptions = new CompositeSubscription();

        // Initialize Shared Prefs
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());

        // Initialize View model
        userViewModel = new UserViewModel();

        // Check if 'activeBookings' flag is present in saved instance state
        if(savedInstanceState != null){
            activeBookings = savedInstanceState.getBoolean(ACTIVE_BOOKING_KEY);
        }

        // Check if a 'activeBookings' flag has been passed as an argument to the fragment
        if(getArguments() != null){
            activeBookings = getArguments().getBoolean(ACTIVE_BOOKING_KEY);
        }

        initViews(view);

        // Load the bookings
        loadBookings();

        return view;
    }

    /**
     * Initialize all of the Views needed
     * @param view
     */
    private void initViews(View view){
        // Initialize List View
        bookingHistoryList = view.findViewById(R.id.booking_history_list);
        if(!activeBookings){
            bookingHistoryList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }

        // Initialize Adapter based on the 'active bookings' flag
        bookingListAdapter = new BookingListAdapter(activeBookings);

        bookingListAdapter.setHasStableIds(true);
        bookingHistoryList.setAdapter(bookingListAdapter);
        bookingHistoryList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Click Listener
        bookingListAdapter.setItemClickListener((v, position) -> {
            // Open a new activity to show the booking overview
            Intent intent = new Intent(getActivity(), BookingViewActivity.class);
            Bundle args = new Bundle();

            // Pass Booking ID to the view
            args.putString(BookingOverviewFragment.BOOKING_ID_KEY, bookings.get(position).getId());
            intent.putExtras(args);

            startActivity(intent);
        });

        // Initialize Empty Booking View
        emptyBookingView = view.findViewById(R.id.empty_booking);

        // Initialize Refresh
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    /**
     * Sends a request to the API via the View model to get a
     * list of all the user's bookings
     */
    private void loadBookings(){
        // Lock Screen Orientation
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        }

        // Show Progress Indicator
        swipeRefreshLayout.setRefreshing(true);

        // Either load all bookings or load only active bookings
        if(activeBookings){
            subscriptions.add(userViewModel.getActiveBookings(sharedPreferencesManager.getToken(),
                    sharedPreferencesManager.getUser().getId()).subscribe(this::handleSuccess, this::handleError));
        }else{
            subscriptions.add(userViewModel.getUserBookings(sharedPreferencesManager.getToken(),
                    sharedPreferencesManager.getUser().getId()).subscribe(this::handleSuccess, this::handleError));
        }
    }

    /**
     * Handle successful retrival of booking history
     * @param bookings
     */
    private void handleSuccess(ArrayList<Booking> bookings){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        // Hide Progress Indicator
        swipeRefreshLayout.setRefreshing(false);

        // Set the local list of bookings
        this.bookings = bookings;

        // If list is empty, show empty view
        if(bookings.size() == 0){
            emptyBookingView.setVisibility(View.VISIBLE);
            bookingHistoryList.setVisibility(View.GONE);
        }else{
            emptyBookingView.setVisibility(View.GONE);
            bookingHistoryList.setVisibility(View.VISIBLE);
        }

        // Populate Bookings
        bookingListAdapter.populateList(bookings);
    }

    /**
     * Handle any errors in retrieving the booking history
     * @param error
     */
    private void handleError(Throwable error){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        // Hide Progress Indicator
        swipeRefreshLayout.setRefreshing(false);

        // If error occurred, show empty view instead
        emptyBookingView.setVisibility(View.VISIBLE);
        bookingHistoryList.setVisibility(View.GONE);

        errorHandler.handle(error, getContext(), view);
    }

    /**
     * Called just before the fragment is destroyed
     * to unsubscribe any async tasks
     */
    @Override
    public void onDestroy(){
        subscriptions.unsubscribe();
        super.onDestroy();
    }

    /**
     * Called when the swipe refresh is triggered
     */
    @Override
    public void onRefresh() {
        loadBookings();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Called just before fragment is deleted to save the 'activeBookings' flag
     * @param outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTIVE_BOOKING_KEY, activeBookings);
    }
}
