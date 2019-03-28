package xyz.rhysevans.taxe.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import xyz.rhysevans.taxe.util.Roles;

/**
 * User.java
 *
 * User model class, that maps onto values in the User document in MongoDB. Also used to save
 * user information in shared preferences.
 * @author Rhys Evans
 * @version 0.1
 */
public class User{


    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    private String role;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("bookings")
    private String[] bookings;


    /**
     * Default user constructor to allow empty users
     */
    public User(){}

    /**
     * Overloaded user constructor to create user with information
     * provided at registration
     * @param name
     * @param email
     * @param password
     */
    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Another overloaded user constructor to create user for storage in shared preferences
     * @param id
     * @param name
     * @param email
     * @param role
     * @param createdAt
     */
    public User(String id, String name, String email, String role, Date createdAt){
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }


    /**
     * Set the object ID of the user
     * @param id
     */
    public void setId(String id){
        this.id = id;
    }

    /**
     * Set the name of the user being created
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Set the email of the user that's being created
     * @param email
     */
    public void setEmail(String email){
        this.email = email;
    }

    /**
     * Set the password of the user that's being created
     * @param password
     */
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * Set the role of the user that's being created
     * @param role
     */
    public void setRole(Roles role){
        // Convert role enum to string for API request
        this.role = role.name();
    }


    /**
     * Gets the name of the user being created
     * @return name
     */
    public String getName(){
        return this.name;
    }

    /**
     * Gets the email of the user being created
     * @return email
     */
    public String getEmail(){
        return this.email;
    }

    /**
     * Gets the password of the user being created
     * @return password
     */
    public String getPassword(){
        return this.password;
    }

    /**
     * Gets the role of the user being created
     * @return
     */
    public String getRole(){
        return this.role;
    }

    /**
     * Gets the creation date of the user
     * @return
     */
    public Date getCreatedAt(){
        return this.createdAt;
    }

    /**
     * Gets the user's object ID.
     * @return
     */
    public String getId(){
        return this.id;
    }

    /**
     * Gets the bookings the user has made
     * @return
     */
    public String[] getBookings(){
        return this.bookings;
    }

}
