package com.example.go4lunch.model;

import java.util.Date;

public class Lunch {

    /** Lunch Identifier */
    // int ou Interger ???
    private int id;

    /** Lunch User */
    private User user;

    /** Lunch Restaurant */
    private Restaurant restaurant;

    /** Lunch dayDate */
    private Date dayDate;



    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

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

    public Date getDayDate() {
        return dayDate;
    }
    public void setDayDate(Date dayDate) {
        this.dayDate = dayDate;
    }


    /**
     * Constructor
     */
    public Lunch(int id, User user, Restaurant restaurant, Date dayDate) {
        this.id = id;
        this.user = user;
        this.restaurant = restaurant;
        this.dayDate = dayDate;
    }
}
