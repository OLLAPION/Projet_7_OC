package com.example.go4lunch.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.MainApplication;
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

    /**
     * Fetches the list of restaurants from the API based on the given location.
     * @param latitude Latitude of the current location.
     * @param longitude Longitude of the current location.
     */
    public void fetchRestaurants(double latitude, double longitude) {
        String location = latitude + "," + longitude;
        int radius = 1000;
        String type = "restaurant";
        // Le mieux est de mettre le context dans un fonction privé
        ListRestaurantViewModel lrvm = this;
        restaurantRepository.getAllRestaurants(location, radius, type, apiKey).observeForever(response -> {
            if (response != null && response.getResults() != null) {
                List<Result> results = response.getResults();
                List<RestaurantItem> restaurantItems = new ArrayList<>();

                lunchRepository.getLunchesForToday().observeForever(lunches -> {
                    if (lunches != null) {
                        for (Result result : results) {
                            if (result != null) {
                                String photoUrl = null;
                                if (result.getPhotos() != null && !result.getPhotos().isEmpty()) {
                                    photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                            + result.getPhotos().get(0).getPhotoReference()
                                            + "&key=" + apiKey;
                                }

                                    // getPlaceId pour id à ajouter
                                    String name = result.getName();
                                    String vicinity = result.getVicinity();
                                    Double rating = result.getRating();
                                    LatLng currentLocation = new LatLng(latitude, longitude);
                                    LatLng restaurantLocation = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                                    double distance = SphericalUtil.computeDistanceBetween(currentLocation, restaurantLocation);

                                    // Count the number of participants for each restaurant.
                                    int nbParticipants = (int) lunches.stream().filter(lunch -> lunch.getRestaurant().getId().equals(result.getPlaceId())).count();
                                    // google map ne fourni pas l'info sur le openingHours
                                    Restaurant origin = new Restaurant("R1", name, vicinity, photoUrl, "10h", "3 étoiles", "www.ollapion.com", "Restaurant_Café_Jeu");

                                    restaurantItems.add(new RestaurantItem(
                                            name, vicinity, rating, photoUrl, distance, nbParticipants, origin));
                                }
                            }
                            // Sort restaurants by distance.
                        Collections.sort(restaurantItems, Comparator.comparingDouble(RestaurantItem::getDistance));
                        restaurantListLiveData.setValue(restaurantItems);
                    }
                });
                // j'ajoute un on Failure ???
            }
        });
    }
}