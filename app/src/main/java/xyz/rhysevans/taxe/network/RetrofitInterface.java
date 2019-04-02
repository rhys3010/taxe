/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.network;


import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.model.LoginResponse;
import xyz.rhysevans.taxe.model.Response;
import xyz.rhysevans.taxe.model.User;


/**
 * RetrofitInterface.java
 *
 * Declare all of the API's endpoints that we will use. Each method will return an Observable
 * that will be subscribed to. This class will provide a layer of abstraction between
 * the client and the API requests, with friendlier method names e.g. register.
 * @author Rhys Evans
 * @version 0.1
 */
public interface RetrofitInterface {

    /////////////////////
    //     USERS      //
    ////////////////////

    /**
     * Get a specific user by their ID
     * @param id
     * @return
     */
    @GET("users/{id}")
    Observable<User> getUser(@Path("id") String id);

    /**
     * Register a new user with user model as body
     * @param user
     * @return
     */
    @POST("users")
    Observable<Response> register(@Body User user);

    /**
     * Authenticate credentials as Basic-Auth header. Hopefully
     * receive token in response
     * @return
     */
    @POST("users/login")
    Observable<LoginResponse> login();

    /**
     * Edit a user record
     * @param id
     * @param user
     * @return
     */
    @PUT("users/{id}")
    Observable<Response> editUser(@Path("id") String id, @Body User user);

    /**
     * Gets a list of a given user's bookings (output number can be limited)
     * @param limit
     * @param id
     * @return
     */
    @GET("users/{id}/bookings")
    Observable<ArrayList<Booking>> getUserBookings(@Path("id") String id, @Query("limit") int limit);

    /**
     * Override getUserBookings method to support request without limit param
     * @param id
     * @return
     */
    @GET("users/{id}/bookings")
    Observable<ArrayList<Booking>> getUserBookings(@Path("id") String id);

    /////////////////////
    //     BOOKINGS    //
    ////////////////////

    /**
     * Get a specific booking by its ID
     * @param id
     * @return
     */
    @GET("bookings/{id}")
    Observable<Booking> getBooking(@Path("id") String id);

    /**
     * Create a new booking with a booking model as the request body
     * @param booking
     * @return
     */
    @POST("bookings")
    Observable<Response> createBooking(@Body Booking booking);

    /**
     * Edit a current booking
     * @param id
     * @param booking
     * @return
     */
    @PUT("bookings/{id}")
    Observable<Response> editBooking(@Path("id") String id, @Body Booking booking);
}
