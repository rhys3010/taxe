/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.account;

import android.accounts.Account;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.model.Response;
import xyz.rhysevans.taxe.model.User;
import xyz.rhysevans.taxe.network.NetworkUtil;
import xyz.rhysevans.taxe.ui.TaxeMainActivity;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;
import xyz.rhysevans.taxe.util.Validation;
import xyz.rhysevans.taxe.viewmodel.UserViewModel;

/**
 * ChangeNameActivity.java
 *
 * Controller class for the change name activity
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class ChangeNameActivity extends AppCompatActivity {

    private TextInputEditText newNameInput;
    private TextInputLayout newNameInputContainer;
    private ImageButton closeBtn;
    private ImageButton submitBtn;
    private View progressIndicator;
    private View view;

    private SharedPreferencesManager sharedPreferencesManager;
    private ErrorHandler errorHandler;
    private CompositeSubscription subscriptions;
    private UserViewModel userViewmodel;

    private String newName;

    /**
     * Called when the activity is created to initialize all views and Subscriptions
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        // Initialize Shared Preferences
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);

        // Initialize Error Handler
        errorHandler = new ErrorHandler();

        // Initialize View Model
        userViewmodel = new UserViewModel();

        // Initialize rxJava Subscriptions
        subscriptions = new CompositeSubscription();

        // Initialize views
        initViews();

        // Initialize toolbar
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Initialize all the view elements
     */
    private void initViews(){
        // Parent View
        view = findViewById(android.R.id.content);

        // Initialize Toolbar
        closeBtn = findViewById(R.id.close_btn);
        submitBtn = findViewById(R.id.submit_btn);
        TextView toolbarTitle = findViewById(R.id.title);
        toolbarTitle.setText(getString(R.string.account_action_change_name));

        newNameInput = findViewById(R.id.new_name_input);
        newNameInputContainer = findViewById(R.id.new_name_input_container);
        progressIndicator = findViewById(R.id.progress_indicator_overlay);

        // Initialize Button Behaviour
        closeBtn.setOnClickListener(v -> finish());
        submitBtn.setOnClickListener(v -> changeName());
    }

    /**
     * Change the user's name by undertaking needed validation
     * and sending Retrofit request
     */
    private void changeName(){
        // Reset Any Errors
        newNameInputContainer.setError(null);

        // Retrieve entered value from edit text
        newName = newNameInput.getText().toString();

        // Validate input
        if(Validation.isValidName(newName)){
            // Lock Screen Orientation
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
            }
            else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
            }

            // Disable button
            submitBtn.setEnabled(false);
            // Show progress bar
            progressIndicator.setVisibility(View.VISIBLE);

            // Create the new user object to send
            User updatedUser = new User();
            updatedUser.setName(newName);

            // Send HTTP Request
            subscriptions.add(userViewmodel.editUser(sharedPreferencesManager.getToken(),
                    sharedPreferencesManager.getUser().getId(), updatedUser)
                    .subscribe(this::handleSuccess, this::handleError));

        }else{
            newNameInputContainer.setError(getString(R.string.name_error));
        }

    }

    /**
     * Handle errors returned from HTTP request
     * @param error
     */
    private void handleError(Throwable error){
        // Re-enable  button
        submitBtn.setEnabled(true);
        // Hide Progress Bar
        progressIndicator.setVisibility(View.GONE);
        // Unlock screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        // Handle any errors using the util class
        errorHandler.handle(error, this, view);
    }

    /**
     * Handle successful response from HTTP request
     * @param response
     */
    private void handleSuccess(Response response){
        // Re-enable  button
        submitBtn.setEnabled(true);
        // Hide Progress Bar
        progressIndicator.setVisibility(View.GONE);
        // Unlock screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        // Clear the edit text input
        newNameInput.setText(null);

        // Update Shared Prefs
        User oldUser = sharedPreferencesManager.getUser();
        oldUser.setName(newName);
        sharedPreferencesManager.putUser(oldUser);

        // Show Toast
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.name_successfully_changed), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();

        // Set Result and End Activity
        setResult(AccountOverviewFragment.CHANGE_NAME_REQUEST_CODE);
        this.finish();
    }

    /**
     * Unsubscribe from all RX subscriptions when activity is destroyed
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        subscriptions.unsubscribe();
    }

}
