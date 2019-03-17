package xyz.rhysevans.taxe.model;

import java.util.Date;

/**
 * LoginResponse.java
 *
 * Custom Response Object for User Login to retrieve
 * information about user and the user's access token
 *
 * @author Rhys Evans
 * @version 0.2
 */
public class LoginResponse {

    /**
     * The access token returned by the API
     */
    private String token;

    /**
     * User's Name returned in response
     */
    private String name;

    /**
     * User's email returned in response
     */
    private String email;

    /**
     * User's account creation date returned in response
     */
    private Date created_at;

    /**
     * User's role returned in response
     */
    private String role;

    /**
     * User's account ID returned in response
     */
    private String _id;

    /**
     * The ID of all of the user's bookings
     */
    private String[] bookings;

    /**
     * Default Constructor
     */
    public LoginResponse(){
    }

    /**
     * Get the token
     * @return
     */
    public String getToken() {
        return token;
    }

    /**
     * Get the role from a response
     * @return
     */
    public String getRole(){
        return role;
    }

    /**
     * Get the name from the response
     * @return
     */
    public String getName(){
        return name;
    }

    /**
     * Get the ID from the response
     * @return
     */
    public String getId(){
        return _id;
    }

    /**
     * Get the Email returned in the response
     * @return
     */
    public String getEmail(){
        return email;
    }

    /**
     * Get the created_at date date of the user
     * @return created_at
     */
    public Date getCreated_at(){
        return created_at;
    }

    /**
     * Get the ID of all the user's bookings
     * @return
     */
    public String[] getBookings(){
        return bookings;
    }
}
