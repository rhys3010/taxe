package xyz.rhysevans.taxe.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.util.Constants;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;

/**
 * TaxeMainActivity.java
 * The main activity of the app, can only be accessed by authenticated users.
 * @author Rhys Evans
 * @version 0.1
 */
public class TaxeMainActivity extends AppCompatActivity {

    /**
     * Instance of shared preference manager
     */
    private SharedPreferencesManager sharedPreferencesManager;

    private Button logoutButton;

    /**
     * Initialize the activity, check if user is 'logged in' (token saved in shared prefs).
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxe_main);

        // Initialize shared preferences manager
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getApplicationContext());

        // Check if token is saved
        if(!sharedPreferencesManager.isTokenPresent()){
            // If token is not saved, user is not authed and should be sent to login
            Intent intent = new Intent(this, TaxeAuthenticationActivity.class);
            startActivity(intent);
            finish();
        }

        // Initialize Button
        logoutButton = findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(v -> logout());

        populateCard();
    }

    private void populateCard(){
        TextView name = findViewById(R.id.profile_name);
        TextView id = findViewById(R.id.profile_id);
        TextView role = findViewById(R.id.profile_role);

        name.setText(sharedPreferencesManager.getName());
        id.setText(sharedPreferencesManager.getId());
        role.setText(sharedPreferencesManager.getRole());
    }

    private void logout(){
        // Delete all shared preferences
        sharedPreferencesManager.deleteAll();
        Intent intent = new Intent(this, TaxeAuthenticationActivity.class);
        startActivity(intent);
        finish();
    }
}
