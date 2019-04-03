/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.home;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.text.WordUtils;

import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.databinding.FragmentHomeBinding;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.model.User;
import xyz.rhysevans.taxe.ui.TaxeMainActivity;
import xyz.rhysevans.taxe.ui.account.AccountOverviewFragment;
import xyz.rhysevans.taxe.ui.authentication.LoginFragment;
import xyz.rhysevans.taxe.util.BookingStatus;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;
import xyz.rhysevans.taxe.viewmodel.BookingViewModel;
import xyz.rhysevans.taxe.viewmodel.UserViewModel;

/**
 * HomeFragment.java
 *
 * Manage the app's home screen fragment.
 * @author Rhys Evans
 * @version 0.1
 */
public class HomeFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private TextView myAccountBtn;

    private CompositeSubscription subscriptions;
    private ErrorHandler errorHandler;
    private SharedPreferencesManager sharedPreferencesManager;
    private UserViewModel userViewModel;
    private FragmentHomeBinding dataBinding;


    public HomeFragment() {
        // Required empty public constructor
    }


    /**
     * Called when the view is created to intialize everything
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dataBinding = FragmentHomeBinding.inflate(inflater, container, false);

        View view = dataBinding.getRoot();

        // Initialize Shared Preferences
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());

        // Initialize Error Handler
        errorHandler = new ErrorHandler();

        // Initialize Subscriptions
        subscriptions = new CompositeSubscription();

        // Initialize View Models
        userViewModel = new UserViewModel();

        // Initialize Views
        myAccountBtn = view.findViewById(R.id.my_account_btn);
        myAccountBtn.setOnClickListener(v -> goToAccount());

        // Load the recent booking and current user
        loadUser();
        loadRecentBooking();

        return view;
    }

    /**
     * Load all of the currently logged in user's information
     */
    private void loadUser(){
        // Lock Screen Orientation
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        // Load the current user
        subscriptions.add(userViewModel.getUser(sharedPreferencesManager.getToken(),
                sharedPreferencesManager.getUser().getId()).subscribe(this::handleUserLoad, this::handleError));
    }

    /**
     * Load the current user's most recent booking
     */
    private void loadRecentBooking(){
        // Lock Screen Orientation
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        // Load the most recent booking
        subscriptions.add(userViewModel.getMostRecentBooking(sharedPreferencesManager.getToken(),
                sharedPreferencesManager.getUser().getId()).subscribe(this::handleBookingLoad, this::handleError));
    }

    /**
     * Handle successfully loading user information
     * @param user
     */
    private void handleUserLoad(User user){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Beautify user's name
        String beautyName = WordUtils.capitalizeFully(user.getName());
        user.setName(beautyName);

        dataBinding.setUser(user);
    }

    /**
     * Handle successfully loading user's recent booking
     * @param booking
     */
    private void handleBookingLoad(Booking booking){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // If the most recent booking is inactive, don't bind
        if(booking.getStatus() == BookingStatus.Finished || booking.getStatus() == BookingStatus.Cancelled){
            booking.setStatus(null);

        }

        dataBinding.setBooking(booking);
    }

    /**
     * Handle an error in the API request
     * @param error
     */
    private void handleError(Throwable error){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Handle Errors using Error Handler
        errorHandler.handle(error, getContext(), getView());
    }

    /**
     * Navigate to Account Fragment
     */
    private void goToAccount(){
        // Move to another fragment
        // This smells a bit but it saves a lot of work..
        TaxeMainActivity activity = (TaxeMainActivity)getActivity();
        if(activity != null){
            activity.navTo(R.id.nav_account);
        }
    }

    /**
     * Called just before the fragment is destroyed to unsubscribe any Async tasks
     */
    public void onDestroy(){
        super.onDestroy();
        if(subscriptions != null){
            subscriptions.unsubscribe();
        }
    }

}
