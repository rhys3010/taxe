/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.network;


import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.util.BookingDeserializer;
import xyz.rhysevans.taxe.util.Constants;

/**
 * NetworkUtil.java
 *
 * The main networking class for the app, has several static methods to interact with
 * the API using different retrofit interfaces
 *
 * Code slightly adapted from: https://github.com/Learn2Crack/android-login-registration-authentication-client
 * @author Rhys Evans
 * @version 0.1
 */
public class NetworkUtil {

    /**
     * Default getRetrofit() method returns a retrofit interface with an RxJavaCallAdapterFactory
     * @return Retrofit.Builder
     */
    public static RetrofitInterface getRetrofit(){

        // Create Rx Adapter
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        // Build and return the retrofit interface
        // Base URL: This is the base url of the API likely <something>:3000/api/v1
        // Call adapter factory: This is the adapter factory we defined above
        // Converter Factory: This is the converter factory to allow Java objects to be serialized to
        // json for the API interaction
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitInterface.class);
    }

    /**
     * getRetrofit() method for sending authentication requests. Combine credentials with ":" between
     * them, then encode in Base64 and send as header in API request
     * @param  - User's provided email in plaintext
     * @param password - User's provided password in plaintext
     * @return
     */
    public static RetrofitInterface getRetrofit(String email, String password){
        // Concat the credentials
        String credentials = email + ":" + password;
        // Encode using Basic64
        String basicCredential = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        // Create a httpClient builder
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // Add an interceptor that will monitor and (if needed) retry the API call(s)
        httpClient.addInterceptor(chain -> {

            Request original = chain.request();
            // Build a HTTP request with encoded credentials as headers
            Request.Builder builder = original.newBuilder()
                    .addHeader("Authorization", basicCredential)
                    .method(original.method(), original.body());

            // Return the chained interceptor
            return chain.proceed(builder.build());
        });

        // Create rxAdapter
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        // Return Retrofit.Builder
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(httpClient.build())
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitInterface.class);
    }

    /**
     * getRetrofit() method for sending access token as Authentication header for API requests.
     * @param token - The access token
     * @param customGsonConverter - Boolean value to decide if custom GSON Converter should be used
     * @return
     */
    public static RetrofitInterface getRetrofit(String token, boolean customGsonConverter){

        Converter.Factory converterFactory;

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // Create HTTP interceptor again to attach token to request header
        httpClient.addInterceptor(chain -> {

            Request original = chain.request();

            Request.Builder builder = original.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .method(original.method(), original.body());

            return chain.proceed(builder.build());
        });

        // Load the correct converter
        if(customGsonConverter){
            converterFactory = createBookingsGsonConverter();
        }else{
            converterFactory = GsonConverterFactory.create();
        }

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(httpClient.build())
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(converterFactory)
                .build().create(RetrofitInterface.class);
    }

    /**
     * Create a custom GSON Converter specifically for bookings
     * (due to the dynamic nature of JSON fields)
     * @return
     */
    private static Converter.Factory createBookingsGsonConverter(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Booking.class, new BookingDeserializer());
        Gson gson = gsonBuilder.create();
        return  GsonConverterFactory.create(gson);
    }
}
