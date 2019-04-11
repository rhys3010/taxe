/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.home;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.commons.text.WordUtils;

import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.databinding.FragmentHomeBinding;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.model.Response;
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

    private CardView availabilityCard;
    private Switch availabilityToggle;

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

        // Load the recent booking and current user
        loadUser();
        loadRecentBooking();

        // Initialize Views
        initViews(view);

        return view;
    }

    /**
     * Initialize all UI elements
     * @param view
     */
    private void initViews(View view){
        myAccountBtn = view.findViewById(R.id.my_account_btn);
        myAccountBtn.setOnClickListener(v -> goToAccount());

        availabilityCard = view.findViewById(R.id.availability);
        availabilityToggle = view.findViewById(R.id.availability_toggle);

        availabilityToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Only update the availability if it was the user who checked the switch
            if(buttonView.isPressed()){
                updateAvailability(isChecked);
            }
        });
    }

    /**
     * Load all of the currently logged in user's information
     */
    private void loadUser(){
        // Lock Screen Orientation
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
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
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        }

        // Load the most recent booking
        subscriptions.add(userViewModel.getMostRecentBooking(sharedPreferencesManager.getToken(),
                sharedPreferencesManager.getUser().getId()).subscribe(this::handleBookingLoad, this::handleError));
    }

    /**
     * Update the user's availability when switch is pressed
     * @param availability
     */
    private void updateAvailability(boolean availability){
        // Lock Screen Orientation
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        }

        // Disable switch
        availabilityToggle.setEnabled(false);

        // Update Availability
        User updatedUser = new User();
        updatedUser.setAvailable(availability);

        // Send API Request
        subscriptions.add(userViewModel.editUser(sharedPreferencesManager.getToken(),
                sharedPreferencesManager.getUser().getId(), updatedUser)
                .subscribe(this::handleAvailabilityUpdate, this::handleError));
    }

    /**
     * Handle successfully loading user information
     * @param user
     */
    private void handleUserLoad(User user){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        // Beautify user's name
        String beautyName = WordUtils.capitalizeFully(user.getName());
        user.setName(beautyName);

        // If the loaded user's role differs from that saved in shared prefs, update
        if(!user.getRole().equals(sharedPreferencesManager.getUser().getRole())){
            // Update Shared Preferences
            User updatedUser = sharedPreferencesManager.getUser();
            updatedUser.setRole(user.getRole());

            // Update user's company
            if(user.getCompany() != null){
                updatedUser.setCompany(user.getCompany());
            }

            sharedPreferencesManager.putUser(updatedUser);

            // Update Nav Bar
            ((TaxeMainActivity) getActivity()).initNavbar();
        }

        dataBinding.setUser(user);
    }

    /**
     * Handle successfully loading user's recent booking
     * @param booking
     */
    private void handleBookingLoad(Booking booking){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        // If the most recent booking is inactive, don't bind
        if(booking.getStatus() == BookingStatus.Finished || booking.getStatus() == BookingStatus.Cancelled){
            booking.setStatus(null);
        }

        dataBinding.setBooking(booking);
    }

    /**
     * Handle Successfully updating the user's availability
     * @param response
     */
    private void handleAvailabilityUpdate(Response response){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        // Enable Button
        availabilityToggle.setEnabled(true);

        // Refresh View
        loadUser();
    }

    /**
     * Handle an error in the API request
     * @param error
     */
    private void handleError(Throwable error){
        // Unlock Screen Orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        // Re-enable button
        availabilityToggle.setEnabled(true);

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
