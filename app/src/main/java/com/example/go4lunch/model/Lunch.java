package com.example.go4lunch.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Lunch {


    /** Lunch User */
    private User user;

    /** Lunch Restaurant */
    private Restaurant restaurant;

    /** Lunch dayDate */
    private Date dayDate;


    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @ServerTimestamp
    public Date getDayDate() {
        return dayDate;
    }
    public void setDayDate(Date dayDate) {
        this.dayDate = dayDate;
    }


    /**
     * Constructor
     */
    public Lunch() {
        super();
    }


    public Lunch(Restaurant restaurant, User user) {
        this();
        this.user = user;
        this.restaurant = restaurant;
    }
}
