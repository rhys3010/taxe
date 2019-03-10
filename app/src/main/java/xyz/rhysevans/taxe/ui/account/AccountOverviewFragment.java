package xyz.rhysevans.taxe.ui.account;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.model.User;
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

        // Populate CardView
        populateCardView(view);

        // Logout Btn behaviour
        Button logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(v -> {
            logout();
        });

        return view;
    }

    private void populateCardView(View view){
        // Initialize objects
        TextView name = view.findViewById(R.id.profile_name);
        TextView email = view.findViewById(R.id.profile_email);
        TextView role = view.findViewById(R.id.profile_role);

        // Get user from shared prefs
        User user = sharedPreferencesManager.getUser();

        // Populate fields
        name.setText(user.getName());
        email.setText(user.getEmail());
        role.setText(user.getRole());
    }

    /**
     * Ask user for confirmation, if confirmed, delete any user-related
     * shared preferences and send user back to login screen.
     */
    private void logout(){
        // Confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(getString(R.string.logout_confirmation));
        // When users confirms dialog, send theem back to login screen
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            // Delete all shared prefs
            sharedPreferencesManager.deleteAll();

            // Send user to login screen
            Intent intent = new Intent(getActivity(), TaxeAuthenticationActivity.class);
            startActivity(intent);
            getActivity().finish();
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

}
