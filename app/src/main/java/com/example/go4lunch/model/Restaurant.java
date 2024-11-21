package com.example.go4lunch.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Model class representing a restaurant item.
 */
public class Restaurant implements Serializable {

    /** Unique identifier of the restaurant */
    private String id;

    /** Name of the restaurant */
    private String name;

    /** Address of the restaurant */
    private String address;

    /** Photo of the restaurant */
    private String photo;

    /** Opening hours of the restaurant */
    private String openingHours;

    /** Rating stars of the restaurant */
    private Double stars;

    /** Website of the restaurant */
    private String website;

    /** Type of restaurant */
    private String typeOfRestaurant;

    /** Type of restaurant */
    private String telephone;
    private Double latitude;
    private Double longitude;

    public Double getStars() {
        return stars;
    }

    public void setStars(Double stars) {
        this.stars = stars;
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

    /**
     * Get the unique identifier of the restaurant.
     * @return The unique identifier of the restaurant.
     */
    public String getId() {
        return id;
    }
    /**
     * Set the unique identifier of the restaurant.
     * @param id The unique identifier of the restaurant.
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
     * Get the photo of the restaurant.
     * @return The photo of the restaurant.
     */
    public String getPhoto() {
        return photo;
    }
    /**
     * Set the photo of the restaurant.
     * @param photo The photo of the restaurant.
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Get the opening hours of the restaurant.
     * @return The opening hours of the restaurant.
     */
    public String getOpeningHours() {
        return openingHours;
    }
    /**
     * Set the opening hours of the restaurant.
     * @param openingHours The opening hours of the restaurant.
     */
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }


    /**
     * Get the website of the restaurant.
     * @return The website of the restaurant.
     */
    public String getWebsite() {
        return website;
    }
    /**
     * Set the website of the restaurant.
     * @param website The website of the restaurant.
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Get the type of restaurant.
     * @return The type of restaurant.
     */
    public String getTypeOfRestaurant() {
        return typeOfRestaurant;
    }
    /**
     * Set the type of restaurant.
     * @param typeOfRestaurant The type of restaurant.
     */
    public void setTypeOfRestaurant(String typeOfRestaurant) {
        this.typeOfRestaurant = typeOfRestaurant;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Constructor to initialize a restaurant object with all attributes.
     * @param id The unique identifier of the restaurant.
     * @param name The name of the restaurant.
     * @param address The address of the restaurant.
     * @param photo The photo of the restaurant.
     * @param openingHours The opening hours of the restaurant.
     * @param stars The rating stars of the restaurant.
     * @param typeOfRestaurant The type of restaurant.
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

    public Restaurant (){

    }

    // 1er tentative du RestaurantRepository
    /*
    public static List<Restaurant> fromResults(List<Result> results) {
        List<Restaurant> restaurants = new ArrayList<>();
        for (Result result : results) {
            restaurants.add(new Restaurant(
                    result.getPlaceId(),
                    result.getName(),
                    result.getVicinity(),
                    result.getPhotoUrl(),
                    result.getOpeningHours(),
                    result.getRating(),
                    result.getWebsite(),
                    result.getTypeOfRestaurant()
            ));
        }
        return restaurants;
    }

     */

    /*
    // pour test 1 dans ListRestaurantFragment le fetchRestaurant !
    public static Restaurant fromMap(Map<String, Object> args) {
        return new Restaurant(
                (String) args.get("placeId"),
                (String) args.get("name"),
                (String) args.get("vicinity"),
                (String) args.get("photoUrl"),
                (String) args.get("openingHours"),
                (String) args.get("rating"),
                (String) args.get("website"),
                (String) args.get("typeOfRestaurant")
        );
    }

     */

}
