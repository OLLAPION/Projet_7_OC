package com.example.go4lunch.services;

import com.example.go4lunch.pojo.RestaurantsAnswer;
import com.example.go4lunch.pojo.Result;
import com.example.go4lunch.pojo.ResultDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitMapsApi {
    @GET("nearbysearch/json")
    Call<RestaurantsAnswer> getAllRestaurants(@Query("location") String location,
                                              @Query("radius") int radius,
                                              @Query("type") String type,
                                              @Query("key") String key);

    @GET("details/json")
    Call<ResultDetails> getRestaurantDetails(@Query("key") String key,
                                             @Query("place_id") String placeId);
}

