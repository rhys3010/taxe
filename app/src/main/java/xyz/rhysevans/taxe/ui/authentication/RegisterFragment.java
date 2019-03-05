package xyz.rhysevans.taxe.ui.authentication;


import android.app.FragmentTransaction;
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
import xyz.rhysevans.taxe.util.SharedPreferencesManager;

/**
 * RegisterFragment.java
 * A fragment to display all the needed UI elements to register a new user
 * with the API.
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
    private EditText conirmPasswordInput;

    /**
     * The Button to register
     */
    private Button registerBtn;

    /**
     * The TextView element for 'login' at the bottom of the form
     */
    private TextView loginText;

    /**
     * The container for the name input
     */
    private TextInputLayout nameInputContainer;

    /**
     * The container for the email input
     */
    private TextInputLayout emailInputContainer;

    /**
     * The container for the password input
     */
    private TextInputLayout passwordInputContainer;

    /**
     * The container for the password confirmation
     */
    private TextInputLayout confirmPasswordInputContainer;

    /**
     * The shared preference manager singleton instance
     */
    private SharedPreferencesManager sharedPreferencesManager;


    public RegisterFragment() {
        // Required empty public constructor
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

        // Initialize Shared Pref Manager
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());

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
        conirmPasswordInput = view.findViewById(R.id.confirm_password_input);
        registerBtn = view.findViewById(R.id.register_btn);
        loginText = view.findViewById(R.id.login_text);

        nameInputContainer = view.findViewById(R.id.name_input_container);
        emailInputContainer = view.findViewById(R.id.email_input_container);
        passwordInputContainer = view.findViewById(R.id.password_input_container);
        confirmPasswordInputContainer = view.findViewById(R.id.confirm_password_input_container);

        // Initailize view behaviour
        loginText.setOnClickListener(v -> goToLogin());
    }

    /**
     * Move to login page, by switching fragments.
     */
    private void goToLogin(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        ft.replace(R.id.authentication_fragment_container, loginFragment, LoginFragment.TAG);
        ft.commit();
    }

}
