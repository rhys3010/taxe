/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.account;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.model.Response;
import xyz.rhysevans.taxe.model.User;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.Errors;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;
import xyz.rhysevans.taxe.util.Validation;
import xyz.rhysevans.taxe.viewmodel.UserViewModel;

/**
 * ChangePasswordActivity.java
 *
 * Controller class for the change password activity, validate input and send HTTP request
 *
 * @author Rhys Evans
 * @verison 0.1
 */
public class ChangePasswordActivity extends AppCompatActivity {

    /*
     * All of the view elements for inputs
     */
    private ImageButton closeBtn;
    private ImageButton submitBtn;
    private TextInputEditText currentPasswordInput;
    private TextInputEditText newPasswordInput;
    private TextInputEditText confirmNewPasswordInput;

    /*
     * All of the view elements for input containers
     */
    private TextInputLayout currentPasswordInputContainer;
    private TextInputLayout newPasswordInputContainer;
    private TextInputLayout confirmNewPasswordInputContainer;

    private View progressIndicator;

    // rxJava subscription object
    private CompositeSubscription subscriptions;
    private SharedPreferencesManager sharedPreferencesManager;
    private UserViewModel userViewModel;
    private ErrorHandler errorHandler;
    private View view;

    /**
     * Called when activity is created to initialize everything
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize Shared Prefs
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);

        // Initialize Error handler
        errorHandler = new ErrorHandler();

        // Initialize Subscriptions
        subscriptions = new CompositeSubscription();

        // Initialize View Model
        userViewModel = new UserViewModel();

        // Initialize all views
        initViews();

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Initialize all of the views
     */
    private void initViews(){
        // Initialize view
        view = findViewById(android.R.id.content);

        // Initialize toolbar
        closeBtn = findViewById(R.id.close_btn);
        submitBtn = findViewById(R.id.submit_btn);
        TextView toolbarTitle = findViewById(R.id.title);
        toolbarTitle.setText(R.string.account_action_change_password);

        // Initialize inputs
        currentPasswordInput = findViewById(R.id.current_password_input);
        newPasswordInput = findViewById(R.id.new_password_input);
        confirmNewPasswordInput = findViewById(R.id.confirm_new_password_input);

        currentPasswordInputContainer = findViewById(R.id.current_password_input_container);
        newPasswordInputContainer = findViewById(R.id.new_password_input_container);
        confirmNewPasswordInputContainer = findViewById(R.id.confirm_new_password_input_container);

        progressIndicator = findViewById(R.id.progress_indicator_overlay);

        // Add Behaviour to Buttons
        closeBtn.setOnClickListener(v -> finish());
        submitBtn.setOnClickListener(v -> changePassword());
    }

    /**
     * Send the API request to change the password
     */
    private void changePassword(){
        // Reset any errors
        currentPasswordInputContainer.setError(null);
        newPasswordInputContainer.setError(null);
        confirmNewPasswordInputContainer.setError(null);

        // Retrieve entered values
        String currentPassword = currentPasswordInput.getText().toString();
        String newPassword = newPasswordInput.getText().toString();
        String confirmNewPassword = confirmNewPasswordInput.getText().toString();

        // Validate Fields
        if(validateFields(newPassword, confirmNewPassword)){
            // Lock Screen Orientation
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
            // Disable Button
            submitBtn.setEnabled(false);
            // Show Progress Bar
            progressIndicator.setVisibility(View.VISIBLE);

            // Send the HTTP request
            User updatedUser = new User();
            updatedUser.setPassword(newPassword);
            updatedUser.setOldPassword(currentPassword);
            subscriptions.add(userViewModel.editUser(sharedPreferencesManager.getToken(),
                    sharedPreferencesManager.getUser().getId(), updatedUser)
                    .subscribe(this::handleSuccess, this::handleError));
        }

    }

    /**
     * Validate the password fields to make sure they conform the requirements
     * @param newPassword
     * @param confirmNewPassword
     */
    private boolean validateFields(String newPassword, String confirmNewPassword){
        int errors = 0;

        if(!Validation.isValidPassword(newPassword)){
            errors++;
            newPasswordInputContainer.setError(getString(R.string.password_error));
        }

        if(!Validation.isValidPasswordConfirmation(newPassword, confirmNewPassword)){
            errors++;
            newPasswordInputContainer.setError(getString(R.string.password_confirmation_error));
            confirmNewPasswordInputContainer.setError(getString(R.string.password_confirmation_error));
        }

        return errors == 0;
    }

    /**
     * Handle a successful password change
     * @param response
     */
    private void handleSuccess(Response response){
        // Re-Enable Button
        submitBtn.setEnabled(true);
        // Hide Progress Bar
        progressIndicator.setVisibility(View.GONE);
        // Unlock Screen Rotation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Clear the inputs
        currentPasswordInput.setText(null);
        newPasswordInput.setText(null);
        confirmNewPasswordInput.setText(null);

        // Show Toast
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.password_successfully_changed), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();

        // Set result and end activity
        setResult(AccountOverviewFragment.CHANGE_PASSWORD_REQUEST_CODE);
        this.finish();
    }

    /**
     * Handle an unsuccessful password change
     * @param error
     */
    private void handleError(Throwable error){
        // Re-Enable Button
        submitBtn.setEnabled(true);
        // Hide Progress Bar
        progressIndicator.setVisibility(View.GONE);
        // Unlock Screen Rotation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Handle Errors using util class and save the error code
        int errorCode = errorHandler.handle(error, this, view);

        // If error is Code is invalid password, show error under current password field
        if(errorCode == Errors.AUTHENTICATION_FAILED_ERROR.getErrorCode()){
            currentPasswordInputContainer.setError(getString(R.string.incorrect_password_error));
        }
    }
}
