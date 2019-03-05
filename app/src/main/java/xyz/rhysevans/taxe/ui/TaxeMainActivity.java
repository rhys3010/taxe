package xyz.rhysevans.taxe.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xyz.rhysevans.taxe.R;

/**
 * TaxeMainActivity.java
 * The main activity of the app, can only be accessed by authenticated users.
 * @author Rhys Evans
 * @version 0.1
 */
public class TaxeMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxe_main);
    }
}
