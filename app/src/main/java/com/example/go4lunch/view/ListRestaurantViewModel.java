package com.example.go4lunch.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.MainApplication;
import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.pojo.RestaurantsAnswer;
import com.example.go4lunch.pojo.Result;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.services.RetrofitService;
import com.example.go4lunch.ui.RestaurantItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for managing the list of restaurants.
 */
public class ListRestaurantViewModel extends ViewModel {

    /** LiveData holding the list of RestaurantItems to be observed by the UI. */
    private MutableLiveData<List<RestaurantItem>> restaurantListLiveData = new MutableLiveData<>();

    /** Repository for fetching restaurant data from the network. */
    private RestaurantRepository restaurantRepository;

    /** Repository for managing lunch data. */
    private LunchRepository lunchRepository;

    /** API key for Google Maps. */
    private String apiKey;
    /**
     * Constructor initializing repositories and API key.
     */
    public ListRestaurantViewModel() {
        restaurantRepository = new RestaurantRepository(RetrofitService.getRestaurantApi());
        lunchRepository = LunchRepository.getInstance(MainApplication.getApplication());
        apiKey = BuildConfig.google_maps_api;
    }

    /**
     * Gets the LiveData object for observing the list of RestaurantItems.
     * @return LiveData holding the list of RestaurantItems.
     */
    public LiveData<List<RestaurantItem>> getRestaurantListLiveData() {
        return restaurantListLiveData;
    }

    public LiveData<List<Restaurant>> getAllRestaurants(String location, int radius, String type, String key) {
        return restaurantRepository.getAllRestaurants(location, radius, type, key);
    }

    public LiveData<Restaurant> getRestaurantDetail(String key, String placeId) {
        return restaurantRepository.getRestaurantDetail(key, placeId);
    }

    public LiveData<List<Lunch>> getLunchesForToday(){
        return lunchRepository.getLunchesForToday();
    }
}