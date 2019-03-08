package xyz.rhysevans.taxe.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.ui.account.AccountOverviewFragment;
import xyz.rhysevans.taxe.ui.booking.BookingHistoryFragment;
import xyz.rhysevans.taxe.ui.booking.BookingOverviewFragment;
import xyz.rhysevans.taxe.ui.home.HomeFragment;
import xyz.rhysevans.taxe.util.Constants;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;

/**
 * TaxeMainActivity.java
 * The main activity of the app, can only be accessed by authenticated users.
 * Handles the app's main navigation by loading different fragments
 * @author Rhys Evans
 * @version 0.1
 */
public class TaxeMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Instance of shared preference manager
     */
    private SharedPreferencesManager sharedPreferencesManager;


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

        // Initialize Nav Menu
        BottomNavigationView navMenu = findViewById(R.id.navigation_menu);
        navMenu.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Load the default fragment
        loadFragment(new HomeFragment());
    }

    /**
     * Event handler for navigation item selection, in the bottom nav.
     * Change the title on the Action Bar based on the current fragment
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        // Get the action bar
        ActionBar actionBar = getSupportActionBar();

        switch(item.getItemId()){
            case R.id.nav_home:
                actionBar.setTitle(R.string.app_name);
                loadFragment(new HomeFragment());
                return true;

            case R.id.nav_booking:
                actionBar.setTitle(R.string.nav_booking);
                loadFragment(new BookingOverviewFragment());
                return true;

            case R.id.nav_history:
                actionBar.setTitle(R.string.nav_history);
                loadFragment(new BookingHistoryFragment());
                return true;

            case R.id.nav_account:
                actionBar.setTitle(R.string.nav_account);
                loadFragment(new AccountOverviewFragment());
                return true;
        }

        return false;
    }

    /**
     * Populate the fragment container of the activity with a new fragment
     * @param fragment
     */
    private void loadFragment(Fragment fragment){
        // Load the fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
