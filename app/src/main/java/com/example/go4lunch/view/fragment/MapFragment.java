package com.example.go4lunch.view.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.bo.Restaurant;
import com.example.go4lunch.view.CoreActivity;
import com.example.go4lunch.view.DetailRestaurantActivity;
import com.example.go4lunch.view.RestaurantItem;
import com.example.go4lunch.viewmodel.LocationViewModel;
import com.example.go4lunch.viewmodel.ListRestaurantViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles map initialization, user location updates, and restaurant search functionality.
 * Manages the interaction between the Google Map, location services, and restaurant data.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap; // GoogleMap instance for the map
    private LocationViewModel locationViewModel; // ViewModel for handling location data
    private ListRestaurantViewModel restaurantViewModel; // ViewModel for handling restaurant data
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0; // Request code for location permission

    private SupportMapFragment mapFragment; // Fragment for the Google map
    private List<Restaurant> mRestaurants = new ArrayList<>(); // List to store restaurants data

    private Double lastGPSLatitude; // Last known GPS latitude
    private Double lastGPSLongitude; // Last known GPS longitude
    private AutocompleteSupportFragment acsf; // Autocomplete fragment for restaurant search
    private Boolean isSearchActive = false; // Flag to track if search is active
    private static final String TAG = "MapFragment"; // Tag for logging



    // Default constructor
    public MapFragment() {
    }

    /**
     * Method called when the fragment's view is created.
     * It sets up the view models, initializes the map fragment, and the restaurant search support.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize ViewModels
        initializeViewModels();

        // Initialize and set up the map fragment
        initializeMapFragment();

        // Initialize the AutocompleteSupportFragment
        initializeAutocompleteSupportFragment(view);

        // Set up GPS button to update the map with the current GPS position
        setupGPSButton(view);

        return view;
    }

    // Initialize the ViewModels that will handle the data for location and restaurants
    private void initializeViewModels() {
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        restaurantViewModel = new ViewModelProvider(this).get(ListRestaurantViewModel.class);
    }

    // Initialize the map fragment by finding it and setting up the map asynchronously.
    private void initializeMapFragment() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // If the map fragment is found, initialize it and set the map ready callback
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Set this fragment as the callback to handle the map's initialization
        } else {
            Log.e(TAG, "Map fragment not found.");
        }
    }


    // Initialize and configure the AutocompleteSupportFragment for location search.
    private void initializeAutocompleteSupportFragment(View view) {
        acsf = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.acf_map);
        configureACSF(view);
    }

    // Set up the GPS button that updates the map with the current location when clicked.
    private void setupGPSButton(View view) {
        FloatingActionButton btnGPS = view.findViewById(R.id.btnPositionGPS);
        btnGPS.setOnClickListener(v -> {
            // Check if the GPS coordinates are available
            if (lastGPSLatitude != null && lastGPSLongitude != null) {
                // Update the map with the current GPS position
                updateMap(lastGPSLatitude, lastGPSLongitude); // Update map with current position
            } else {
                Log.d(TAG, "GPS position is null, cannot update map.");
            }
        });
    }

    /**
     * Method called after the view is created. It sets up the click listener for the GPS button.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupGPSButton(view);
    }

    /**
     * This method is called when the map is ready to be used.
     * It sets up the map and its various listeners (e.g., marker click, camera position change).
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mGoogleMap == null) {
            Log.e(TAG, "GoogleMap is null.");
            return;
        }

        Log.d(TAG, "onMapReady called");
        Log.d(TAG, "GoogleMap initialized: " + (mGoogleMap != null));
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true); // Enable zoom controls

        requestPermissions(); // Request necessary permissions

        // Set marker click listener
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerName = (String) marker.getTag();
                Log.d(TAG, "onMarkerClick" + markerName);

                // Navigate to the restaurant detail activity when a marker is clicked
                for (int i = 0; i< mRestaurants.size(); i++){
                    if (mRestaurants.get(i).getId().equals(markerName)){
                        DetailRestaurantActivity.navigate(MapFragment.this.getContext(), mRestaurants.get(i));
                        break;
                    }
                }
                return false;
            }
        });

        // Set camera idle listener to fetch restaurants when the map's camera stops moving
        mGoogleMap.setOnCameraIdleListener(() -> {
            if (mGoogleMap != null) {
                Log.d(TAG, "OnCameraIdle");
                LatLng poscamera = mGoogleMap.getCameraPosition().target;
                if (poscamera != null) {
                    Log.d(TAG, "clearGoogleMap_CameraIdle");
                    mGoogleMap.clear();
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(poscamera)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))); // add marker
                    fetchRestaurants(poscamera.latitude, poscamera.longitude); // Fetch restaurants based on camera position
                }
            } else {
                Log.e(TAG, "GoogleMap is not initialized.");
            }
        });
    }

    /**
     * Requests the necessary permissions to access the user's location.
     */
    private void requestPermissions() {
        requestPermissions(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        }, LOCATION_PERMISSION_REQUEST_CODE);
    }

    /**
     * Handles the result of the permission request.
     * If permissions are granted, the method observes the user's location.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            boolean permissionsGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            Log.d(TAG, "Permissions granted: " + permissionsGranted);

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                observeLocation(); // Observe the location if permission is granted
            } else {
                Log.d(TAG, "Permission Denied");
            }
        }
    }

    /**
     * Observes the user's location and updates the map when the location changes.
     */
    @SuppressLint("MissingPermission")
    private void observeLocation() {
        boolean hasLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "Permissions granted: " + hasLocationPermission);

        if (hasLocationPermission) {
            // Observe the location from the ViewModel
            locationViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), location -> {
                if (location != null) {
                    if (location.getLatitude() != null && location.getLongitude() != null) {

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        lastGPSLatitude = latitude;
                        lastGPSLongitude = longitude;

                        updateMap(latitude, longitude); // Update the map with the latest location
                    }
                }
            });
        } else {
            Log.d(TAG, "Location permission is missing, cannot observe location.");
        }
    }

    /**
     * Updates the map with the new user location.
     */
    private void updateMap(double latitude, double longitude) {
        if (mGoogleMap == null) {
            Log.d(TAG, "Map not initialized");
            return;
        }

        LatLng currentPosition = new LatLng(latitude, longitude);

        Log.d(TAG, "Updating map with position: " + currentPosition);

        mGoogleMap.clear(); // Clear existing markers
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15)); // Move the camera to the current position

        if (restaurantViewModel != null) {

            fetchRestaurants(latitude, longitude); // Fetch and display nearby restaurants

        } else {
            Log.d(TAG, "restaurantViewModel is null");
        }
    }

    /**
     * Fetches restaurants near the given location.
     */
    public void fetchRestaurants(double latitude, double longitude) {
        String location = latitude + "," + longitude;
        int radius = 1000; // Radius for restaurant search
        String type = "restaurant"; // Type of places to search for

        // Observe restaurant data from the ViewModel
        restaurantViewModel.getLunchesForToday().observe(getViewLifecycleOwner(), lunches -> {
            if (lunches != null) {
                restaurantViewModel.getAllRestaurants(location, radius, type, BuildConfig.google_maps_api)
                        .observe(getViewLifecycleOwner(), listRestaurants -> {
                            if (listRestaurants != null) {
                                mRestaurants.clear();
                                mRestaurants.addAll(listRestaurants);

                                List<RestaurantItem> restaurantItems = new ArrayList<>();

                                // Process the restaurant data
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

                                    Log.d(TAG, "avant RestaurantItem");
                                    Log.d(TAG, String.valueOf(r.getStars() != null));
                                    if (r.getStars() != null)
                                        Log.d(TAG, String.valueOf(r.getStars()));
                                    restaurantItems.add(new RestaurantItem(
                                            r.getName(),
                                            r.getAddress(),
                                            r.getStars(),
                                            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + r.getPhoto() + "&key=" + BuildConfig.google_maps_api,
                                            distance,
                                            nbParticipants,
                                            r.getLatitude(),
                                            r.getLongitude(),
                                            r
                                    ));
                                    Log.d(TAG, "après RestaurantItem");
                                }

                                // Sort the restaurant items based on the distance or rating
                                Collections.sort(restaurantItems);

                                // Add markers for each restaurant on the map
                                addMarkers(restaurantItems);
                            }
                        });
            }
        });
    }

    /**
     * Adds markers on the map for each restaurant.
     */
    private void addMarkers(List<RestaurantItem> restaurantItems) {

        Log.d(TAG, "addMarker Start");

        // Add a marker for each restaurant on the map
        for (RestaurantItem item : restaurantItems) {
            Log.d(TAG, item.toString());
            if (item.getLatitude() != null && item.getLongitude() != null) {
                LatLng position = new LatLng(item.getLatitude(), item.getLongitude());
                float markerColor = item.getNbParticipant() > 0 ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED;

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(item.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor))); // Set marker color based on participation

                if (marker != null) {
                    marker.setTag(item.getOrigin().getId()); // Set the marker's tag to the restaurant's ID
                }
            }
        }
        Log.d(TAG, "addMarker finish");
    }

    /**
     * Configures the AutocompleteSupportFragment (ACSF) for restaurant search.
     */
    private void configureACSF(View view) {

        // Initialize the Places API
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), BuildConfig.google_maps_api);
        }

        // Configure the AutocompleteSupportFragment
        acsf.setCountries("FR");
        acsf.setHint(getString(R.string.search_hint));
        acsf.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.NAME));

        // Set listener for place selection
        acsf.setOnPlaceSelectedListener(new com.google.android.libraries.places.widget.listener.PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
                LatLng latLng = place.getLatLng();
                if(latLng != null) {
                    fetchRestaurants(latLng.latitude, latLng.longitude); // Fetch restaurants based on the selected place's location
                }
                ACSFVisibility(); // Hide the search bar and show the map
            }

            @Override
            public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                Log.e(TAG, "Error selecting place: " + status.getStatusMessage());
            }
        });
    }

    /**
     * Toggles the visibility between ACSF and the map.
     */
    private void ACSFVisibility() {
        // Toggle the visibility of the AutocompleteSupportFragment and map
        if (isSearchActive) {
            acsf.getView().setVisibility(View.INVISIBLE);
            mapFragment.getView().setVisibility(View.VISIBLE);
            isSearchActive = false;
        } else {
            acsf.getView().setVisibility(View.VISIBLE);
            mapFragment.getView().setVisibility(View.INVISIBLE);
            isSearchActive = true;
        }
    }

    /**
     * Called when the fragment starts.
     * It hides ACSF and shows the map, and sets up the Toolbar click listener.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Hide the search bar and show the map when the fragment starts
        if (acsf != null && acsf.getView() != null) {
            acsf.getView().setVisibility(View.INVISIBLE);
            mapFragment.getView().setVisibility(View.VISIBLE);
            isSearchActive = false;
        }

        // Set up the toolbar click listener to toggle search visibility
        Toolbar mToolbar = ((CoreActivity) getActivity()).findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setOnClickListener(v -> {
                if (acsf != null && acsf.getView() != null) {
                    ACSFVisibility(); // Toggle search visibility when the toolbar is clicked
                }
            });
        }
    }

    /**
     * Called when the fragment resumes.
     * It refreshes the location and the map.
     */
    @Override
    public void onResume() {
        super.onResume();
        locationViewModel.refresh(); // Refresh location data
        mapFragment.onResume(); // Resume map fragment
    }

    /**
     * Called when the fragment is destroyed.
     * It cleans up the map.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy(); // Destroy map fragment
    }

    /**
     * Called when the fragment's instance state needs to be saved.
     * It saves the map state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapFragment.onSaveInstanceState(outState); // Save map fragment state
    }

    /**
     * Called when there is low memory.
     * It handles the map's state.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory(); // Handle low memory scenarios
    }
}
