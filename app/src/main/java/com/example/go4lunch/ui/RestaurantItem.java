package com.example.go4lunch.ui;

import com.example.go4lunch.model.Restaurant;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

// n'est pas un modele
public class RestaurantItem implements Serializable, Comparable<RestaurantItem> {
    private String name;
    private String address;
    private double rating;
    private String photoUrl;

    private Double distance;
    private int NbParticipant;

    private Restaurant origin;

    public RestaurantItem(String name, String address, double rating, String photoUrl, Double distance, int nbParticipant, Restaurant origin) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.photoUrl = photoUrl;
        this.distance = distance;
        NbParticipant = nbParticipant;
        this.origin = origin;
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

    public Double getDistance() {
        return distance;
    }

    public int getNbParticipant() {
        return NbParticipant;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setNbParticipant(int nbParticipant) {
        NbParticipant = nbParticipant;
    }

    public Restaurant getOrigin() {
        return origin;
    }

    // compare la distance entre deux objets
    @Override
    public int compareTo(RestaurantItem other) {
        return this.distance.compareTo(other.distance);
    }

}
