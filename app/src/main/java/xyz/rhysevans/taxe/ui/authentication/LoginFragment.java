package xyz.rhysevans.taxe.ui.authentication;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.ui.TaxeMainActivity;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;

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
        // TEMP
        // Set token
        sharedPreferencesManager.putToken("tokengoeshere");
        // Move to main screen
        Intent intent = new Intent(getActivity(), TaxeMainActivity.class);
        startActivity(intent);
        getActivity().finish();
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