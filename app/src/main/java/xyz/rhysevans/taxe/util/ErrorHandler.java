/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import xyz.rhysevans.taxe.R;
import xyz.rhysevans.taxe.ui.authentication.AuthenticationActivity;

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
     * Based on the error provided, perform the relevant actions
     * (show dialog, snackbar, etc)
     *
     * @param error
     * @param context
     * @param view
     * @returns errorCode - The error code that was found
     */
    public int handle(Throwable error, Context context, View view) {

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
                String errorString = context.getString(parseErrorMessage(errorCode));

                // If the error was a token error, show token expiry dialog
                if (errorCode == Errors.INVALID_TOKEN_ERROR.getErrorCode() ||
                        errorCode == Errors.MISSING_TOKEN_ERROR.getErrorCode() ||
                        errorCode == Errors.TOKEN_EXPIRED_ERROR.getErrorCode()) {
                    handleExpiredToken(context);
                } else {
                    // If the error was not a token error or network error,
                    // show a snackbar.
                    showSnackbarMessage(errorString, view);
                }

            } catch (IOException e) {
                // *nothing to see here*
            }
            // If the error wasn't a HTTP error, show network issues dialog
        } else {
            Log.d("TEST", error.toString());
            showNetworkErrorDialog(context);
        }

        return errorCode;
    }

    /**
     * Show a dialog indicating network issues to user
     *
     * @param context
     */
    private void showNetworkErrorDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.network_error));
        builder.setMessage(context.getString(R.string.network_error_long));
        builder.setIcon(R.drawable.ic_signal_cellular_connected_no_internet_0_bar_black_24dp);
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Change button colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(context.getColor(R.color.colorPrimary));
    }

    /**
     * Show a dialog to the user, indicating that their token or 'session'
     * has expired and redirect them to the login screen. Delete token
     * from shared prefs
     *
     * @param context
     */
    private void handleExpiredToken(Context context) {
        // Delete token from shared prefs
        SharedPreferencesManager.getInstance(context).deleteAll();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.token_expiry));
        builder.setMessage(context.getString(R.string.token_expired_error));
        builder.setIcon(R.drawable.ic_vpn_key_black_24dp);
        builder.setCancelable(false);
        // When users confirms dialog, send theem back to login screen
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            // Send user to login screen
            Intent intent = new Intent(context, AuthenticationActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        // Change button colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(context.getColor(R.color.colorPrimary));
    }

    /**
     * Show the user a snackbar messagge to display any other errors.
     * @param view - the view to show the snackbar on
     * @param message
     */
    private void showSnackbarMessage(String message, View view) {
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
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

        if(code == Errors.BOOKING_NOT_FOUND_ERROR.getErrorCode()){
            return R.string.booking_not_found_error;
        }

        if(code == Errors.CUSTOMER_ALREADY_HAS_ACTIVE_BOOKING_ERROR.getErrorCode()){
            return R.string.customer_already_has_active_booking_error;
        }

        if(code == Errors.UNAUTHORIZED_VIEW_ERROR.getErrorCode()){
            return R.string.unauthorized_view_error;
        }

        if(code == Errors.INVALID_ROLE_ERROR.getErrorCode()){
            return R.string.invalid_role_error;
        }

        if(code == Errors.MISSING_AUTHENTICATION_ERROR.getErrorCode()){
            return R.string.missing_authentication_error;
        }

        if(code == Errors.COMPANY_NOT_FOUND_ERROR.getErrorCode()){
            return R.string.company_not_found_error;
        }

        if(code == Errors.DRIVER_ALREADY_ADDED_ERROR.getErrorCode()){
            return R.string.driver_already_added_error;
        }

        // Default Case
        return R.string.internal_server_error;
    }
}

