package xyz.rhysevans.taxe.model;

/**
 * Response.java
 *
 * Model class for the HTTP response from the API server
 * @author Rhys Evans
 * @version 0.1
 */
public class Response {

    /**
     * The message of the response, this is the json returned by the API
     */
    private String message;

    /**
     * The token returned by the API when a successful authentication request is made
     */
    private String token;

    /**
     * Get the message returned by the API
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the token
     * @return
     */
    public String getToken() {
        return token;
    }
}
