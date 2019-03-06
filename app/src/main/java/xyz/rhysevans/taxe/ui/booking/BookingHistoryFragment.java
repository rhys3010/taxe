package xyz.rhysevans.taxe.ui.booking;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.ui.authentication.LoginFragment;

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


    public BookingHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking_history, container, false);
    }

}
