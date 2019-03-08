package xyz.rhysevans.taxe.model;

import java.util.Date;

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
     * Error code for error responses
     */
    private int code;

    /**
     * Name returned in response
     */
    private String name;

    /**
     * Email returned in response
     */
    private String email;

    /**
     * Created at returned in response
     */
    private Date created_at;


    /**
     * Role returned in response
     */
    private String role;

    /**
     * Id returned in response
     */
    private String _id;

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
}
