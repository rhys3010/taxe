package xyz.rhysevans.taxe.ui.booking;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.ui.authentication.LoginFragment;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;

/**
 * BookingOverviewFragment.java
 *
 * Manage the booking overview screen.
 * @author Rhys Evans
 * @version 0.1
 */
public class BookingOverviewFragment extends Fragment {

    /**
     * The fragment's TAG, for use in fragment transactions
     */
    public static final String TAG = LoginFragment.class.getSimpleName();

    // The container for the empty booking view
    FrameLayout emptyBookingContainer;

    // The Container for the populate booking view
    CardView activeBookingContainer;

    // All of the labels needed to populate
    TextView pickupLocationLabel;
    TextView destinationLabel;
    TextView dueAtLabel;
    TextView driverLabel;
    TextView statusLabel;
    TextView noPassengersLabel;

    // Cancel Button
    TextView cancelBtn;

    SharedPreferencesManager sharedPreferencesManager;
    ErrorHandler errorHandler;
    CompositeSubscription subscriptions;

    public BookingOverviewFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking_overview, container, false);

        // Initialize all the components
        initViews(view);

        // Initialize Shared Preferences
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());

        // Initialize Error Handler
        errorHandler = new ErrorHandler();

        // Initialize subscriptions
        subscriptions = new CompositeSubscription();

        return view;
    }

    /**
     * Initailize all the needed views for the active booking display
     * @param view
     */
    private void initViews(View view){
        emptyBookingContainer = view.findViewById(R.id.empty_booking);
        activeBookingContainer = view.findViewById(R.id.booking_overview);

        pickupLocationLabel = view.findViewById(R.id.pickup_location_label);
        destinationLabel = view.findViewById(R.id.destination_label);
        dueAtLabel = view.findViewById(R.id.due_at_label);
        driverLabel = view.findViewById(R.id.driver_label);
        statusLabel = view.findViewById(R.id.status_label);
        noPassengersLabel = view.findViewById(R.id.no_passengers_label);

        cancelBtn = view.findViewById(R.id.cancel_btn);
    }
}
