package xyz.rhysevans.taxe.ui.booking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.model.Response;
import xyz.rhysevans.taxe.network.NetworkUtil;
import xyz.rhysevans.taxe.ui.TaxeMainActivity;
import xyz.rhysevans.taxe.ui.dialogs.NumberPickerDialog;
import xyz.rhysevans.taxe.util.ErrorHandler;
import xyz.rhysevans.taxe.util.Errors;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;
import xyz.rhysevans.taxe.util.Validation;
import xyz.rhysevans.taxe.viewmodel.BookingViewModel;

/**
 * CreateBookingActivity.java
 *
 * Controller class for activity creation screen
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class CreateBookingActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private final String BOOKING_HOUR_KEY = "BOOKING_HOUR";
    private final String BOOKING_MINUTE_KEY = "BOOKING_MINUTE";

    /*
     * All of the view elements for inputs
     */
    private ImageButton cancelBtn;
    private ImageButton createBtn;
    private TextInputEditText pickupLocationInput;
    private TextInputEditText destinationInput;
    private TextInputEditText timeInput;
    private TextInputEditText noPassengersInput;
    private TextInputEditText notesInput;

    /*
     * All of the view elements for input containers
     */
    private TextInputLayout pickupLocationInputContainer;
    private TextInputLayout destinationInputContainer;
    private TextInputLayout timeInputContainer;
    private TextInputLayout noPassengersInputContainer;
    private TextInputLayout notesInputContainer;

    private View progressIndicator;

    // rxJava subscription object
    private CompositeSubscription subscriptions;
    private SharedPreferencesManager sharedPreferencesManager;
    private ErrorHandler errorHandler;
    private BookingViewModel bookingViewModel;
    private View view;

    private int bookingHour;
    private int bookingMinute;

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

        // Initialize View Model
        bookingViewModel = new BookingViewModel();

        // Initialize all views
        initViews();

        // Initialize variables from saved instance state
        if(savedInstanceState != null){
            bookingHour = savedInstanceState.getInt(BOOKING_HOUR_KEY);
            bookingMinute = savedInstanceState.getInt(BOOKING_MINUTE_KEY);
        }


        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
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

        // Initialize toolbar
        cancelBtn = findViewById(R.id.close_btn);
        createBtn = findViewById(R.id.submit_btn);
        TextView toolbarTitle = findViewById(R.id.title);
        toolbarTitle.setText(getString(R.string.create_new_booking));

        pickupLocationInput = findViewById(R.id.pickup_location_input);
        destinationInput = findViewById(R.id.destination_input);
        timeInput = findViewById(R.id.time_input);
        noPassengersInput = findViewById(R.id.number_of_passengers_input);
        notesInput = findViewById(R.id.notes_input);
        progressIndicator = findViewById(R.id.progress_indicator_overlay);

        pickupLocationInputContainer = findViewById(R.id.pickup_location_input_container);
        destinationInputContainer = findViewById(R.id.destination_input_container);
        timeInputContainer = findViewById(R.id.time_input_container);
        noPassengersInputContainer = findViewById(R.id.number_of_passengers_input_container);
        notesInputContainer = findViewById(R.id.notes_input_container);


        // Set Default Value of No_Passengers to 1
        noPassengersInput.setText("1");

        // Set the default value of desired time to 15 mins from NOW
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        bookingHour = cal.get(Calendar.HOUR_OF_DAY);
        bookingMinute = cal.get(Calendar.MINUTE);

        timeInput.setText(getString(R.string.desired_time_format, bookingHour, bookingMinute));

        // Add behaviour to relevant views
        noPassengersInput.setOnClickListener(this::showNumberPickerDialog);
        timeInput.setOnClickListener(v -> showTimePicker());
        cancelBtn.setOnClickListener(v -> cancel());
        createBtn.setOnClickListener(v -> createBooking());
    }

    /**
     * Create the booking
     */
    private void createBooking(){
        // Reset any errors
        pickupLocationInputContainer.setError(null);
        destinationInputContainer.setError(null);
        timeInputContainer.setError(null);
        noPassengersInputContainer.setError(null);
        notesInputContainer.setError(null);

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
        int noPassengers = 0;
        if(!noPassengersInput.getText().toString().isEmpty()){
            noPassengers = Integer.parseInt(noPassengersInput.getText().toString());
        }

        // Create a new date object with the selected hours and minutes
        // to send in HTTP request
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, bookingHour);
        cal.set(Calendar.MINUTE, bookingMinute);

        Date time = cal.getTime();

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
            Booking booking = new Booking(pickupLocation, destination, time, noPassengers);

            // Send HTTP request
            subscriptions.add(bookingViewModel.createBooking(sharedPreferencesManager.getToken(), booking)
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
    private boolean validateFields(String pickupLocation, String destination, Date time, int noPassengers, String notes){
        int errors = 0;

        if(!Validation.isValidNoPassengers(noPassengers)){
            errors++;
            noPassengersInputContainer.setError(getString(R.string.no_passengers_error));
        }

        if(pickupLocation.length() < 3){
            errors++;
            pickupLocationInputContainer.setError(getString(R.string.pickup_location_error));
        }

        if(destination.length() < 3){
            errors++;
            destinationInputContainer.setError(getString(R.string.destination_error));
        }

        if(!Validation.isValidBookingTime(time)){
            errors++;
            timeInputContainer.setError(getString(R.string.booking_time_error));
        }

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

        // Show Toast
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.booking_created_successfully), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();

        // Force to Booking Screen and End
        setResult(TaxeMainActivity.CREATE_BOOKING_REQUEST_CODE);
        this.finish();
    }

    /**
     * Display a number picker dialog to prompt user for the
     * number of passengers
     * @param view
     */
    private void showNumberPickerDialog(View view){
        // Create the dialog fragment
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
     * Display a time picker dialog to prompt user for
     * desired booking time
     */
    private void showTimePicker(){

        // Create new time picker instance
        TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            // Update booking time
            bookingHour = hourOfDay;
            bookingMinute = minute;

            // Set the contents of the edit text to the relevant hours / minutes
            timeInput.setText(getString(R.string.desired_time_format, bookingHour, bookingMinute));

        }, bookingHour, bookingMinute, true);

        timePicker.setTitle(getString(R.string.desired_time_selection));
        timePicker.show();

        // Change button colors
        Button positiveButton = timePicker.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getColor(R.color.colorPrimary));
        Button negativeButton = timePicker.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(getColor(R.color.colorPrimary));
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

    /**
     * Called just before the activity is destroyed
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
        // Save current booking times to outstate
        outState.putInt(BOOKING_HOUR_KEY, bookingHour);
        outState.putInt(BOOKING_MINUTE_KEY, bookingMinute);

        super.onSaveInstanceState(outState);
    }
}
