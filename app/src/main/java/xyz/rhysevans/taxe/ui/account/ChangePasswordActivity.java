package xyz.rhysevans.taxe.ui.account;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;

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
        confirmNewPasswordInput = findViewById(R.id.confirm_password_input);

        currentPasswordInputContainer = findViewById(R.id.current_password_input_container);
        newPasswordInputContainer = findViewById(R.id.new_password_input_container);
        confirmNewPasswordInputContainer = findViewById(R.id.confirm_new_password_input_container);

        progressIndicator = findViewById(R.id.progress_indicator_overlay);

        // Add Behaviour to Buttons
        closeBtn.setOnClickListener(v -> finish());
        // TODO: Submit Button
    }
}
