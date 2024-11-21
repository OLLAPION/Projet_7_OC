package com.example.go4lunch.ui;

import com.example.go4lunch.model.Restaurant;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

// n'est pas un modele
public class RestaurantItem implements Serializable, Comparable<RestaurantItem> {
    private String name;
    private String address;
    private Double rating;
    private String photoUrl;

    private Double distance;
    private Integer NbParticipant;

    private Double latitude;
    private Double longitude;

    private Restaurant origin;

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

    // compare la distance entre deux objets
    @Override
    public int compareTo(RestaurantItem other) {
        if (this.distance == null || other.distance == null)
            return -1;
        return this.distance.compareTo(other.distance);
    }

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
