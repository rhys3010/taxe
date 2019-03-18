package xyz.rhysevans.taxe.ui.booking;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.model.Response;
import xyz.rhysevans.taxe.network.NetworkUtil;
import xyz.rhysevans.taxe.ui.authentication.TaxeAuthenticationActivity;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.Errors;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;
import xyz.rhysevans.taxe.util.Validation;

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
     * rxJava subscription object
     */
    private CompositeSubscription subscriptions;

    /**
     * Shared Preferences
     */
    private SharedPreferencesManager sharedPreferencesManager;

    /**
     * Error Handler
     */
    private ErrorHandler errorHandler;

    /**
     * The view object
     */
    private View view;

    /**
     * Called when the activity is created to initialize all the views and needed behaviour
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_booking);

        // Initialize Shared Prefs
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);

        // Initialize Error Handler
        errorHandler = new ErrorHandler();

        // Initialize Subscriptions
        subscriptions = new CompositeSubscription();

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
        // Parent View
        view = findViewById(android.R.id.content);

        // Initialize all the variables
        cancelBtn = findViewById(R.id.create_booking_close_button);
        createBtn = findViewById(R.id.create_booking_button);
        pickupLocationInput = findViewById(R.id.pickup_location_input);
        destinationInput = findViewById(R.id.destination_input);
        timeInput = findViewById(R.id.time_input);
        noPassengersInput = findViewById(R.id.number_of_passengers_input);
        notesInput = findViewById(R.id.notes_input);

        // TEMP
        noPassengersInput.setText("1");
        noPassengersInput.setEnabled(false);

        // Get reasonable time
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 1);
        Date timeToUse = cal.getTime();

        timeInput.setText(timeToUse.toString());
        timeInput.setEnabled(false);

        // Add behaviour to relevant views
        cancelBtn.setOnClickListener(v -> cancel());
        createBtn.setOnClickListener(v -> createBooking());
    }

    /**
     * Create the booking
     */
    private void createBooking(){
        // Reset any errors
        pickupLocationInput.setError(null);
        destinationInput.setError(null);
        timeInput.setError(null);
        noPassengersInput.setError(null);
        notesInput.setError(null);

        // Retrieve entered values from the form
        String pickupLocation = pickupLocationInput.getText().toString();
        String destination = destinationInput.getText().toString();
        String notes = notesInput.getText().toString();
        String time = timeInput.getText().toString();
        int noPassengers = Integer.parseInt(noPassengersInput.getText().toString());

        // TEMP: Override Time (for now)
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 1);
        Date timeToUse = cal.getTime();

        // Validate fields
        if(validateFields(pickupLocation, destination, time, noPassengers, notes)){
            // Disable button
            createBtn.setEnabled(false);
            // TODO: Show progress bar

            // Create new booking object to send
            Booking booking = new Booking(pickupLocation, destination, timeToUse, noPassengers);

            // Send HTTP request
            subscriptions.add(NetworkUtil.getRetrofit(sharedPreferencesManager.getToken()).createBooking(booking)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleSuccess, this::handleError));
        }
    }

    /**
     * Validate the fields provided using util class
     * @param pickupLocation
     * @param time
     * @param noPassengers
     * @param notes
     */
    private boolean validateFields(String pickupLocation, String destination, String time, int noPassengers, String notes){
        int errors = 0;

        if(!Validation.isValidNoPassengers(noPassengers)){
            errors++;
            noPassengersInput.setError(getString(R.string.no_passengers_error));
        }

        if(pickupLocation.length() < 3){
            errors++;
            pickupLocationInput.setError(getString(R.string.pickup_location_error));
        }

        if(destination.length() < 3){
            errors++;
            destinationInput.setError(getString(R.string.destination_error));
        }

        // TODO: Validate Time

        return errors == 0;
    }

    /**
     * Handle errors received from API by using the error parser util and providing the error
     * code returned.
     * @param error
     */
    private void handleError(Throwable error){
        // Re-enable  button
        createBtn.setEnabled(true);
        // TODO Hide Progress Bar

        // Handle Errors using util class and save the error code
        int errorCode = errorHandler.handle(error, this, view);
    }

    /**
     * Handle successful response from API by displaying in snackbar
     * @param response
     */
    private void handleSuccess(Response response){
        // Re-enable  button
        createBtn.setEnabled(true);
        // TODO Hide Progress Bar

        // Clear the text inputs
        pickupLocationInput.setText(null);
        destinationInput.setText(null);
        timeInput.setText(null);
        noPassengersInput.setText(null);
        notesInput.setText(null);

        // End Activity
        this.finish();

        showSnackbarMessage(getString(R.string.create_booking_success));
    }

    /**
     * Send a snackbar message, checking for a null view
     * @param message
     */
    private void showSnackbarMessage(String message){
        if(view != null){
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
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
