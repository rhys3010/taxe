/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import xyz.rhysevans.taxe.model.User;

/**
 * SharedPreferencesManager.java
 *
 * A singleton utility class to manage all shared preferences, intended
 * purpose is to provide a layer of abstraction and allow for cleaner
 * handling of shared preferences
 * @author Rhys Evans
 * @version 0.1
 */
public class SharedPreferencesManager {

    /**
     * Initialize instance of manager singleton
     */
    private static SharedPreferencesManager INSTANCE = null;

    /**
     * The shared preferences object
     */
    private static SharedPreferences sharedPreferences;

    /**
     * The editor object of the shared preferences
     */
    private static SharedPreferences.Editor editor;

    /**
     * Empty, private constructor to enforce singleton
     */
    private SharedPreferencesManager(){}

    /**
     * Returns the instance of the Shared Preferences Manager
     * @param context
     * @return
     */
    public static synchronized SharedPreferencesManager getInstance(Context context){
        if(INSTANCE == null){
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            editor = sharedPreferences.edit();

            INSTANCE = new SharedPreferencesManager();
        }

        return INSTANCE;
    }

    /**
     * Store a user in the shared preferences by converting to json using
     * Gson
     * @param user
     */
    public void putUser(User user){
        // Be extra sure that password is not present in user object
        if(user.getPassword() != null){
            user.setPassword(null);
        }

        // Convert object to JSON using Gson
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(Constants.USER_KEY, userJson);
        editor.commit();
    }

    /**
     * Retrieves a user from the shared preferences
     * @returns User - The user
     */
    @Nullable
    public User getUser(){
        Gson gson = new Gson();
        String userJson = sharedPreferences.getString(Constants.USER_KEY, "NO_USER_FOUND");

        // Attempt to serialize json, if userJson is invalid or malformed, return null
        try{
            return gson.fromJson(userJson, User.class);
        }catch(JsonParseException e){
            return null;
        }
    }

    /**
     * Save the access token to the shared preferences
     * @param token
     */
    public void putToken(String token){
        editor.putString(Constants.TOKEN_KEY, token);
        editor.apply();
    }

    /**
     * Save the ID of the currently active booking to the shared prefs
     * @param id
     */
    public void putActiveBooking(String id){
        editor.putString(Constants.ACTIVE_BOOKING_KEY, id);
        editor.apply();
    }

    /**
     * Retrieve the token from the shared preferences
     * @return
     */
    public String getToken(){
        return sharedPreferences.getString(Constants.TOKEN_KEY, "NO_TOKEN_FOUND");
    }

    /**
     * Retrieve the ID of the currently active booking from the shared prefs
     * @return
     */
    public String getActiveBooking(){
        return sharedPreferences.getString(Constants.ACTIVE_BOOKING_KEY, "NO_ACTIVE_BOOKINGS");
    }

    /**
     * Check if an active booking is present in the shared prefs
     * @return
     */
    public boolean isActiveBooking(){
        return sharedPreferences.contains(Constants.ACTIVE_BOOKING_KEY);
    }

    /**
     * Check if token is saved, (if user is logged in)
     * @return
     */
    public boolean isTokenPresent(){
        return sharedPreferences.contains(Constants.TOKEN_KEY);
    }

    /**
     * Delete the currently active booking from shared prefs
     */
    public void deleteActiveBooking(){
        editor.remove(Constants.ACTIVE_BOOKING_KEY);
        editor.apply();
    }

    /**
     * Delete token from shared preferences
     */
    public void deleteToken(){
        editor.remove(Constants.TOKEN_KEY);
        editor.apply();
    }

    /**
     * Delete all shared preferences
     */
    public void deleteAll(){
        editor.clear();
        editor.apply();
    }
}
