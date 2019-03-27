package xyz.rhysevans.taxe.util;

/**
 * Enum to convert error numbers to more readable format
 */
public enum Errors {

    // Declare all errors
    INVALID_TOKEN_ERROR(1),
    TOKEN_EXPIRED_ERROR(2),
    MISSING_TOKEN_ERROR(3),
    NO_USERS_FOUND_ERROR(4),
    USER_ALREADY_EXISTS_ERROR(5),
    AUTHENTICATION_FAILED_ERROR(6),
    VALIDATION_ERROR(7),
    INVALID_OBJECT_ID_ERROR(8),
    UNAUTHORIZED_EDIT_ERROR(9),
    BOOKING_NOT_FOUND_ERROR(10),
    CUSTOMER_ALREADY_HAS_ACTIVE_BOOKING_ERROR(11),
    UNAUTHORIZED_VIEW_ERROR(12),
    INVALID_ROLE_ERROR(13),
    MISSING_AUTHENTICATION_ERROR(14),
    INTERNAL_SERVER_ERROR(0);

    /**
     * The true error code
     */
    private int errorCode;

    /**
     * Constructor
     * @param errorCode
     */
    Errors(int errorCode){
        this.errorCode = errorCode;
    }

    /**
     * Return the true error code
     * @return
     */
    public int getErrorCode(){
        return this.errorCode;
    }

}
