package xyz.rhysevans.taxe.model;

import java.util.Date;

/**
 * Booking.java
 *
 * Booking Model Class that maps onto a Mongoose / MongoDB model in the API.
 *
 * @author Rhys Evans
 * @version 0.2
 */
public class Booking {

    /**
     * The pickup location of the booking
     * (REQUIRED)
     */
    private String pickup_location;

    /**
     * The destination of the booking
     * (REQUIRED)
     */
    private String destination;

    /**
     * The desired time of the booking
     * (REQUIRED)
     */
    private Date time;

    /**
     * The number of passengers for the booking
     * (REQUIRED, Default = 1)
     */
    private int no_passengers;

    /**
     * Any notes the user wants to add to booking i.e. child seat etc.
     * (Optional)
     */
    private String notes;

    /**
     * The booking's status - not required for creation
     */
    private String status;

    /**
     * The customer of the booking
     */
    private String customer;

    /**
     * The driver responsible for the booking
     */
    private String driver;

    /**
     * The creation date of the booking
     */
    private Date created_at;

    /**
     * Default empty constructor
     */
    public Booking(){

    }

    /**
     * Overloaded constructor to create a booking with the minimum required information
     * @param pickup_location
     * @param destination
     * @param time
     * @param no_passengers
     */
    public Booking(String pickup_location, String destination, Date time, int no_passengers){
        this.pickup_location = pickup_location;
        this.destination = destination;
        this.time = time;
        this.no_passengers = no_passengers;
    }

    /**
     * Get the pickup location of the booking
     * @return
     */
    public String getPickup_location() {
        return pickup_location;
    }

    /**
     * Set the pickup location of the booking
     * @param pickup_location
     */
    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
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
    public int getNo_passengers() {
        return no_passengers;
    }

    /**
     * Set the number of passengers for the booking
     * @param no_passengers
     */
    public void setNo_passengers(int no_passengers) {
        this.no_passengers = no_passengers;
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
    public String getStatus() {
        return status;
    }

    /**
     * Set the status of the booking
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the customer of the booking (ID)
     * @return
     */
    public String getCustomer() {
        return customer;
    }

    /**
     * Set the customer of the booking (ID)
     * @param customer
     */
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    /**
     * Get the driver responsible for the booking (ID)
     * @return
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Set the driver responsible for the booking (ID)
     * @param driver
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * Get the date the booking was created
     * @return
     */
    public Date getCreated_at() {
        return created_at;
    }
}
