package lifegraphy.app;

import java.util.ArrayList;
import java.util.Date;

public class BookingsList {

    private String event_id, client_id,production_id,booking_date,email,message,status,type;




    public BookingsList(String event_id, String client_id, String production_id, String booking_date,String email,String message,String status, String type) {
        this.event_id = event_id;
        this.client_id = client_id;
        this.production_id = production_id;
        this.booking_date = booking_date;
        this.email = email;
        this.message = message;
        this.status = status;
        this.type = type;

    }

    public String getBooking_date() {

        return booking_date;
    }

    public String getEvent_id() {

        return event_id;
    }



    public String getClient_id() {
        return client_id;

    }



    public String getProduction_id() {

        return production_id;

    }

    public String getEmail() {

        return email;
    }



    public String getMessage() {
        return message;

    }



    public String getStatus() {

        return status;

    }

    public String getType() {

        return type;

    }









}