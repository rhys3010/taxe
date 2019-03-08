package xyz.rhysevans.taxe.ui.authentication;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.model.Response;
import xyz.rhysevans.taxe.model.User;
import xyz.rhysevans.taxe.network.NetworkUtil;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.Errors;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;
import xyz.rhysevans.taxe.util.Validation;

/**
 * RegisterFragment.java
 * A fragment to display all the needed UI elements to register a new user
 * with the API.
 *
 * Adapted Code from: https://github.com/Learn2Crack/android-login-registration-authentication-client
 * @author Rhys Evans
 * @version 0.1
 */
public class RegisterFragment extends Fragment {

    /**
     * Set the fragmeent's TAG for fragment transactions
     */
    public static final String TAG = RegisterFragment.class.getSimpleName();

    /**
     * The EditText element for name
     */
    private EditText nameInput;

    /**
     * The EditText element for email
     */
    private EditText emailInput;

    /**
     * The EditText element for password
     */
    private EditText passwordInput;

    /**
     * The EditText element for password confirmation
     */
    private EditText confirmPasswordInput;

    /**
     * The Button to register
     */
    private Button registerBtn;

    /**
     * The TextView element for 'login' at the bottom of the form
     */
    private TextView loginText;

    /**
     * The progress indicator
     */
    private ProgressBar progressIndicator;

    /**
     * The shared preference manager singleton instance
     */
    private SharedPreferencesManager sharedPreferencesManager;

    /**
     * Error handler util class
     */
    private ErrorHandler errorHandler;

    /**
     * rxJava Composite subscription to subscribe to
     * observables returned from HTTP response
     */
    private CompositeSubscription subscriptions;


    /**
     * Constructor to utilize all helper classes
     */
    public RegisterFragment() {
        // Initialize Shared Pref Manager
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());

        // Initialize subscriptions
        subscriptions = new CompositeSubscription();

        // Initialize Error Handler
        errorHandler = new ErrorHandler(this);
    }


    /**
     * Initialize all of the UI views and inflate the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize Views
        initViews(view);

        return view;
    }

    /**
     * Initialize the UI views and add behaviour to buttons
     * @param view
     */
    private void initViews(View view){
        // Initialize all the views
        nameInput = view.findViewById(R.id.name_input);
        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        confirmPasswordInput = view.findViewById(R.id.confirm_password_input);
        registerBtn = view.findViewById(R.id.register_btn);
        loginText = view.findViewById(R.id.login_text);
        progressIndicator = view.findViewById(R.id.progress_indicator);

        // Initailize view behaviour
        registerBtn.setOnClickListener(v -> register());
        loginText.setOnClickListener(v -> goToLogin());
    }

    /**
     * Get all the values from the fields, validate them.
     * If no errors are found, begin registration process with retrofit.
     * Otherwise, upload UI with errors.
     */
    private void register(){
        // New registration attempt, so remove all errors from input fields
        nameInput.setError(null);
        emailInput.setError(null);
        passwordInput.setError(null);
        confirmPasswordInput.setError(null);

        // Get values from fields
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        // If all fields are successfully validated, begin registration process
        if(validateFields(name, email, password, confirmPassword)){
            // Disable Register Button
            registerBtn.setEnabled(false);

            // Show Progress Bar
            progressIndicator.setVisibility(View.VISIBLE);

            // Create new user
            User user = new User(name, email, password);

            // Add response from registration call to subscriptions
            subscriptions.add(NetworkUtil.getRetrofit().register(user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleSuccess, this::handleError));
        }
    }

    /**
     * Use the Validation util class to validate all the fields of the form and throw errors
     * if needed
     * @param name
     * @param email
     * @param password
     * @param confirmPassword
     * @return check if any errors occured
     */
    private boolean validateFields(String name, String email, String password, String confirmPassword){
        // track no of errors
        int errors = 0;

        if(!Validation.isValidName(name)){
            errors++;
            nameInput.setError(getString(R.string.name_error));
        }

        if(!Validation.isValidEmail(email)){
            errors++;
            emailInput.setError(getString(R.string.email_error));
        }

        if(!Validation.isValidPassword(password)){
            errors++;
            passwordInput.setError(getString(R.string.password_error));
        }

        if(!Validation.isValidPasswordConfirmation(password, confirmPassword)){
            errors++;
            passwordInput.setError(getString(R.string.password_confirmation_error));
            confirmPasswordInput.setError(getString(R.string.password_confirmation_error));
        }

        return errors == 0;
    }


    /**
     * Handle errors received from API by using the error parser util and providing the error
     * code returned.
     * @param error
     */
    private void handleError(Throwable error){
        // Re-enable register button
        registerBtn.setEnabled(true);
        // Hide Progress Bar
        progressIndicator.setVisibility(View.GONE);

        // Handle Errors using util class and save the error code
        int errorCode = errorHandler.handle(error);

        // If the error was caused by pre-existing users, mark that field as errored
        if(errorCode == Errors.USER_ALREADY_EXISTS_ERROR.getErrorCode()){
            emailInput.setError(getString(R.string.user_already_exists_error));
        }
    }

    /**
     * Handle successful response from API by displaying in snackbar
     * @param response
     */
    private void handleSuccess(Response response){
        // Re-enable register button
        registerBtn.setEnabled(true);
        // Hide Progress Bar
        progressIndicator.setVisibility(View.GONE);

        // Clear the text inputs
        emailInput.setText(null);
        nameInput.setText(null);
        passwordInput.setText(null);
        confirmPasswordInput.setText(null);

        // Move user to login screen
        goToLogin();
        showSnackbarMessage(getString(R.string.register_success));
    }


    /**
     * Send a snackbar message, checking for a null view
     * @param message
     */
    private void showSnackbarMessage(String message){
        if(getView() != null){
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Move to login page, by switching fragments.
     */
    private void goToLogin(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        ft.replace(R.id.authentication_fragment_container, loginFragment, LoginFragment.TAG).addToBackStack(TAG);
        ft.commit();
    }

    /**
     * When fragment is destroyed, unsubscribe all rx subscriptions
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        subscriptions.unsubscribe();
    }

}
