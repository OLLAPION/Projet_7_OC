package com.example.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.pojo.RestaurantsAnswer;
import com.example.go4lunch.pojo.Result;
import com.example.go4lunch.pojo.ResultDetails;
import com.example.go4lunch.services.RetrofitMapsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {
    private final RetrofitMapsApi retrofitMapsApi;

    public RestaurantRepository(RetrofitMapsApi retrofitMapsApi) {
        this.retrofitMapsApi = retrofitMapsApi;
    }

    public LiveData<List<Restaurant>> getAllRestaurants(String location, int radius, String type, String key) {
        MutableLiveData<List<Restaurant>> liveDataAllRestaurants = new MutableLiveData<>();

        retrofitMapsApi.getAllRestaurants(location, radius, type, key).enqueue(new Callback<RestaurantsAnswer>() {
            @Override
            public void onResponse(Call<RestaurantsAnswer> call, Response<RestaurantsAnswer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Result> results = response.body().getResults();
                    List<Restaurant> restaurantList = new ArrayList<>();

                    // Transformer les Result en Restaurant
                    for (Result result : results) {
                        Restaurant restaurant = new Restaurant(

                                result.getPlaceId(),
                                result.getName(),
                                result.getVicinity(),
                                result.getPhotos() != null && !result.getPhotos().isEmpty()
                                        ? result.getPhotos().get(0).getPhotoReference()
                                        : null,
                                result.getOpeningHours() != null
                                        ? result.getOpeningHours().getOpenNow().toString()
                                        : null,
                                result.getRating() != null
                                        ? result.getRating()
                                        : null,
                                result.getTypes() != null && !result.getTypes().isEmpty()
                                        ? result.getTypes().get(0)
                                        : null


                        );

                        if (result.getGeometry() != null && result.getGeometry().getLocation() != null){
                            restaurant.setLatitude(result.getGeometry().getLocation().getLat());
                            restaurant.setLongitude(result.getGeometry().getLocation().getLng());
                        }


                        restaurantList.add(restaurant);
                    }

                    liveDataAllRestaurants.setValue(restaurantList);
                } else {
                    liveDataAllRestaurants.setValue(null);  // Si erreur ou réponse vide
                }
            }

            @Override
            public void onFailure(Call<RestaurantsAnswer> call, Throwable t) {
                liveDataAllRestaurants.setValue(null);  // En cas d'échec
            }
        });

        return liveDataAllRestaurants;
    }

    public LiveData<Restaurant> getRestaurantDetail(String key, String placeId) {
        MutableLiveData<Restaurant> liveDataRestaurant = new MutableLiveData<>();

        retrofitMapsApi.getRestaurantDetails(key, placeId).enqueue(new Callback<ResultDetails>() {
            @Override
            public void onResponse(Call<ResultDetails> call, Response<ResultDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResultDetails results = response.body();
                    Result result = results.getResult();

                        Restaurant restaurant = new Restaurant(
                                result.getPlaceId(),
                                result.getName(),
                                result.getVicinity(),
                                result.getPhotos() != null && !result.getPhotos().isEmpty()
                                        ? result.getPhotos().get(0).getPhotoReference()
                                        : null,
                                result.getOpeningHours() != null
                                        ? result.getOpeningHours().getOpenNow().toString()
                                        : null,
                                result.getRating() != null
                                        ? result.getRating()
                                        : null,
                                result.getTypes() != null && !result.getTypes().isEmpty()
                                        ? result.getTypes().get(0)
                                        : null

                        );
                        restaurant.setWebsite(result.getWebsite());
                        restaurant.setTelephone(result.getFormatted_phone_number());

                    liveDataRestaurant.setValue(restaurant);
                } else {
                    liveDataRestaurant.setValue(null);  // Si erreur ou réponse vide
                }
            }

            @Override
            public void onFailure(Call<ResultDetails> call, Throwable t) {
                liveDataRestaurant.setValue(null);  // En cas d'échec
            }
        });

        return liveDataRestaurant;
    }
}
