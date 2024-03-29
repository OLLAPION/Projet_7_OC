package com.example.go4lunch.repository;

import com.example.go4lunch.pojo.RestaurantsAnswer;
import com.example.go4lunch.pojo.Result;
import com.example.go4lunch.services.RetrofitMapsApi;

import retrofit2.Call;

public class RestaurantRepository {
    private RetrofitMapsApi retrofitMapsApi;

    public RestaurantRepository(RetrofitMapsApi retrofitMapsApi) {
        this.retrofitMapsApi = retrofitMapsApi;
    }

    public Call<RestaurantsAnswer> getAllRestaurants(String location, int radius, String type, String key) {
        return retrofitMapsApi.getAllRestaurants(location, radius, type, key);
    }
}

