package xyz.rhysevans.taxe.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.ui.TaxeAuthenticationActivity;
import xyz.rhysevans.taxe.ui.TaxeMainActivity;

/**
 * ErrorHandler.java
 *
 * Handle any HTTP errors returned from API, by giving the user the correct feedback.
 *
 * Error Reference:
 * https://github.com/rhys3010/taxe-api/blob/master/routes/README.md
 * @author Rhys Evans
 * @version 0.1
 */
public class ErrorHandler {

    /**
     * The fragment that the error handle request came from
     */
    Fragment fragment;

    /**
     * The shared preference manager for deleting tokens
     */
    SharedPreferencesManager sharedPreferencesManager;

    /**
     * Constructor to initialize fragment
     *
     * @param fragment
     */
    public ErrorHandler(Fragment fragment) {
        this.fragment = fragment;
        sharedPreferencesManager = SharedPreferencesManager.getInstance(fragment.getContext());
    }


    /**
     * Based on the error provided, perform the relevant actions
     * (show dialog, snackbar, etc)
     *
     * @param error
     * @returns errorCode - The error code that was found
     */
    public int handle(Throwable error) {

        int errorCode = 0;

        // Get the HTTP status code from the error
        if (error instanceof HttpException) {

            try {
                // Get the body of the error thrown and convert to java object using gson
                String errorBody = ((HttpException) error).response().errorBody().string();
                JsonObject response = new Gson().fromJson(errorBody, JsonObject.class);

                // Use the json response to get the error code thrown
                errorCode = response.get("code").getAsInt();
                // Get the correct error message, given the code displayed.
                String errorString = fragment.getString(parseErrorMessage(errorCode));

                // If the error was a token error, show token expiry dialog
                if (errorCode == Errors.INVALID_TOKEN_ERROR.getErrorCode() ||
                        errorCode == Errors.MISSING_TOKEN_ERROR.getErrorCode() ||
                        errorCode == Errors.TOKEN_EXPIRED_ERROR.getErrorCode()) {
                    handleExpiredToken();
                } else {
                    // If the error was not a token error or network error,
                    // show a snackbar.
                    showSnackbarMessage(errorString);
                }

            } catch (IOException e) {
                // *nothing to see here*
            }
            // If the error wasn't a HTTP error, show network issues dialog
        } else {
            showNetworkErrorDialog();
        }

        return errorCode;
    }

    /**
     * Show a dialog indicating network issues to user
     */
    private void showNetworkErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());

        builder.setTitle(fragment.getString(R.string.network_error));
        builder.setMessage(fragment.getString(R.string.network_error_long));
        builder.setIcon(R.drawable.ic_signal_cellular_connected_no_internet_0_bar_black_24dp);
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Change button colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(fragment.getActivity().getColor(R.color.colorPrimary));
    }

    /**
     * Show a dialog to the user, indicating that their token or 'session'
     * has expired and redirect them to the login screen. Delete token
     * from shared prefs
     */
    private void handleExpiredToken() {
        // Delete token from shared prefs
        sharedPreferencesManager.deleteToken();

        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());

        builder.setTitle(fragment.getString(R.string.token_expiry));
        builder.setMessage(fragment.getString(R.string.token_expired_error));
        builder.setIcon(R.drawable.ic_vpn_key_black_24dp);
        builder.setCancelable(false);
        // When users confirms dialog, send theem back to login screen
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            // Send user to login screen
            Intent intent = new Intent(fragment.getActivity(), TaxeAuthenticationActivity.class);
            fragment.startActivity(intent);
            fragment.getActivity().finish();
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        // Change button colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(fragment.getActivity().getColor(R.color.colorPrimary));


    }

    /**
     * Show the user a snackbar messagge to display any other errors.
     *
     * @param message
     */
    private void showSnackbarMessage(String message) {
        if (fragment.getView() != null) {
            Snackbar.make(fragment.getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Based on the error code provided, return the correct string resource to use
     * only a limited number of the below errors will ever actually be returned to the user
     * (hopefully)
     *
     * @return
     */
    private int parseErrorMessage(int code) {

        if (code == Errors.INVALID_TOKEN_ERROR.getErrorCode()) {
            return R.string.invalid_token_error;
        }

        if (code == Errors.TOKEN_EXPIRED_ERROR.getErrorCode()) {
            return R.string.token_expired_error;
        }

        if (code == Errors.MISSING_TOKEN_ERROR.getErrorCode()) {
            return R.string.missing_token_error;
        }

        if (code == Errors.NO_USERS_FOUND_ERROR.getErrorCode()) {
            return R.string.no_users_found_error;
        }

        if (code == Errors.USER_ALREADY_EXISTS_ERROR.getErrorCode()) {
            return R.string.user_already_exists_error;
        }

        if (code == Errors.AUTHENTICATION_FAILED_ERROR.getErrorCode()) {
            return R.string.authentication_failed_error;
        }

        if (code == Errors.VALIDATION_ERROR.getErrorCode()) {
            return R.string.validation_error;
        }

        if (code == Errors.INVALID_OBJECT_ID_ERROR.getErrorCode()) {
            return R.string.invalid_object_id_error;
        }

        if (code == Errors.UNAUTHORIZED_EDIT_ERROR.getErrorCode()) {
            return R.string.unauthorized_edit_error;
        }

        // Default Case
        return R.string.internal_server_error;
    }
}

