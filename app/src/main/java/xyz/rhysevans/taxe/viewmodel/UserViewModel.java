package xyz.rhysevans.taxe.viewmodel;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.model.LoginResponse;
import xyz.rhysevans.taxe.model.Response;
import xyz.rhysevans.taxe.model.User;
import xyz.rhysevans.taxe.network.NetworkUtil;
import xyz.rhysevans.taxe.util.SharedPreferencesManager;

/**
 * UserViewModel.java
 *
 * Serve as mediator between user views and user models, to follow MVVM design pattern.
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class UserViewModel extends ViewModel {

    /**
     * Send login request to API
     * @param email
     * @param password
     * @return
     */
    public Observable<LoginResponse> login(String email, String password){
        return NetworkUtil.getRetrofit(email, password).login()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Register new user through API
     * @param newUser
     * @return
     */
    public Observable<Response> register(User newUser){
        return NetworkUtil.getRetrofit().register(newUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Retrieve a user from API request by user's ID
     * @param token
     * @param userId
     * @return
     */
    public Observable<User> getUser(String token, String userId){
        return NetworkUtil.getRetrofit(token).getUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Edits a user entry
     * @param token
     * @param userId
     * @param updatedUser
     * @return
     */
    public Observable<Response> editUser(String token, String userId, User updatedUser){
        return NetworkUtil.getRetrofit(token).editUser(userId, updatedUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Get a list of all of a user's bookings
     * @param token
     * @param userId
     * @param limit
     * @return
     */
    public Observable<ArrayList<Booking>> getUserBookings(String token, String userId, int limit){
        return NetworkUtil.getRetrofit(token).getUserBookings(limit, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
