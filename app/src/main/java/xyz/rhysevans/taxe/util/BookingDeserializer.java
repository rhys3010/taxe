package xyz.rhysevans.taxe.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import xyz.rhysevans.taxe.model.Booking;
import xyz.rhysevans.taxe.model.User;

/**
 * BookingDeserializer.java
 *
 * Custom deserializer class to handle dynamic JSON response for bookings
 * Adapted from:
 * http://www.veskoiliev.com/how-to-handle-dynamic-json-response-with-retrofit/?fbclid=IwAR26EEtwq0hUbqxUr1UzyGPyORFfYWaE3gzAmDCZW1y2AH6Mj8cpZO8BP9g
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class BookingDeserializer implements JsonDeserializer<Booking> {

    // JSON fields that correspond to Booking POJO
    private final String ID_KEY = "_id";
    private final String PICKUP_LOCATION_KEY = "pickup_location";
    private final String DESTINATION_KEY = "destination";
    private final String TIME_KEY = "time";
    private final String NO_PASSENGERS_KEY = "no_passengers";
    private final String NOTES_KEY = "notes";
    private final String STATUS_KEY = "status";
    private final String CUSTOMER_KEY = "customer";
    private final String DRIVER_KEY = "driver";
    private final String CREATED_AT_KEY = "created_at";


    /**
     * Override default deserialize object to handle dynamic customer and driver fields
     * @param json
     * @param typeOfT
     * @param context
     * @return
     * @throws JsonParseException
     */
    @Override
    public Booking deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        // Read the *simple* static fields
        final String id = jsonObject.get(ID_KEY).getAsString();
        final String pickupLocation = jsonObject.get(PICKUP_LOCATION_KEY).getAsString();
        final String destination = jsonObject.get(DESTINATION_KEY).getAsString();
        final int noPassengers = jsonObject.get(NO_PASSENGERS_KEY).getAsInt();
        final BookingStatus status = BookingStatus.valueOf(jsonObject.get(STATUS_KEY).getAsString());

        // Read

        // OPTIONAL Fields
        String notes = "";
        if(jsonObject.get(NOTES_KEY) != null){
            notes = jsonObject.get(NOTES_KEY).getAsString();
        }

        // Read dynamic content (String OR Object)
        final User customer = readUser(jsonObject, CUSTOMER_KEY, context);
        final User driver = readUser(jsonObject, DRIVER_KEY, context);

        // Read dates
        Date time = null;
        Date createdAt = null;
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.UK);

        try{
            time = format.parse(jsonObject.get(TIME_KEY).getAsString());
            createdAt = format.parse(jsonObject.get(CREATED_AT_KEY).getAsString());
        }catch(ParseException e){
            e.printStackTrace();
        }

        // Create a new Booking object and populate it
        Booking booking = new Booking();
        booking.setId(id);
        booking.setPickupLocation(pickupLocation);
        booking.setDestination(destination);
        booking.setNoPassengers(noPassengers);
        booking.setNotes(notes);
        booking.setStatus(status);
        booking.setDriver(driver);
        booking.setCustomer(customer);
        booking.setTime(time);

        return booking;
    }

    /**
     * Read user fields that could either contain a string or JSON object
     * e.g. Custmer and Driver fields (could either be ID or a populated json object)
     * @param jsonObject
     * @param userFieldKey
     * @param context
     * @return User object
     */
    private User readUser(JsonObject jsonObject, String userFieldKey, JsonDeserializationContext context){
        final JsonElement userElement = jsonObject.get(userFieldKey);
        // The output User object
        User user;

        if(userElement == null){
            return null;
        }

        // If the type of the json element is object, deserialize it..
        if(userElement.isJsonObject()){
            user = context.deserialize(userElement, User.class);

            // If it isn't read as string and create a new user object with just that info
        }else{
            user = new User();
            user.setId(userElement.getAsString());
        }

        return user;
    }
}
