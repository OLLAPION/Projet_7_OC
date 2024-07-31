package com.example.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.pojo.RestaurantsAnswer;
import com.example.go4lunch.services.RetrofitMapsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {
    private RetrofitMapsApi retrofitMapsApi;

    public RestaurantRepository(RetrofitMapsApi retrofitMapsApi) {
        this.retrofitMapsApi = retrofitMapsApi;
    }

    public LiveData<RestaurantsAnswer> getAllRestaurants(String location, int radius, String type, String key) {
        MutableLiveData<RestaurantsAnswer> data = new MutableLiveData<>();
        retrofitMapsApi.getAllRestaurants(location, radius, type, key).enqueue(new Callback<RestaurantsAnswer>() {
            @Override
            public void onResponse(Call<RestaurantsAnswer> call, Response<RestaurantsAnswer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RestaurantsAnswer> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}


