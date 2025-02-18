package com.example.go4lunch.ui;

import com.example.go4lunch.model.Restaurant;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Represents a restaurant item with detailed information including name, address, rating, photo URL,
 * distance, number of participants, and geographic coordinates.
 * This class is used to store and manipulate restaurant data within the application.
 * It also implements Comparable to allow sorting of restaurants based on their distance.
 */
public class RestaurantItem implements Serializable, Comparable<RestaurantItem> {

    // Name of the restaurant
    private String name;

    // Address of the restaurant
    private String address;

    // Rating of the restaurant
    private Double rating;

    // Photo URL of the restaurant
    private String photoUrl;

    // Distance from the user's current location to the restaurant
    private Double distance;

    // Number of participants interested in the restaurant
    private Integer NbParticipant;

    // Latitude of the restaurant's location
    private Double latitude;

    // Longitude of the restaurant's location
    private Double longitude;

    // Original Restaurant object from the model
    private Restaurant origin;

    /**
     * Constructs a new RestaurantItem with the provided details.
     *
     * @param name        Name of the restaurant
     * @param address     Address of the restaurant
     * @param rating      Rating of the restaurant
     * @param photoUrl    URL to the restaurant's photo
     * @param distance    Distance from the user to the restaurant
     * @param nbParticipant  Number of participants for the restaurant
     * @param latitude    Latitude of the restaurant
     * @param longitude   Longitude of the restaurant
     * @param origin      Original Restaurant object that this item represents
     */
    public RestaurantItem(String name, String address, Double rating, String photoUrl, Double distance, Integer nbParticipant, Double latitude, Double longitude, Restaurant origin) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.photoUrl = photoUrl;
        this.distance = distance;
        NbParticipant = nbParticipant;
        this.latitude = latitude;
        this.longitude = longitude;
        this.origin = origin;
    }

    // Getters ans Setters
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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getNbParticipant() {
        return NbParticipant;
    }

    public void setNbParticipant(Integer nbParticipant) {
        NbParticipant = nbParticipant;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Restaurant getOrigin() {
        return origin;
    }

    public void setOrigin(Restaurant origin) {
        this.origin = origin;
    }

    /**
     * Compares two RestaurantItem objects based on their distance from the user.
     *
     * @param other The other RestaurantItem to compare with
     * @return A negative integer, zero, or a positive integer if this RestaurantItem is
     *         closer, equally distant, or farther than the other one, respectively.
     */
    @Override
    public int compareTo(RestaurantItem other) {
        if (this.distance == null || other.distance == null)
            return -1;
        return this.distance.compareTo(other.distance);
    }

    /**
     * Returns a string representation of the RestaurantItem object.
     *
     * @return A string representing the restaurant's details.
     */
    @Override
    public String toString() {
        return "RestaurantItem{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rating=" + rating +
                ", photoUrl='" + photoUrl + '\'' +
                ", distance=" + distance +
                ", NbParticipant=" + NbParticipant +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", origin=" + origin +
                '}';
    }
}
