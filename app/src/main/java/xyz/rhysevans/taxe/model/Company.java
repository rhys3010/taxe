/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Company.java
 *
 * Model class for the Company object.
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class Company {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("bookings")
    private String[] bookings;

    @SerializedName("drivers")
    private String[] drivers;

    @SerializedName("admins")
    private String[] admins;

    @SerializedName("created_at")
    private Date createdAt;

    /**
     * Get a company's ID
     */
    public String getId(){
        return this.id;
    }

    /**
     * Returns the ID of a company
     * @param id
     */
    public void setId(String id){
        this.id = id;
    }

    /**
     * Get the name of a company object
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of a company object
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get a list of a company's bookings
     * @return
     */
    public String[] getBookings() {
        return bookings;
    }

    /**
     * Get a list of a company's drivers
     * @return
     */
    public String[] getDrivers() {
        return drivers;
    }

    /**
     * Get a list of a company's admins
     * @return
     */
    public String[] getAdmins() {
        return admins;
    }

    /**
     * Get the company's creation date
     * @return
     */
    public Date getCreatedAt() {
        return createdAt;
    }
}
