package xyz.rhysevans.taxe.util;

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
}
