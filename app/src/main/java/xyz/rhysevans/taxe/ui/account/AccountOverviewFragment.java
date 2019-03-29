package xyz.rhysevans.taxe.ui.account;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.model.User;
import xyz.rhysevans.taxe.ui.authentication.TaxeAuthenticationActivity;
import xyz.rhysevans.taxe.ui.authentication.LoginFragment;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;

/**
 * AccountOverviewFragment.java
 *
 * Manage the account overview screen and all account options available
 * @author Rhys Evans
 * @version 0.1
 */
public class AccountOverviewFragment extends Fragment implements ListView.OnItemClickListener{


    public static final String TAG = LoginFragment.class.getSimpleName();
    public static final int CHANGE_NAME_REQUEST_CODE = 3;
    public static final int CHANGE_PASSWORD_REQUEST_CODE = 4;

    private SharedPreferencesManager sharedPreferencesManager;

    private ArrayList<AccountActionModel> accountActions;
    private View view;


    /**
     * Constructor to initialize list
     */
    public AccountOverviewFragment() {
        accountActions = new ArrayList<AccountActionModel>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_account_overview, container, false);

        // Init Shared prefs
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());

        // Init user masthead
        initMasthead(view);

        // Init account actions list
        initAccountActionsList(view);

        return view;
    }

    /**
     * OnItemClick, handle when an item from the account actions list is clicked
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Get the resource ID of the list item's String
        int itemId = accountActions.get(position).getText();

        // Depending on the value of the string, perform relevant action
        switch(itemId){
            case R.string.account_action_change_password:
                Intent changePasswordIntent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivityForResult(changePasswordIntent, CHANGE_PASSWORD_REQUEST_CODE);
                break;

            case R.string.account_action_change_name:
                // Start an Activity With Result, to force refresh if successful
                Intent changeNameIntent = new Intent(getActivity(), ChangeNameActivity.class);
                startActivityForResult(changeNameIntent, CHANGE_NAME_REQUEST_CODE);
                break;

            case R.string.account_action_log_out:
                logout();
                break;

            case R.string.account_action_about:
                Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
                startActivity(aboutIntent);
                break;

            default:
                // Do nothing
        }
    }

    /**
     * Called when the change name activity exits
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // If the request code is the same as we sent, it was successful
        // and screen should be refreshed
        if(resultCode == CHANGE_NAME_REQUEST_CODE){
            initMasthead(view);
        }
    }

    /**
     * Initialize the contents of the account overview masthead
     * Initial and Full Name
     * @param view
     */
    private void initMasthead(View view){
        TextView avatarInitial = view.findViewById(R.id.account_avatar_text);
        TextView fullNameDisplay = view.findViewById(R.id.account_user_name);

        // Get the user's first name initial
        User user = sharedPreferencesManager.getUser();
        String name = "";

        // If user isn't null, get their name
        if(user != null){
            name = user.getName();
        }

        // Get user's initial
        char initial = name.charAt(0);

        // Set name to text view
        fullNameDisplay.setText(name);

        // Convert initial to uppercase string and set as text
        String initialString = String.valueOf(initial);

        avatarInitial.setText(initialString.toUpperCase());
    }

    /**
     * Initialize account options list
     */
    private void initAccountActionsList(View view){

        ListView accountActionsList = view.findViewById(R.id.account_actions_list);

        // Create account options list
        accountActions.add(new AccountActionModel(R.drawable.ic_vpn_key_black_24dp, R.string.account_action_change_password));
        accountActions.add(new AccountActionModel(R.drawable.ic_edit_black_24dp, R.string.account_action_change_name));
        accountActions.add(new AccountActionModel(R.drawable.ic_exit_to_app_black_24dp, R.string.account_action_log_out));
        accountActions.add(new AccountActionModel(R.drawable.ic_info_black_24dp, R.string.account_action_about));

        // Create and set adapter
        AccountActionsListAdapter adapter = new AccountActionsListAdapter(this.getContext(), accountActions);
        accountActionsList.setAdapter(adapter);

        // Add behaviour to options
        accountActionsList.setOnItemClickListener(this);

    }

    /**
     * Ask user for confirmation, if confirmed, delete any user-related
     * shared preferences and send user back to login screen.
     */
    private void logout(){
        // Confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(getString(R.string.logout_confirmation));
        // When users confirms dialog, send them back to login screen
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            // Delete all shared prefs
            sharedPreferencesManager.deleteAll();

            // Send user to login screen and show toast messsage
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.logged_out_successfully), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
            toast.show();

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
