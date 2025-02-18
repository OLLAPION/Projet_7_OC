package com.example.go4lunch.services;

import com.example.go4lunch.pojo.RestaurantsAnswer;
import com.example.go4lunch.pojo.Result;
import com.example.go4lunch.pojo.ResultDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * RetrofitMapsApi is an interface for defining network requests using Retrofit.
 * It communicates with the Google Places API to retrieve restaurant information.
 */
public interface RetrofitMapsApi {

    /**
     * Retrieves a list of restaurants near a specific location.
     *
     * @param location The latitude and longitude of the location (e.g., "37.7749,-122.4194").
     * @param radius   The search radius in meters.
     * @param type     The type of place to search for (e.g., "restaurant").
     * @param key      The API key for authenticating the request.
     * @return A Call object for fetching RestaurantsAnswer containing a list of nearby restaurants.
     */
    @GET("nearbysearch/json")
    Call<RestaurantsAnswer> getAllRestaurants(@Query("location") String location,
                                              @Query("radius") int radius,
                                              @Query("type") String type,
                                              @Query("key") String key);

    /**
     * Retrieves detailed information about a specific restaurant.
     *
     * @param key      The API key for authenticating the request.
     * @param placeId  The unique identifier of the restaurant (place ID from Google Places).
     * @return A Call object for fetching ResultDetails containing detailed restaurant information.
     */
    @GET("details/json")
    Call<ResultDetails> getRestaurantDetails(@Query("key") String key,
                                             @Query("place_id") String placeId);
}

