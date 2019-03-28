package xyz.rhysevans.taxe.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response.java
 *
 * Model class for the generic HTTP response from the API server
 * Usually in response to POST and PUT methods
 * @author Rhys Evans
 * @version 0.1
 */
public class Response {


    @SerializedName("message")
    private String message;

    @SerializedName("bookingId")
    private String bookingId;

    /**
     * Default Constructor
     */
    public Response(){
    }

    /**
     * Get the message returned by the API
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the booking ID of a newly created booking
     * @return
     */
    public String getBookingId(){
        return bookingId;
    }
}
