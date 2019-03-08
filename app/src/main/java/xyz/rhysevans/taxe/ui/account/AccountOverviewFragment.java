package xyz.rhysevans.taxe.ui.account;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.ui.TaxeAuthenticationActivity;
import xyz.rhysevans.taxe.ui.authentication.LoginFragment;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;

/**
 * AccountOverviewFragment.java
 *
 * Manage the account overview screen
 * @author Rhys Evans
 * @version 0.1
 */
public class AccountOverviewFragment extends Fragment {

    /**
     * The fragment's TAG, for use in fragment transactions
     */
    public static final String TAG = LoginFragment.class.getSimpleName();

    /**
     * Shared Preferences Manager
     */
    private SharedPreferencesManager sharedPreferencesManager;


    public AccountOverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account_overview, container, false);

        // Init Shared prefs
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());

        // Logout Btn behaviour
        Button logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(v -> {
            sharedPreferencesManager.deleteAll();
            // Switch to setup activity
            Intent intent = new Intent(getActivity(), TaxeAuthenticationActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }

}
