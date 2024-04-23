package com.example.go4lunch.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * This class represents a meal taken by a user at a specific restaurant on a specific date.
 */
public class Lunch {

    /** User who took the meal */
    private User user;

    /** Restaurant where the meal was taken */
    private Restaurant restaurant;

    /** Date when the meal was taken */
    private Date dayDate;

    /**
     * Get the user who took the meal.
     * @return The user who took the meal.
     */
    public User getUser() {
        return user;
    }

    /**
     * Set the user who took the meal.
     * @param user The user who took the meal.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get the restaurant where the meal was taken.
     * @return The restaurant where the meal was taken.
     */
    public Restaurant getRestaurant() {
        return restaurant;
    }

    /**
     * Set the restaurant where the meal was taken.
     * @param restaurant The restaurant where the meal was taken.
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    /**
     * Get the date when the meal was taken.
     * @return The date when the meal was taken.
     */
    @ServerTimestamp
    public Date getDayDate() {
        return dayDate;
    }

    /**
     * Set the date when the meal was taken.
     * @param dayDate The date when the meal was taken.
     */
    public void setDayDate(Date dayDate) {
        this.dayDate = dayDate;
    }

    /**
     * Default constructor.
     */
    public Lunch() {
        super();
    }

    /**
     * Parameterized constructor to initialize the meal.
     * @param restaurant The restaurant where the meal was taken.
     * @param user The user who took the meal.
     */
    public Lunch(Restaurant restaurant, User user) {
        this();
        this.user = user;
        this.restaurant = restaurant;
    }
}
