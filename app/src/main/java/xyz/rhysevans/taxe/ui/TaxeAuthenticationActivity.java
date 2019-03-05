package xyz.rhysevans.taxe.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xyz.rhysevans.taxe.R;

/**
 * TaxeAuthenticationActivity.java
 * The authentication activity of the app, to house the login and registration fragments
 * users must authenticate successfully in order to get to the main activity.
 * @author Rhys Evans
 * @version 0.1
 */
public class TaxeAuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxe_authentication);
    }
}
