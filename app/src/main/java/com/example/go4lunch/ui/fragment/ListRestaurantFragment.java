package com.example.go4lunch.ui.fragment;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
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
import com.example.go4lunch.ui.CoreActivity;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

    /** ViewModel for managing restaurant data */
    private ListRestaurantViewModel viewModel;

    /** ViewModel for managing location data */
    private LocationViewModel locationViewModel;

    /** TextView displayed while waiting for location permission or data */
    private TextView textToWait;

    /** AutocompleteSupportFragment for place search */
    AutocompleteSupportFragment acsf;

    /** PlacesClient for interacting with Google Places API */
    PlacesClient placeClient;

    /** Boolean to track if search is active */
    Boolean isSearchActive = false;

    /**
     * Default constructor for the fragment.
     */
    public ListRestaurantFragment() {
    }

    /**
     * Inflates the layout and initializes views, adapters, and ViewModels.
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment's UI
     * @param savedInstanceState Bundle with saved state data
     * @return The inflated view for the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_restaurant, container, false);
        // Initialize the RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.restaurant_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RestaurantAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        textToWait = view.findViewById(R.id.texttowait);
        acsf= (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.acf_list);

        // Initialize the ViewModels
        viewModel = new ViewModelProvider(this).get(ListRestaurantViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        // Configure the AutocompleteSupportFragment and request permissions
        configureACSF();
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
                textToWait.setVisibility(View.VISIBLE);
                textToWait.setText(getString(R.string.permission_denied));

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
     * If location is available, it fetches nearby restaurants.
     * If querying is active, displays a loading spinner.
     * If permission is incorrect or data is unavailable, shows an error message.
     * @param location the GPSStatus object containing location information
     */
    @SuppressLint("SetTextI18n")
    private void updateLocationUI(GPSStatus location) {
        ProgressBar loadingSpinner = getView().findViewById(R.id.loading_spinner);

        if (location != null) {
            if (location.getLatitude() != null && location.getLongitude() != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Hide loading spinner when location is obtained
                loadingSpinner.setVisibility(View.GONE);

                // Fetch nearby restaurants using the current location
                fetchRestaurants(latitude, longitude);

                // Observe changes in the restaurant list and update RecyclerView
                viewModel.getRestaurantListLiveData().observe(getViewLifecycleOwner(), restaurantItems -> {
                    adapter.updateData(restaurantItems);
                });

            } else if (location.getQuerying()) {
                // Location is being queried, show loading spinner
                loadingSpinner.setVisibility(View.VISIBLE);
                textToWait.setVisibility(View.GONE);
            } else {
                // Error with location data, show error message
                loadingSpinner.setVisibility(View.GONE);
                textToWait.setVisibility(View.VISIBLE);
                textToWait.setText(getString(R.string.permission_denied));
            }
        } else {
            Log.e(TAG, "GPSStatus is null");
        }
    }

    /**
     * Fetches nearby restaurants based on the given latitude and longitude.
     * Updates the RecyclerView adapter with the retrieved data.
     * @param latitude The latitude of the current location
     * @param longitude The longitude of the current location
     */
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

                                    // Calculate distance between user's location and the restaurant
                                    if(r.getLatitude() != null && r.getLongitude() != null) {
                                        LatLng currentLocation = new LatLng(latitude, longitude);
                                        LatLng restaurantLocation = new LatLng(r.getLatitude(), r.getLongitude());
                                        distance = SphericalUtil.computeDistanceBetween(currentLocation, restaurantLocation);

                                    }

                                    Integer nbParticipants = null;
                                    if(r.getId() != null){
                                        // Count the number of participants for each restaurant
                                        nbParticipants = (int) lunches.stream().filter(lunch -> lunch.getRestaurant() != null && lunch.getRestaurant().getId() != null && lunch.getRestaurant().getId().equals(r.getId())).count();
                                    }

                                    Log.d(TAG, "avant RestaurantItem");
                                    Log.d(TAG, String.valueOf(r.getStars() != null));
                                    if (r.getStars() != null)
                                        Log.d(TAG, String.valueOf(r.getStars()));
                                    // Create a RestaurantItem for each restaurant and add it to the list
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
                                // Sort the list of restaurants by distance
                                Collections.sort(restaurantItems);
                                adapter.updateData(restaurantItems);
                            }
                        });
            }
        });
    }

    /**
     * Configures the AutocompleteSupportFragment (ACSF) for place searching.
     * Sets country filter, hint, and place fields.
     * Handles the selection and error events for the place search.
     */
    public void configureACSF() {

        if (!Places.isInitialized()) {
            // Initialize Places API if not already initialized
            Places.initialize(getContext(), BuildConfig.google_maps_api);
        }

        placeClient = Places.createClient(getContext());

        // Set up AutocompleteSupportFragment settings
        acsf.setCountries("FR");
        acsf.setHint(getString(R.string.search_hint));
        acsf.setPlaceFields(Arrays.asList(Place.Field.NAME));

        // Handle place selection and update the map and list accordingly
        acsf.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                LatLng latLng = place.getLatLng();
                recyclerView.setVisibility(View.VISIBLE);
                acsf.getView().setVisibility(View.INVISIBLE);
                isSearchActive = false;
                String name = place.getName();
                String address = place.getAddress();
                Log.i(TAG, "onPlaceSelect success : " + name + " " + latLng + " " + address);

                // Get LatLng from address if not available directly from place
                if(latLng == null){
                    if(address == null) {
                        Log.i(TAG,"adresse introuvable pour la selection de l'utilisateur" );
                        return;
                    }
                    latLng = getLocationFromAddress(address);
                }

                if (latLng==null){
                    Log.i(TAG,"position introuvable à partir de l'adresse pour la selection de l'utilisateur" );
                    return;
                }
                // Fetch restaurants for the selected place's location
                fetchRestaurants(latLng.latitude, latLng.longitude);

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "Error selecting place: " + status.getStatusMessage());
            }
        });
    }

    /**
     * Converts an address string into a LatLng object using Geocoder.
     * @param strAddress The address to be converted
     * @return The corresponding LatLng object, or null if not found
     */
    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        LatLng latLng = null;

        try {
            addresses = geocoder.getFromLocationName(strAddress, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (Exception e) {
            Log.e(TAG, "Geocoder failed: " + e.getMessage());
        }

        return latLng;
    }

    /**
     * Called when the fragment becomes visible to the user.
     * Initializes the visibility of the AutocompleteSupportFragment and RecyclerView.
     * Sets up click listeners on the toolbar to toggle the search bar.
     */
    @Override
    public void onStart() {
        super.onStart();

        if (acsf != null && acsf.getView() != null) {
            acsf.getView().setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            isSearchActive = false;
        }

        Toolbar mToolbar = ((CoreActivity) getActivity()).findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setOnClickListener(v -> {
                if (acsf != null && acsf.getView() != null) {
                    if (isSearchActive) {
                        acsf.getView().setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        isSearchActive = false;
                    } else {
                        acsf.getView().setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        isSearchActive = true;
                    }


                }
            });
        }
    }

    /**
     * Refreshes location data when the fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (locationViewModel != null) {
            locationViewModel.refresh();
        }
    }

    /**
     * Called when the system is running low on memory.
     * Releases resources by setting the RecyclerView's adapter to null.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        recyclerView.setAdapter(null);
    }

}
