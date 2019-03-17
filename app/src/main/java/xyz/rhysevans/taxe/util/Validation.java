package xyz.rhysevans.taxe.util;

import android.util.Log;

import java.util.Date;

/**
 * Validation.java
 *
 * A Validation class for all user input, validation is up to the same standard as the
 * API's validation
 * @author Rhys Evans
 * @verison 0.1
 */
public class Validation {

    /**
     * Validates that the name provided during registration meets the following criteria:
     * Cannot contain any special characters (except hyphen)
     * Cannot contain any numbers
     * Must be longer than 3 characters
     * @param name
     * @return whether the name was valid or not
     */
    public static boolean isValidName(String name){
        return name.length() > 3 && name.matches("^[a-zA-z- ]+$");
    }

    /**
     * Validate that the email provided is a valid email using regex expression from
     * https://stackoverflow.com/questions/46155/how-to-validate-an-email-address-in-javascript
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email){
        return email.matches("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
    }

    /**
     * Validate that the password provided meets the following criteria:
     * Is atleast 8 characters long
     * Contains at least 1 number
     * @param password
     * @return
     */
    public static boolean isValidPassword(String password){
        return password.length()  > 8 && password.matches(".*\\d+.*");
    }

    /**
     * Evaluate whether the value entered into the confirm password field matches
     * the value entered into the password field.
     * @param password
     * @param confirmedPassword
     * @return
     */
    public static boolean isValidPasswordConfirmation(String password, String confirmedPassword){
        return password.equals(confirmedPassword);
    }

    /**
     * Evaluate whether a provided booking time meets the following criteria:
     *  - Cannot be too far in the future (see const)
     *  - Cannot be in the past
     *  - Cannot be too soon (see const)
     * @param time
     * @return
     */
    public static boolean isValidBookingTime(Date time){
        // Conversion rate for millisecond to hour
        final int MILLIS_PER_HOUR = 1000 * 60 * 60;
        // How far in the future are we willing to take bookings for?
        final int MAX_HOURS_FUTURE = 3;
        // How much notice do we need for a booking (i.e. 10 minutes in future..)
        final int MIN_MINUTES_NOTICE = 10;

        // Get the number of hours between the booking time and NOW
        // Cast to int
        int hoursDiff = (int) (time.getTime() - new Date().getTime()) / MILLIS_PER_HOUR;

        // Make sure booking isn't too far away
        if(hoursDiff > MAX_HOURS_FUTURE){
            return false;
        }

        // Make sure booking isn't in the past
        if(time.getTime() < new Date().getTime()){
            return false;
        }

        // Make sure booking isn't too soon
        // (if the difference in minutes between NOW and booking time is less than wanted
        if((hoursDiff * 60) < MIN_MINUTES_NOTICE){
            return false;
        }

        return true;
    }

    /**
     * Evaluate whether the provided number of passengers is valid (must be atleast 1)
     * @param noPassengers
     * @return
     */
    public static boolean isValidNoPassengers(int noPassengers){
        return noPassengers >= 1;
    }
}
