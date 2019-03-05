package xyz.rhysevans.taxe.model;

import xyz.rhysevans.taxe.util.Roles;

/**
 * User.java
 *
 * User model class, that maps onto values in the User document in MongoDB
 * @author Rhys Evans
 * @version 0.1
 */
public class User {

    /**
     * The user's full name
     */
    private String name;

    /**
     * The user's email
     */
    private String email;

    /**
     * The user's password
     */
    private String password;

    /**
     * The user's role
     */
    private String role;

    /**
     * The date the user's record was created
     */
    private String created_at;

    /**
     * The user's current access token
     */
    private String token;

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
     * Sets the access token of the user
     * @param token
     */
    public void setToken(String token){
        this.token = token;
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
    public String getCreated_at(){
        return this.created_at;
    }

}
