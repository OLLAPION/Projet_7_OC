package com.example.go4lunch.ui.fragment;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.location.GPSStatus;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.ui.RestaurantItem;
import com.example.go4lunch.pojo.RestaurantsAnswer;
import com.example.go4lunch.pojo.Result;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.services.RetrofitMapsApi;
import com.example.go4lunch.services.RetrofitService;
import com.example.go4lunch.view.ListRestaurantViewModel;
import com.example.go4lunch.view.LocationViewModel;
import com.example.go4lunch.view.adapter.RestaurantAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListRestaurantFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;
    private static final String TAG = "ListRestaurantFragment";

    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private RestaurantRepository restaurantRepository;

    // changer les noms des deux ViewModel car utiliser pour plusieurs class
    private LocationViewModel locationViewModel;
    private ListRestaurantViewModel viewModel;
    private TextView textViewLatitude;
    private TextView textViewLongitude;

    public ListRestaurantFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_restaurant, container, false);
        recyclerView = view.findViewById(R.id.restaurant_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RestaurantAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        textViewLatitude = view.findViewById(R.id.textViewLatitude);
        textViewLongitude = view.findViewById(R.id.textViewLongitude);

        // viewmodel le mettre
        RetrofitMapsApi retrofitMapsApi = RetrofitService.getRestaurantApi();
        // faire comme DetailRestaurantActivity
        restaurantRepository = new RestaurantRepository(retrofitMapsApi);
        locationViewModel = new LocationViewModel(MainApplication.getApplication());

        /*
        viewModel = new ViewModelProvider(this).get(ListRestaurantViewModel.class);
        viewModel.getRestaurantListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                adapter.updateData(restaurants);
            }
        });

         */

        requestPermissions();
        return view;
    }


    private void requestPermissions() {
        requestPermissions(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        }, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                observeLocation();
            } else {
                textViewLatitude.setText("Permission Denied");
                textViewLongitude.setText("Permission Denied");
            }
        }
    }

    private void observeLocation() {
        locationViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), new Observer<GPSStatus>() {
            @Override
            public void onChanged(GPSStatus location) {
                updateLocationUI(location);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateLocationUI(GPSStatus location) {
        if (location != null) {
            if (location.getLatitude() != null && location.getLongitude() != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
                textViewLatitude.setText(String.valueOf(latitude));
                textViewLongitude.setText(String.valueOf(longitude));

                fetchRestaurants(latitude, longitude);

            } else if (location.getQuerying()) {
                textViewLatitude.setText("querying");
                textViewLongitude.setText("querying");
            } else {
                textViewLatitude.setText("Permission incorrect");
                textViewLongitude.setText("Permission incorrect");
            }
        } else {
            Log.e(TAG, "GPSStatus is null");
        }
    }


    /*
    private void fetchRestaurants(double latitude, double longitude) {
        viewModel.fetchRestaurants(latitude, longitude);
    }

     */


    private void fetchRestaurants(double latitude, double longitude) {
        String location = latitude + "," + longitude;
        int radius = 1000;
        String type = "restaurant";
        String apiKey = BuildConfig.google_maps_api;

        restaurantRepository.getAllRestaurants(location, radius, type, apiKey).enqueue(new Callback<RestaurantsAnswer>() {
            @Override
            public void onResponse(Call<RestaurantsAnswer> call, Response<RestaurantsAnswer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Result> results = response.body().getResults();
                    List<RestaurantItem> restaurantItems = new ArrayList<>();

                    Log.d(TAG, "Number of restaurants fetched: " + results.size());

                    for (Result result : results) {
                        if (result != null) {
                            String name = result.getName();
                            String vicinity = result.getVicinity();
                            Double rating = result.getRating();

                            // Construction de l'URL de la photo si disponible
                            String photoUrl = null;
                            if (result.getPhotos() != null && !result.getPhotos().isEmpty()) {
                                photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                        + result.getPhotos().get(0).getPhotoReference()
                                        + "&key=" + apiKey;
                            }

                            // Calcul de la distance avec SphericalUtil
                            LatLng currentLocation = new LatLng(latitude, longitude);
                            LatLng restaurantLocation = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                            double distance = SphericalUtil.computeDistanceBetween(currentLocation, restaurantLocation);

                            Log.d(TAG, "Restaurant: " + name + ", Distance: " + distance);

                            // Les workmates pour ce restaurant
                            LiveData<ArrayList<User>> workmatesLiveData = LunchRepository.getInstance(getContext()).getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(new Restaurant(result.getPlaceId(), name, vicinity, photoUrl, "10h", "3 étoiles", "www.ollapion.com", "Restaurant_Café_Jeu"));

                            final String finalPhotoUrl = photoUrl;
                            workmatesLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
                                @Override
                                public void onChanged(ArrayList<User> workmates) {
                                    int nbParticipants = workmates.size();
                                    Restaurant origin = new Restaurant("R1", "Chez Ollapion", "1 rue de la jeunesse", "http://www.ollapion.com/kross.jpg)", "10h", "3 étoiles", "www.ollapion.com", "Restaurant_Café_Jeu");

                                    Log.d(TAG, "Adding restaurant item: " + name + " with distance: " + distance);

                                    restaurantItems.add(new RestaurantItem(
                                            name, vicinity, rating, finalPhotoUrl, distance, nbParticipants, origin));

                                    adapter.updateData(restaurantItems);
                                }
                            });
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to fetch restaurants: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RestaurantsAnswer> call, Throwable t) {
                Log.e(TAG, "Error fetching restaurants", t);
            }
        });
    }


    @Override
    public void onResume(){
        super.onResume();
        if (locationViewModel != null){
            locationViewModel.refresh();
        }
    }
}