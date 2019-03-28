package xyz.rhysevans.taxe.ui.authentication;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.model.LoginResponse;
import xyz.rhysevans.taxe.model.User;
import xyz.rhysevans.taxe.ui.TaxeMainActivity;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.Errors;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;
import xyz.rhysevans.taxe.util.Validation;
import xyz.rhysevans.taxe.viewmodel.UserViewModel;

/**
 * LoginFragment.java
 * Fragment to display all the UI elements needed to login and send
 * credentials to API.
 * @author Rhys Evans
 * @version 0.1
 */
public class LoginFragment extends Fragment {


    public static final String TAG = LoginFragment.class.getSimpleName();

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private Button loginBtn;
    private TextView registerText;
    private View progressIndicator;

    private SharedPreferencesManager sharedPreferencesManager;
    private ErrorHandler errorHandler;
    private CompositeSubscription subscriptions;
    private UserViewModel userViewModel;

    /**
     * Initialize all the helper classess
     */
    public LoginFragment() {
        // Initailize shared preference manager
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());

        // Initialize subscriptions
        subscriptions = new CompositeSubscription();

        // Initialize View Model
        userViewModel = new UserViewModel();

        // Initialize Error Handler
        errorHandler = new ErrorHandler();
    }


    /**
     * Initialize all of the UI views and inflate the fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize views
        initViews(view);

        return view;
    }

    /**
     * Initialize the fragment's UI views and declare button behaviour
     * @param view - the parent view of the fragment
     */
    private void initViews(View view) {
        // Initialize all the views
        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        loginBtn = view.findViewById(R.id.login_btn);
        registerText = view.findViewById(R.id.register_text);
        progressIndicator = view.findViewById(R.id.progress_indicator_overlay);

        // Declare button actions
        loginBtn.setOnClickListener(v -> login());
        registerText.setOnClickListener(v -> goToRegister());
    }

    /**
     * Send provided credentials to server for authentication
     */
    private void login() {
        // Reset Errors
        emailInput.setError(null);
        passwordInput.setError(null);

        // Get values from the fields
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        // If all fields are successfully validated, begin login process
        if(validateFields(email, password)){
            // Lock Screen Orientation
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }

            // Disable button
            loginBtn.setEnabled(false);
            // Show Progress bar
            progressIndicator.setVisibility(View.VISIBLE);

            // Send login request via viewmodel
            subscriptions.add(userViewModel.login(email, password).subscribe(this::handleSuccess, this::handleError));
        }
    }

    /**
     * Validate the email and password field to ensure they aren't empty
     * @param email
     * @param password
     * @return
     */
    private boolean validateFields(String email, String password){
        int errors = 0;

        if(!Validation.isValidEmail(email)){
            errors++;
            emailInput.setError(getString(R.string.email_error));
        }

        // If password is empty
        if(password.length() == 0){
            errors++;
            passwordInput.setError(getString(R.string.empty_password_error));
        }

        return errors == 0;
    }

    /**
     * Handle successful authentication by saving the token and moving to the
     * main screeen.
     * @param response
     */
    private void handleSuccess(LoginResponse response){
        // Re-enable button
        loginBtn.setEnabled(true);
        // Hide Progress bar
        progressIndicator.setVisibility(View.GONE);
        // Unlock screen orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Create a new user object to store non-sensitive data in shared prefs
        User user = new User(response.getId(), response.getName(), response.getEmail(), response.getRole(), response.getCreatedAt());

        // Place all the values in the shared preferences
        sharedPreferencesManager.putToken(response.getToken());
        sharedPreferencesManager.putUser(user);

        // Clear the text fields
        emailInput.setText(null);
        passwordInput.setText(null);

        // Move to main screen
        Intent intent = new Intent(getActivity(), TaxeMainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * Handle error in authentication by displaying the error message
     * @param error
     */
    private void handleError(Throwable error){
        // Re-enable button
        loginBtn.setEnabled(true);
        // Hide Progress bar
        progressIndicator.setVisibility(View.GONE);
        // Unlock screen orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Handle the error using the util classs
        int errorCode = errorHandler.handle(error, this.getContext(), this.getView());

        // Show errors on form if auth error occurs
        if(errorCode == Errors.AUTHENTICATION_FAILED_ERROR.getErrorCode()){
            emailInput.setError(getString(R.string.authentication_failed_error));
            passwordInput.setError(getString(R.string.authentication_failed_error));
        }
    }

    /**
     * Change fragments to register fragment, called when
     * user chooses to 'register'
     */
    private void goToRegister() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RegisterFragment registerFragment = new RegisterFragment();
        ft.replace(R.id.authentication_fragment_container, registerFragment, RegisterFragment.TAG).addToBackStack(TAG);
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