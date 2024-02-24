package com.example.go4lunch.model;

import java.util.Date;
import java.util.List;

/**
 * model item restaurant
 */
public class Restaurant {

    /** restaurant Identifier */
    // int ou Interger ???
    private int id;

    /** restaurant name */
    private String name;

    /** restaurant address */
    private String address;

    /** restaurant photo */
    private String photo;

    /** restaurant openingHours */
    private String openingHours;

    /** restaurant stars */
    private String stars;

    /** restaurant website */
    private String website;

    /** restaurant type of restaurant */
    private String typeOfRestaurant;



    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOpeningHours() {
        return openingHours;
    }
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getStars() {
        return stars;
    }
    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTypeOfRestaurant() {
        return typeOfRestaurant;
    }
    public void setTypeOfRestaurant(String typeOfRestaurant) {
        this.typeOfRestaurant = typeOfRestaurant;
    }


    /**
     * Constructor
     */
    public Restaurant(int id, String name, String address, String photo, String openingHours, String stars, String website, String typeOfRestaurant) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.openingHours = openingHours;
        this.stars = stars;
        this.website = website;
        this.typeOfRestaurant = typeOfRestaurant;
    }

}
