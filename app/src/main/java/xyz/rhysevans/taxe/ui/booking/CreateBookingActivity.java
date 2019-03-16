package xyz.rhysevans.taxe.ui.booking;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.ui.authentication.TaxeAuthenticationActivity;

/**
 * CreateBookingActivity.java
 *
 * Controller class for activity creation screen
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class CreateBookingActivity extends AppCompatActivity {

    /**
     * Close button
     */
    private ImageButton cancelBtn;

    /**
     * The 'create' button
     */
    private TextView createBtn;

    /**
     * Pickup location view
     */
    private EditText pickupLocationInput;

    /**
     * Destination location view
     */
    private EditText destinationInput;

    /**
     * Desired Time input view
     */
    private EditText timeInput;

    /**
     * Number of passengers input view
     */
    private EditText noPassengersInput;

    /**
     * Notes input view
     */
    private EditText notesInput;

    /**
     * Called when the activity is created to initialize all the views and needed behaviour
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_booking);

        // Initialize all views
        initViews();

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.create_booking_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Override default back button behaviour to prompt user for
     * confirmation instead
     */
    @Override
    public void onBackPressed(){
        cancel();
    }

    /**
     * Initialize all the required views
     */
    private void initViews(){
        // Initialize all the variables
        cancelBtn = findViewById(R.id.create_booking_close_button);
        createBtn = findViewById(R.id.create_booking_button);
        pickupLocationInput = findViewById(R.id.pickup_location_input);
        destinationInput = findViewById(R.id.destination_input);
        timeInput = findViewById(R.id.time_input);
        noPassengersInput = findViewById(R.id.number_of_passengers_input);
        notesInput = findViewById(R.id.notes_input);

        // Add behaviour to relevant views
        cancelBtn.setOnClickListener(v -> cancel());
        createBtn.setOnClickListener(v -> createBooking());
    }

    /**
     * Create the booking
     */
    private void createBooking(){
    }

    /**
     * Show confirmation dialog if any inputs have contents
     * if not, simply end the activity
     */
    private void cancel(){
        if(isFormEmpty()){
            finish();
        }else{
            // Confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.cancel_booking_creation_confirmation));
            // When users confirms dialog, end activity
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                finish();
            });

            // Do nothing if cancel
            builder.setNegativeButton(android.R.string.cancel, null);

            AlertDialog dialog = builder.create();
            dialog.show();

            // Change button colors
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(getColor(R.color.colorPrimary));
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(getColor(R.color.colorPrimary));
        }
    }

    /**
     * Return whether or not the form is empty
     */
    private boolean isFormEmpty(){
        if(pickupLocationInput.getText().toString().length() != 0){
            return false;
        }

        if (destinationInput.getText().toString().length() != 0){
            return false;
        }

        if(timeInput.getText().toString().length() != 0){
            return false;
        }

        if(noPassengersInput.getText().toString().length() != 0){
            return false;
        }

        if(notesInput.getText().toString().length() != 0){
            return false;
        }

        return true;
    }
}
