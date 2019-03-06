package xyz.rhysevans.taxe.util;

import xyz.rhysevans.taxe.R;

/**
 * ErrorParser.java
 *
 * Using an error code from HTTP response, show the correct error. Increased support
 * for internationalization / localization.
 * Error Reference:
 * https://github.com/rhys3010/taxe-api/blob/master/routes/README.md
 * @author Rhys Evans
 * @version 0.1
 */
public class ErrorParser {

    /**
     * Based on the error code provided, return the correct string resource to use
     * only a limited number of the below errors will ever actually be returned to the user
     * (hopefully)
     * @return
     */
    public static int getErrorMessage(int code){

        int resourceId;

        switch(code){
            case 1:
                resourceId = R.string.invalid_token_error;
                break;

            case 2:
                resourceId = R.string.token_expired_error;
                break;

            case 3:
                resourceId = R.string.missing_token_error;
                break;

            case 4:
                resourceId = R.string.no_users_found_error;
                break;

            case 5:
                resourceId = R.string.user_already_exists_error;
                break;

            case 6:
                resourceId = R.string.authentication_failed_error;
                break;

            case 7:
                resourceId = R.string.validation_error;
                break;

            case 8:
                resourceId = R.string.invalid_object_id_error;
                break;

            case 9:
                resourceId = R.string.unauthorized_edit_error;
                break;

            case 0:
                resourceId = R.string.internal_server_error;
                break;

            default:
                resourceId = R.string.internal_server_error;
                break;
        }

        return resourceId;
    }
}
