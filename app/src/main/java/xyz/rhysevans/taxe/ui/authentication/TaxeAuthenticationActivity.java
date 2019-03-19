package xyz.rhysevans.taxe.ui.authentication;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.ui.authentication.LoginFragment;

/**
 * TaxeAuthenticationActivity.java
 * The authentication activity of the app, to house the login and registration fragments
 * users must authenticate successfully in order to get to the main activity.
 * @author Rhys Evans
 * @version 0.1
 */
public class TaxeAuthenticationActivity extends AppCompatActivity {

    /**
     * Initialize activity by displaying the login fragment
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxe_authentication);

        // If the activity is opening for the first time
        if(savedInstanceState == null){
            // Show login fragment
            LoginFragment loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.authentication_fragment_container, loginFragment, LoginFragment.TAG).commit();
        }
    }

}
