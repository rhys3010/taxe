package xyz.rhysevans.taxe.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import xyz.rhysevans.taxe.util.BookingStatus;

/**
 * Booking.java
 *
 * Booking Model Class that maps onto a Mongoose / MongoDB model in the API.
 *
 * @author Rhys Evans
 * @version 0.2
 */
public class Booking {

    @SerializedName("_id")
    private String id;

    @SerializedName("pickup_location")
    private String pickupLocation;

    @SerializedName("destination")
    private String destination;

    @SerializedName("time")
    private Date time;

    @SerializedName("no_passengers")
    private int noPassengers;

    @SerializedName("notes")
    private String notes;

    @SerializedName("status")
    private BookingStatus status;

    @SerializedName("customer")
    private User customer;

    @SerializedName("driver")
    private User driver;

    @SerializedName("created_at")
    private Date createdAt;

    /**
     * Default empty constructor
     */
    public Booking(){

    }

    /**
     * Overloaded constructor to create a booking with the minimum required information
     * @param pickupLocation
     * @param destination
     * @param time
     * @param noPassengers
     */
    public Booking(String pickupLocation, String destination, Date time, int noPassengers){
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.time = time;
        this.noPassengers = noPassengers;
    }

    /**
     * Get the pickup location of the booking
     * @return
     */
    public String getPickupLocation() {
        return pickupLocation;
    }

    /**
     * Set the pickup location of the booking
     * @param pickupLocation
     */
    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    /**
     * Get the destination of the booking
     * @return
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Set the destination of the boooking
     * @param destination
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Get the time of the booking
     * @return
     */
    public Date getTime() {
        return time;
    }

    /**
     * Set the time of the booking
     * @param time
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     *
     * Get the number of passengers for the booking
     * @return
     */
    public int getNoPassengers() {
        return noPassengers;
    }

    /**
     * Set the number of passengers for the booking
     * @param noPassengers
     */
    public void setNoPassengers(int noPassengers) {
        this.noPassengers = noPassengers;
    }

    /**
     * Get any notes for the booking
     * @return
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Set any notes for the booking
     * @param notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Get the status of the booking
     * @return
     */
    public BookingStatus getStatus() {
        return status;
    }

    /**
     * Set the status of the booking
     * @param status
     */
    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    /**
     * Get the customer of the booking (ID)
     * @return
     */
    public User getCustomer() {
        return customer;
    }

    /**
     * Set the customer of the booking (ID)
     * @param customer
     */
    public void setCustomer(User customer) {
        this.customer = customer;
    }

    /**
     * Get the driver responsible for the booking (ID)
     * @return
     */
    public User getDriver() {
        return driver;
    }

    /**
     * Set the driver responsible for the booking (ID)
     * @param driver
     */
    public void setDriver(User driver) {
        this.driver = driver;
    }

    /**
     * Get the date the booking was created
     * @return
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the ID of a booking
     * @param id
     */
    public void setId(String id){
        this.id = id;
    }

    /**
     * Get the ID of the booking object
     * @return
     */
    public String getId(){
        return id;
    }
}
