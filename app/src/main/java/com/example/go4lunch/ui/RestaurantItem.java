package com.example.go4lunch.ui;

import java.io.Serializable;
// n'est pas un modele
public class RestaurantItem implements Serializable {
    private String name;
    private String address;
    private double rating;
    private String photoUrl;

    public RestaurantItem(String name, String address, double rating, String photoUrl) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.photoUrl = photoUrl;
    }

    // Getters and setters
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
