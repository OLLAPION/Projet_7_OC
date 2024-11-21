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
import com.example.go4lunch.pojo.ResultDetails;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Il est actuellement possible d'accumuler plusieurs Lunch du jour par jour et ça garde en memoire pour le lendemein ...
// quand je retire le Lunch du jour il est toujours comptabilisé sur le visuel alors que sur firestore c'est bien retiré
/**
 * Fragment that displays a list of nearby restaurants based on the user's location.
 */
public class ListRestaurantFragment extends Fragment {

    /** Request code for location permissions */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;

    /** Tag for logging */
    private static final String TAG = "ListRestaurantFragment";

    /** RecyclerView for displaying the list of restaurants */
    private RecyclerView recyclerView;

    /** Adapter for the RecyclerView */
    private RestaurantAdapter adapter;
    // changer les nom des deux viewModels !!!
    /** ViewModel for managing restaurant data */
    private ListRestaurantViewModel viewModel;

    /** ViewModel for managing location data */
    private LocationViewModel locationViewModel;

    /** TextViews for displaying latitude and longitude */
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    AutocompleteSupportFragment acsf;


    public ListRestaurantFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_restaurant, container, false);
        // Initialize the RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.restaurant_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RestaurantAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        // Initialize TextViews for displaying latitude and longitude
        textViewLatitude = view.findViewById(R.id.textViewLatitude);
        textViewLongitude = view.findViewById(R.id.textViewLongitude);

        // Initialize the ViewModels
        viewModel = new ViewModelProvider(this).get(ListRestaurantViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);


        requestPermissions();
        return view;
    }

    /**
     * Request the necessary permissions to access location data.
     */
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
                // If permission is granted, observe location updates
                observeLocation();
            } else {
                // Display a message if permission is denied
                textViewLatitude.setText("Permission Denied");
                textViewLongitude.setText("Permission Denied");
            }
        }
    }


    /**
     * Observe location changes and update the UI accordingly.
     */
    private void observeLocation() {
        locationViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), this::updateLocationUI);
    }



    /**
     * Update the UI with the current location data.
     * @param location the GPSStatus object containing location information
     */
    @SuppressLint("SetTextI18n")
    private void updateLocationUI(GPSStatus location) {
        if (location != null) {
            if (location.getLatitude() != null && location.getLongitude() != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Display latitude and longitude
                textViewLatitude.setText(String.valueOf(latitude));
                textViewLongitude.setText(String.valueOf(longitude));

                // Fetch the list of restaurants based on the current location
                // viewModel.fetchRestaurants(latitude, longitude);
                // modification du repository et du viewModel
                fetchRestaurants(latitude, longitude);

                // Update the adapter data when the restaurant list changes
                viewModel.getRestaurantListLiveData().observe(getViewLifecycleOwner(), restaurantItems -> {
                    adapter.updateData(restaurantItems);
                });

            } else if (location.getQuerying()) {
                // Display a message while querying location
                textViewLatitude.setText("querying");
                textViewLongitude.setText("querying");
            } else {
                // Display a message if location permission is incorrect
                textViewLatitude.setText("Permission incorrect");
                textViewLongitude.setText("Permission incorrect");
            }
        } else {
            Log.e(TAG, "GPSStatus is null");
        }
    }

    public void fetchRestaurants(double latitude, double longitude) {
        String location = latitude + "," + longitude;
        int radius = 1000;
        String type = "restaurant";

        viewModel.getLunchesForToday().observe(getViewLifecycleOwner(), lunches -> {
            if (lunches != null) {
                viewModel.getAllRestaurants(location, radius, type, BuildConfig.google_maps_api)
                        .observe(getViewLifecycleOwner(), listRestaurants -> {
                            if (listRestaurants != null) {

                                List<RestaurantItem> restaurantItems = new ArrayList<>();

                                for (Restaurant r : listRestaurants) {
                                    Double distance = null;

                                    if(r.getLatitude() != null && r.getLongitude() != null) {
                                        LatLng currentLocation = new LatLng(latitude, longitude);
                                        LatLng restaurantLocation = new LatLng(r.getLatitude(), r.getLongitude());
                                        distance = SphericalUtil.computeDistanceBetween(currentLocation, restaurantLocation);

                                    }

                                    Integer nbParticipants = null;
                                    if(r.getId() != null){
                                        nbParticipants = (int) lunches.stream().filter(lunch -> lunch.getRestaurant() != null && lunch.getRestaurant().getId() != null && lunch.getRestaurant().getId().equals(r.getId())).count();
                                    }

                                    Log.d("Test", "avant RestaurantItem");
                                    Log.d("Test", String.valueOf(r.getStars() != null));
                                    if (r.getStars() != null)
                                        Log.d("Test", String.valueOf(r.getStars()));
                                    restaurantItems.add(new RestaurantItem(
                                            r.getName(),
                                            r.getAddress(),
                                            r.getStars(),
                                            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + r.getPhoto() + "&key=" + BuildConfig.google_maps_api,
                                            distance,
                                            nbParticipants,
                                            latitude,
                                            longitude,
                                            r
                                    ));
                                    Log.d("Test", "après RestaurantItem");


                                }
                                // Sort restaurants by distance.
                                Collections.sort(restaurantItems);
                                // Update the UI using the adapter
                                adapter.updateData(restaurantItems);
                            }


                        });

            }


        });


    }


    @Override
    public void onResume() {
        super.onResume();
        if (locationViewModel != null) {
            // Refresh location data when the fragment resumes
            locationViewModel.refresh();
        }
    }
}
