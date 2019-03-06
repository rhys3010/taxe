package xyz.rhysevans.taxe.network;


import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;
import xyz.rhysevans.taxe.model.Response;
import xyz.rhysevans.taxe.model.User;


/**
 * RetrofitInterface.java
 *
 * Declare all of the API's endpoints that we will use. Each method will return an Observable
 * that will be subscribed to. This class will provide a layer of abstraction between
 * the client and the API requests, with friendlier method names e.g. register.
 * @author Rhys Evans
 * @version 0.1
 */
public interface RetrofitInterface {

    /**
     * Get a specific user by their ID
     * @param id
     * @return
     */
    @GET("users/{id}")
    Observable<User> getUser(@Path("id") String id);

    /**
     * Register a new user with user model as body
     * @param user
     * @return
     */
    @POST("users")
    Observable<Response> register(@Body User user);

    /**
     * Authenticate credentials as Basic-Auth header. Hopefully
     * receive token in response
     * @return
     */
    @POST("users/login")
    Observable<Response> login();

    /**
     * Edit a user record
     * @param id
     * @param user
     * @return
     */
    @PUT("users/{id}")
    Observable<Response> editUser(@Path("id") String id, @Body User user);
}