package xyz.rhysevans.taxe.ui.authentication;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import xyz.rhysevans.taxe.network.NetworkUtil;
import xyz.rhysevans.taxe.ui.TaxeMainActivity;
import xyz.rhysevans.taxe.util.ErrorParser;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;
import xyz.rhysevans.taxe.util.Validation;

/**
 * LoginFragment.java
 * Fragment to display all the UI elements needed to login and send
 * credentials to API.
 * @author Rhys Evans
 * @version 0.1
 */
public class LoginFragment extends Fragment {

    /**
     * The fragment's TAG, for use in fragment transactions
     */
    public static final String TAG = LoginFragment.class.getSimpleName();

    /**
     * Input Field for Email
     */
    private EditText emailInput;

    /**
     * Input Field for password
     */
    private EditText passwordInput;

    /**
     * The Login Button
     */
    private Button loginBtn;

    /**
     * The 'register' link at the bottom of the form
     */
    private TextView registerText;

    /**
     * The container for the email text input
     */
    private TextInputLayout emailInputContainer;

    /**
     * The container fo rhte password input
     */
    private TextInputLayout passwordInputContainer;

    /**
     * Instance of SharedPreferenceManager
     */
    private SharedPreferencesManager sharedPreferencesManager;

    /**
     * rxJava Composite subscription to subscribe to
     * observables returned from HTTP response
     */
    private CompositeSubscription subscriptions;

    public LoginFragment() {
        // Required empty public constructor
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

        // Initailize shared preference manager
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());

        // Initialize subscriptions
        subscriptions = new CompositeSubscription();

        // Initialize views
        initViews(view);

        return view;
    }

    /**
     * Initialize the fragment's UI views and declare button behaviour
     */
    private void initViews(View view) {
        // Initialize all the views
        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        loginBtn = view.findViewById(R.id.login_btn);
        registerText = view.findViewById(R.id.register_text);
        emailInputContainer = view.findViewById(R.id.email_input_container);
        passwordInputContainer = view.findViewById(R.id.password_input_container);

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
            // Disable cold
            loginBtn.setEnabled(false);

            // Send login request
            subscriptions.add(NetworkUtil.getRetrofit(email, password).login()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleSuccess, this::handleError));
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
    private void handleSuccess(Response response){
        // Re-enable button
        loginBtn.setEnabled(true);

        // Place all the values in the shared preferences
        sharedPreferencesManager.putToken(response.getToken());
        sharedPreferencesManager.putId(response.getId());
        sharedPreferencesManager.putName(response.getName());
        sharedPreferencesManager.putRole(response.getRole());

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

        // Get the status code from the error
        if(error instanceof HttpException){

            try{
                // Get the body of the error and convert to java object (response) using gson.
                String errorBody = ((HttpException) error).response().errorBody().string();
                JsonObject response = new Gson().fromJson(errorBody, JsonObject.class);

                // Use json response to get the error code and parse it
                int errorStringResource = ErrorParser.getErrorMessage(response.get("code").getAsInt());
                showSnackbarMessage(getString(errorStringResource));

            }catch(IOException e){
                e.printStackTrace();
            }
            // If the error wasn't a HTTP error, print network issues
        }else{
            showSnackbarMessage(getString(R.string.network_error));
        }
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
     * Change fragments to register fragment, called when
     * user chooses to 'register'
     */
    private void goToRegister() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RegisterFragment registerFragment = new RegisterFragment();
        ft.replace(R.id.authentication_fragment_container, registerFragment, RegisterFragment.TAG).addToBackStack(TAG);;
        ft.commit();
    }
}