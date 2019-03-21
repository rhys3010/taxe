package xyz.rhysevans.taxe.ui.booking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
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
import xyz.rhysevans.taxe.ui.dialogs.NumberPickerDialog;
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
public class CreateBookingActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

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
    private TextInputEditText pickupLocationInput;

    /**
     * Destination location view
     */
    private TextInputEditText destinationInput;

    /**
     * Desired Time input view
     */
    private TextInputEditText timeInput;

    /**
     * Number of passengers input view
     */
    private TextInputEditText noPassengersInput;

    /**
     * Notes input view
     */
    private TextInputEditText notesInput;

    /**
     * The progress bar
     */
    private ProgressBar progressIndicator;

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
        progressIndicator = findViewById(R.id.progress_indicator);

        // Set Default Value of No_Passengers to 1
        noPassengersInput.setText("1");

        // Get reasonable time
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 1);
        Date timeToUse = cal.getTime();

        timeInput.setText(timeToUse.toString());
        timeInput.setEnabled(false);

        // Add behaviour to relevant views
        noPassengersInput.setOnClickListener(this::showNumberPickerDialog);
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

        // Hide the keyboard
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if(view == null){
            view = new View(this);
        }

        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        // Retrieve entered values from the form
        String pickupLocation = pickupLocationInput.getText().toString();
        String destination = destinationInput.getText().toString();
        String notes = notesInput.getText().toString();
        String time = timeInput.getText().toString();
        int noPassengers = 0;
        if(!noPassengersInput.getText().toString().isEmpty()){
            noPassengers = Integer.parseInt(noPassengersInput.getText().toString());
        }


        // TEMP: Override Time (for now)
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 1);
        Date timeToUse = cal.getTime();

        // Validate fields
        if(validateFields(pickupLocation, destination, time, noPassengers, notes)){
            // Lock Screen Orientation
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }

            // Disable button
            createBtn.setEnabled(false);
            // Show progress bar
            progressIndicator.setVisibility(View.VISIBLE);

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
        // Hide Progress Bar
        progressIndicator.setVisibility(View.GONE);
        // Unlock screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Handle Errors using util class and save the error code
        int errorCode = errorHandler.handle(error, this, view);

        // If error is Code 11, show dialog
        if(errorCode == Errors.CUSTOMER_ALREADY_HAS_ACTIVE_BOOKING_ERROR.getErrorCode()){
            showErrorDialog();
        }
    }

    /**
     * Handle successful response from API by displaying in snackbar
     * @param response
     */
    private void handleSuccess(Response response){
        // Re-enable  button
        createBtn.setEnabled(true);
        // Hide Progress Bar
        progressIndicator.setVisibility(View.GONE);
        // Unlock screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

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
     * Display a number picker dialog to prompt user for the
     * number of passengers
     * @param view
     */
    private void showNumberPickerDialog(View view){
        // Create the dialoog fragment
        NumberPickerDialog dialog = new NumberPickerDialog();

        // Create new bundle to store args
        Bundle args = new Bundle();
        args.putInt(NumberPickerDialog.MAX_VALUE_KEY, getResources().getInteger(R.integer.max_no_passengers));
        args.putInt(NumberPickerDialog.MIN_VALUE_KEY, getResources().getInteger(R.integer.min_no_passengers));
        args.putString(NumberPickerDialog.MESSAGE_KEY, getResources().getString(R.string.number_of_passengers_selection));

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), NumberPickerDialog.TAG);
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
     * Show a dialog to inform the user they already have an active booking and end the
     * activity.
     */
    private void showErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.booking_not_created));
        builder.setMessage(getString(R.string.customer_already_has_active_booking_error));
        builder.setIcon(R.drawable.ic_error_black_24dp);
        builder.setCancelable(false);
        // When users confirms dialog, close the activity
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            // Close the Activity..
            finish();
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        // Change button colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getColor(R.color.colorPrimary));
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

        if(destinationInput.getText().toString().length() != 0){
            return false;
        }

        if(timeInput.getText().toString().length() != 0){
            return false;
        }


        if(notesInput.getText().toString().length() != 0){
            return false;
        }

        return true;
    }

    /**
     * Unsubscribe all the composite subscriptions
     * when activity is destroyed
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        subscriptions.unsubscribe();
    }

    /**
     * Implementing the number picker's onvaluechange listener to update edittext content with
     * number of passnegers
     * @param picker
     * @param oldVal
     * @param newVal
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        noPassengersInput.setText(String.valueOf(picker.getValue()));
    }
}
