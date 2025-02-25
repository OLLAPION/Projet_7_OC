package com.example.go4lunch.model.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.bo.Restaurant;
import com.example.go4lunch.model.pojo.RestaurantsAnswer;
import com.example.go4lunch.model.pojo.Result;
import com.example.go4lunch.model.pojo.ResultDetails;
import com.example.go4lunch.model.services.RetrofitMapsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository responsible for fetching restaurant data from the API.
 * Provides methods to retrieve a list of restaurants and details of a specific restaurant.
 */
public class RestaurantRepository {
    // Instance of RetrofitMapsApi used for making network requests to the external API.
    private final RetrofitMapsApi retrofitMapsApi;

    /**
     * Constructor to initialize the repository with RetrofitMapsApi instance.
     * @param retrofitMapsApi The RetrofitMapsApi instance used for making network requests.
     */
    public RestaurantRepository(RetrofitMapsApi retrofitMapsApi) {
        this.retrofitMapsApi = retrofitMapsApi;
    }

    /**
     * Fetches a list of restaurants near a specified location within a certain radius.
     * @param location The location (latitude, longitude) to search for nearby restaurants.
     * @param radius The radius (in meters) within which to search for restaurants.
     * @param type The type of place (e.g., restaurant, cafe).
     * @param key The API key for authentication.
     * @return LiveData object containing a list of restaurants.
     */
    public LiveData<List<Restaurant>> getAllRestaurants(String location, int radius, String type, String key) {
        MutableLiveData<List<Restaurant>> liveDataAllRestaurants = new MutableLiveData<>();

        // Making an asynchronous API call to fetch all restaurants.
        retrofitMapsApi.getAllRestaurants(location, radius, type, key).enqueue(new Callback<RestaurantsAnswer>() {
            @Override
            public void onResponse(Call<RestaurantsAnswer> call, Response<RestaurantsAnswer> response) {
                // Checking if the response is successful and contains valid data.
                if (response.isSuccessful() && response.body() != null) {
                    List<Result> results = response.body().getResults(); // Extracting results from response body
                    List<Restaurant> restaurantList = new ArrayList<>(); // List to store Restaurant objects

                    // Transforming each Result into a Restaurant object.
                    for (Result result : results) {
                        Restaurant restaurant = new Restaurant(

                                result.getPlaceId(),
                                result.getName(),
                                result.getVicinity(),
                                result.getPhotos() != null && !result.getPhotos().isEmpty()
                                        ? result.getPhotos().get(0).getPhotoReference() // Fetching the photo reference if available
                                        : null,
                                result.getOpeningHours() != null
                                        ? result.getOpeningHours().getOpenNow().toString() // Getting opening hours status
                                        : null,
                                result.getRating() != null
                                        ? result.getRating() // Fetching rating if available
                                        : null,
                                result.getTypes() != null && !result.getTypes().isEmpty()
                                        ? result.getTypes().get(0)  // Getting the first type if available
                                        : null


                        );

                        // Fetching location (latitude and longitude) if available in geometry.
                        if (result.getGeometry() != null && result.getGeometry().getLocation() != null){
                            restaurant.setLatitude(result.getGeometry().getLocation().getLat());
                            restaurant.setLongitude(result.getGeometry().getLocation().getLng());
                        }


                        restaurantList.add(restaurant); // Adding the restaurant to the list
                    }

                    liveDataAllRestaurants.setValue(restaurantList); // Setting the result into LiveData
                } else {
                    liveDataAllRestaurants.setValue(null);  // Returning null in case of an error or empty response
                }
            }

            @Override
            public void onFailure(Call<RestaurantsAnswer> call, Throwable t) {
                liveDataAllRestaurants.setValue(null);  // Setting null in case of failure during the API call
            }
        });

        return liveDataAllRestaurants;
    }

    /**
     * Fetches detailed information about a specific restaurant based on its place ID.
     * @param key The API key for authentication.
     * @param placeId The unique place ID of the restaurant.
     * @return LiveData object containing the restaurant details.
     */
    public LiveData<Restaurant> getRestaurantDetail(String key, String placeId) {
        MutableLiveData<Restaurant> liveDataRestaurant = new MutableLiveData<>();

        // Making an asynchronous API call to fetch restaurant details.
        retrofitMapsApi.getRestaurantDetails(key, placeId).enqueue(new Callback<ResultDetails>() {
            @Override
            public void onResponse(Call<ResultDetails> call, Response<ResultDetails> response) {
                // Checking if the response is successful and contains valid data.
                if (response.isSuccessful() && response.body() != null) {
                    ResultDetails results = response.body(); // Extracting the results from the response body
                    Result result = results.getResult(); // Getting the result object

                    // Creating a Restaurant object with the detailed data.
                    Restaurant restaurant = new Restaurant(
                                result.getPlaceId(),
                                result.getName(),
                                result.getVicinity(),
                                result.getPhotos() != null && !result.getPhotos().isEmpty()
                                        ? result.getPhotos().get(0).getPhotoReference() // Fetching the photo reference if available
                                        : null,
                                result.getOpeningHours() != null
                                        ? result.getOpeningHours().getOpenNow().toString() // Getting opening hours status
                                        : null,
                                result.getRating() != null
                                        ? result.getRating() // Fetching rating if available
                                        : null,
                                result.getTypes() != null && !result.getTypes().isEmpty()
                                        ? result.getTypes().get(0) // Getting the first type if available
                                        : null

                        );
                        // Adding additional details such as website and phone number.
                        restaurant.setWebsite(result.getWebsite());
                        restaurant.setTelephone(result.getFormatted_phone_number());

                    liveDataRestaurant.setValue(restaurant); // Setting the restaurant details into LiveData
                } else {
                    liveDataRestaurant.setValue(null);  // Returning null if error or empty response
                }
            }

            @Override
            public void onFailure(Call<ResultDetails> call, Throwable t) {
                liveDataRestaurant.setValue(null);  // Setting null in case of failure during the API call
            }
        });

        return liveDataRestaurant;
    }
}
