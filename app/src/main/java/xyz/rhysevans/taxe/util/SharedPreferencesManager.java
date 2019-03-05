package xyz.rhysevans.taxe.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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
     * Save the access token to the shared preferences
     * @param token
     */
    public void putToken(String token){
        editor.putString(Constants.TOKEN, token);
        editor.apply();
    }

    /**
     * Retrieve the token from the shared preferences
     * @return
     */
    public String getToken(){
        return sharedPreferences.getString(Constants.TOKEN, "NO_TOKEN_FOUND");
    }

    /**
     * Check if token is saved, (if user is logged in)
     * @return
     */
    public boolean isTokenPresent(){
        return sharedPreferences.contains(Constants.TOKEN);
    }

    /**
     * Delete token from shared preferences
     */
    public void deleteToken(){
        editor.remove(Constants.TOKEN);
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
