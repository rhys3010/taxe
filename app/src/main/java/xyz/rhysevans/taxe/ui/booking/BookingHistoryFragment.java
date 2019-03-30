package xyz.rhysevans.taxe.ui.booking;


import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class BookingHistoryFragment extends Fragment {

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

        // Initialize List View
        bookingHistoryList = view.findViewById(R.id.booking_history_list);
        bookingHistoryList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));


        // Initialize View model
        userViewModel = new UserViewModel();

        // Initialize Refresh
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        // Load the bookings
        loadBookings();

        return view;
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

        // Initialize Adapter
        bookingHistoryListAdapter = new BookingHistoryListAdapter(bookings);
        bookingHistoryList.setAdapter(bookingHistoryListAdapter);
        bookingHistoryList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Handle any errors in retrieving the booking history
     * @param error
     */
    private void handleError(Throwable error){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

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

}
