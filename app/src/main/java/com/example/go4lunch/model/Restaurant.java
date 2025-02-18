package com.example.go4lunch.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Model class representing a restaurant item.
 */
public class Restaurant implements Serializable {
    private String id; // Id of restaurant
    private String name; // Name of restaurant
    private String address; // Address of restaurant
    private String photo; // Photo of restaurant
    private String openingHours; // Opening hours of restaurant
    private Double stars; // Popularity of tge restaurant represented by stars
    private String website; // Website of restaurant
    private String typeOfRestaurant; // Type of restaurant
    private String telephone; // Telephone of restaurant
    private Double latitude; // latitude coordinate of restaurant
    private Double longitude; // longitude coordinate of restaurant
    /**
     * Get the star rating of the restaurant.
     * @return The star rating.
     */
    public Double getStars() {
        return stars;
    }

    /**
     * Set the star rating of the restaurant.
     * @param stars The star rating.
     */
    public void setStars(Double stars) {
        this.stars = stars;
    }

    /**
     * Get the latitude coordinate of the restaurant.
     * @return The latitude coordinate.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Set the latitude coordinate of the restaurant.
     * @param latitude The latitude coordinate.
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Get the longitude coordinate of the restaurant.
     * @return The longitude coordinate.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Set the longitude coordinate of the restaurant.
     * @param longitude The longitude coordinate.
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Get the unique identifier of the restaurant.
     * @return The unique identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the unique identifier of the restaurant.
     * @param id The unique identifier.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the name of the restaurant.
     * @return The name of the restaurant.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the restaurant.
     * @param name The name of the restaurant.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the address of the restaurant.
     * @return The address of the restaurant.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the address of the restaurant.
     * @param address The address of the restaurant.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the photo URL of the restaurant.
     * @return The photo URL.
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Set the photo URL of the restaurant.
     * @param photo The photo URL.
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Get the opening hours of the restaurant.
     * @return The opening hours.
     */
    public String getOpeningHours() {
        return openingHours;
    }

    /**
     * Set the opening hours of the restaurant.
     * @param openingHours The opening hours.
     */
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    /**
     * Get the website URL of the restaurant.
     * @return The website URL.
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Set the website URL of the restaurant.
     * @param website The website URL.
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Get the type of the restaurant (e.g., Fast Food, Italian, etc.).
     * @return The type of the restaurant.
     */
    public String getTypeOfRestaurant() {
        return typeOfRestaurant;
    }

    /**
     * Set the type of the restaurant.
     * @param typeOfRestaurant The type of the restaurant.
     */
    public void setTypeOfRestaurant(String typeOfRestaurant) {
        this.typeOfRestaurant = typeOfRestaurant;
    }

    /**
     * Get the telephone number of the restaurant.
     * @return The telephone number.
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Set the telephone number of the restaurant.
     * @param telephone The telephone number.
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Constructor to initialize a restaurant object with basic attributes.
     * @param id The unique identifier of the restaurant.
     * @param name The name of the restaurant.
     * @param address The address of the restaurant.
     * @param photo The photo URL of the restaurant.
     * @param openingHours The opening hours of the restaurant.
     * @param stars The rating stars of the restaurant.
     * @param typeOfRestaurant The type of the restaurant.
     */
    public Restaurant(String id, String name, String address, String photo, String openingHours, Double stars, String typeOfRestaurant) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.openingHours = openingHours;
        this.stars = stars;
        this.typeOfRestaurant = typeOfRestaurant;
    }

    /**
     * Constructor to initialize a restaurant object with website info.
     * @param id The unique identifier of the restaurant.
     * @param name The name of the restaurant.
     * @param address The address of the restaurant.
     * @param photo The photo URL of the restaurant.
     * @param openingHours The opening hours of the restaurant.
     * @param stars The rating stars of the restaurant.
     * @param website The website URL of the restaurant.
     * @param typeOfRestaurant The type of the restaurant.
     */
    public Restaurant(String id, String name, String address, String photo, String openingHours, Double stars, String website, String typeOfRestaurant) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.openingHours = openingHours;
        this.stars = stars;
        this.website = website;
        this.typeOfRestaurant = typeOfRestaurant;
    }

    /**
     * Default constructor.
     */
    public Restaurant() {
    }
}