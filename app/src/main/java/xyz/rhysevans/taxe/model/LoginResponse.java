/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * LoginResponse.java
 *
 * Custom Response Object for User Login to retrieve
 * information about user and the user's access token from JSON response
 *
 * @author Rhys Evans
 * @version 0.2
 */
public class LoginResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("role")
    private String role;

    @SerializedName("_id")
    private String id;

    @SerializedName("company")
    private String company;

    @SerializedName("bookings")
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
        return id;
    }

    /**
     * Get the Email returned in the response
     * @return
     */
    public String getEmail(){
        return email;
    }

    /**
     * Get the createdAt date date of the user
     * @return createdAt
     */
    public Date getCreatedAt(){
        return createdAt;
    }

    /**
     * Get the ID of all the user's bookings
     * @return
     */
    public String[] getBookings(){
        return bookings;
    }

    /**
     * Get the company ID of the user
     * @return
     */
    public String getCompany(){
        return this.company;
    }
}
