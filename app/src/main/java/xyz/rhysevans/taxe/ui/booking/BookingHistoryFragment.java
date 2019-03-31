package xyz.rhysevans.taxe.ui.booking;


import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
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
 * BookingHistoryFragment.java
 *
 * Manage the booking history screen
 * @author Rhys Evans
 * @version 0.1
 */
public class BookingHistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    /**
     * The fragment's TAG, for use in fragment transactions
     */
    public static final String TAG = LoginFragment.class.getSimpleName();

    private ErrorHandler errorHandler;
    private CompositeSubscription subscriptions;
    private UserViewModel userViewModel;
    private SharedPreferencesManager sharedPreferencesManager;

    private BookingHistoryListAdapter bookingHistoryListAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView bookingHistoryList;
    private View emptyBookingView;
    private View view;


    public BookingHistoryFragment() {
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
        bookingHistoryList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        bookingHistoryListAdapter = new BookingHistoryListAdapter();
        bookingHistoryListAdapter.setHasStableIds(true);
        bookingHistoryList.setAdapter(bookingHistoryListAdapter);
        bookingHistoryList.setLayoutManager(new LinearLayoutManager(getContext()));

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
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        // Show Progress Indicator
        swipeRefreshLayout.setRefreshing(true);

        subscriptions.add(userViewModel.getUserBookings(sharedPreferencesManager.getToken(),
                sharedPreferencesManager.getUser().getId()).subscribe(this::handleSuccess, this::handleError));
    }

    /**
     * Handle successful retrival of booking history
     * @param bookings
     */
    private void handleSuccess(ArrayList<Booking> bookings){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Hide Progress Indicator
        swipeRefreshLayout.setRefreshing(false);

        // If list is empty, show empty view
        if(bookings.size() == 0){
            emptyBookingView.setVisibility(View.VISIBLE);
            bookingHistoryList.setVisibility(View.GONE);
        }else{
            emptyBookingView.setVisibility(View.GONE);
            bookingHistoryList.setVisibility(View.VISIBLE);
        }

        // Populate Bookings
        bookingHistoryListAdapter.populateList(bookings);
    }

    /**
     * Handle any errors in retrieving the booking history
     * @param error
     */
    private void handleError(Throwable error){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

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
}
