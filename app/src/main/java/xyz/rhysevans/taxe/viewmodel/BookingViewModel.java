package xyz.rhysevans.taxe.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.net.Network;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.model.Response;
import xyz.rhysevans.taxe.network.NetworkUtil;

/**
 * BookingViewModel.java
 *
 * Acts as a mediator between booking model and booking views
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class BookingViewModel extends ViewModel {

    /**
     * Create a new booking from a booking object
     * @param newBooking
     * @return
     */
    public Observable<Response> createBooking(String token, Booking newBooking){
        return NetworkUtil.getRetrofit(token).createBooking(newBooking)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Update a booking from booking object
     * @param token
     * @param bookingId
     * @param updatedBooking
     * @return
     */
    public Observable<Booking> editBooking(String token, String bookingId, Booking updatedBooking){
        return NetworkUtil.getRetrofit(token).editBooking(bookingId, updatedBooking)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Retrieve a booking by its ID
     * @param token
     * @param bookingId
     * @return
     */
    public Observable<Booking> getBooking(String token, String bookingId){
        return NetworkUtil.getRetrofit(token).getBooking(bookingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
