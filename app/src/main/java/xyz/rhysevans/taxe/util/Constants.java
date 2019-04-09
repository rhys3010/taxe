/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.util;

/**
 * Constants.java
 * All of the app's global constants, accessible from throughout the app.
 * @author Rhys Evans
 * @version 0.1
 */
public class Constants {

    /**
     * The API's base url for sending requests
     */
    //public static final String BASE_URL = "http://taxe.rhysevans.xyz:3000/api/v1/";
    // Local Dev Environment
    public static final String BASE_URL = "http://10.0.2.2:3000/api/v1/";

    /**
     * The shared preferences key for access token
     */
    static final String TOKEN_KEY = "token";

    /**
     * The shared preferences key for the user object
     */
    static final String USER_KEY = "user";

    static final String ACTIVE_BOOKING_KEY = "active_booking";
}
