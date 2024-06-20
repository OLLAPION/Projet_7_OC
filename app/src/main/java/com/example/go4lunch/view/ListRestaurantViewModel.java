package com.example.go4lunch.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.model.location.GPSStatus;
import com.example.go4lunch.pojo.RestaurantsAnswer;
import com.example.go4lunch.pojo.Result;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.ui.RestaurantItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListRestaurantViewModel extends ViewModel {
    private final RestaurantRepository restaurantRepository;
    private final MutableLiveData<List<RestaurantItem>> restaurantItemLiveData = new MutableLiveData<>();
    private final MutableLiveData<GPSStatus> gpsLiveData = new MutableLiveData<>();

    public ListRestaurantViewModel(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public LiveData<List<RestaurantItem>> getRestaurantItemsLiveData() {
        return restaurantItemLiveData;
    }

    public LiveData<GPSStatus> getGpsLiveData() {
        return gpsLiveData;
    }

    public void fetchRestaurants(double latitude, double longitude) {
        String location = latitude + "," + longitude;
        int radius = 1000;
        String type = "restaurant";
        String apiKey = BuildConfig.google_maps_api;

        restaurantRepository.getAllRestaurants(location, radius, type, apiKey)
                .enqueue(new Callback<RestaurantsAnswer>() {
                    @Override
                    public void onResponse(Call<RestaurantsAnswer> call, Response<RestaurantsAnswer> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Result> results = response.body().getResults();
                            List<RestaurantItem> restaurantItems = new ArrayList<>();
                            for (Result result : results) {
                                String photoUrl = result.getPhotos() != null && !result.getPhotos().isEmpty()
                                        ? "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + result.getPhotos().get(0).getPhotoReference() + "&key=" + apiKey
                                        : null;
                                restaurantItems.add(new RestaurantItem(
                                        result.getName(),
                                        result.getVicinity(),
                                        result.getRating(),
                                        photoUrl
                                ));
                            }
                            restaurantItemLiveData.postValue(restaurantItems);
                        } else {
                            // Handle unsuccessful response
                            restaurantItemLiveData.postValue(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onFailure(Call<RestaurantsAnswer> call, Throwable t) {
                        // Handle failure
                        restaurantItemLiveData.postValue(new ArrayList<>());
                    }
                });
    }

    public void updateGPSStatus(GPSStatus gpsStatus) {
        gpsLiveData.setValue(gpsStatus);
        if (gpsStatus.getLatitude() != null && gpsStatus.getLongitude() != null) {
            fetchRestaurants(gpsStatus.getLatitude(), gpsStatus.getLongitude());
        }
    }
}
