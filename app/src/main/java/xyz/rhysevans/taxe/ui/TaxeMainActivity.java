package xyz.rhysevans.taxe.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.ui.account.AccountOverviewFragment;
import xyz.rhysevans.taxe.ui.authentication.AuthenticationActivity;
import xyz.rhysevans.taxe.ui.booking.BookingHistoryFragment;
import xyz.rhysevans.taxe.ui.booking.BookingOverviewFragment;
import xyz.rhysevans.taxe.ui.booking.CreateBookingActivity;
import xyz.rhysevans.taxe.ui.home.HomeFragment;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;

/**
 * TaxeMainActivity.java
 * The main activity of the app, can only be accessed by authenticated users.
 * Handles the app's main navigation by loading different fragments
 * @author Rhys Evans
 * @version 0.1
 */
public class TaxeMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private final String PAGE_TITLE_KEY = "PAGE_TITLE_KEY";
    public static final int CREATE_BOOKING_REQUEST_CODE = 2;

    private SharedPreferencesManager sharedPreferencesManager;

    private BottomNavigationView navMenu;
    private int currentPageTitle;


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
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        }

        // Initialize Nav Menu
        navMenu = findViewById(R.id.navigation_menu);
        navMenu.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Load the default fragment if opening for first time
        if(savedInstanceState == null){
            loadFragment(new HomeFragment());
        }else{
            // Initialize page title
            currentPageTitle = savedInstanceState.getInt(PAGE_TITLE_KEY);
            // If current page title is uninitialized, just default to app's name
            if(currentPageTitle == 0){
                currentPageTitle = R.string.app_name;
            }else {
                getSupportActionBar().setTitle(currentPageTitle);
            }
        }
    }

    /**
     * Event handler for navigation item selection, in the bottom nav.
     * Change the title on the Action Bar based on the current fragment
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        // The return value
        boolean ret = false;

        // Depending on the page selected, set the title of the action bar
        // and load the correct fragment
        switch(item.getItemId()){
            case R.id.nav_home:
                getSupportActionBar().setTitle(R.string.app_name);
                loadFragment(new HomeFragment());
                ret = true;
                break;

            case R.id.nav_booking:
                getSupportActionBar().setTitle(R.string.nav_booking);
                loadFragment(new BookingOverviewFragment());
                ret = true;
                break;

            case R.id.nav_new:
                showCreateBooking();
                break;

            case R.id.nav_history:
                getSupportActionBar().setTitle(R.string.nav_history);
                loadFragment(new BookingHistoryFragment());
                ret = true;
                break;

            case R.id.nav_account:
                getSupportActionBar().setTitle(R.string.nav_account);
                loadFragment(new AccountOverviewFragment());
                ret = true;
                break;
        }

        return ret;
    }

    /**
     * Populate the fragment container of the activity with a new fragment
     * @param fragment
     */
    private void loadFragment(Fragment fragment){
        // Load the fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container, fragment);
        ft.commit();
    }

    /**
     * Show the create booking activity above the main activity
     */
    private void showCreateBooking(){
        Intent intent = new Intent(this, CreateBookingActivity.class);
        startActivityForResult(intent, CREATE_BOOKING_REQUEST_CODE);
    }

    /**
     * Navigate to a given screen
     * @param id
     */
    public void navTo(int id){
        navMenu.setSelectedItemId(id);
    }

    /**
     * Called when the create booking activity exits
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // If the request code is the same as we sent
        if(resultCode == CREATE_BOOKING_REQUEST_CODE){
            // Force nav menu to booking screen
            navMenu.setSelectedItemId(R.id.nav_booking);
        }
    }

    /**
     * Called to save the state of the activity, just before the activity is destroyed
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
        // Save the title of the loaded fragment
        outState.putInt(PAGE_TITLE_KEY, currentPageTitle);

        super.onSaveInstanceState(outState);
    }
}
