package com.example.go4lunch.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.location.GPSStatus;
import com.example.go4lunch.ui.DetailRestaurantActivity;
import com.example.go4lunch.ui.RestaurantItem;
import com.example.go4lunch.view.LocationViewModel;
import com.example.go4lunch.view.ListRestaurantViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// GPS status fixe ? même restaurant peux importe où je suis
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private LocationViewModel locationViewModel;
    private ListRestaurantViewModel restaurantViewModel;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;

    private SupportMapFragment mapFragment;

    private List<Restaurant> mRestaurants = new ArrayList<>();

    private Double lastGPSLatitude ;
    private Double lastGPSLongitude ;



    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Mettre dans une méthode
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        restaurantViewModel = new ViewModelProvider(this).get(ListRestaurantViewModel.class);

        // configur map
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "Map fragment not found.");
        }



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // configure bouton gps
        FloatingActionButton btnGPS = (FloatingActionButton) view.findViewById(R.id.btnPositionGPS);

        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if =! null
               updateMap(lastGPSLatitude, lastGPSLongitude);

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mGoogleMap == null) {
            Log.e(TAG, "GoogleMap is null.");
            return;
        }

        Log.d(TAG, "onMapReady called");
        Log.d(TAG, "GoogleMap initialized: " + (mGoogleMap != null));
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        // Demande la permission + laisser l'observation sur les position GPS
        requestPermissions();

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerName = (String) marker.getTag();
                Log.d(TAG, "onMarkerClick" + markerName);

                for (int i = 0; i< mRestaurants.size(); i++){
                    if (mRestaurants.get(i).getId().equals(markerName)){
                        DetailRestaurantActivity.navigate(MapFragment.this.getContext(), mRestaurants.get(i));
                        break;
                    }
                }
                return false;

            }
        });

        // lors du mouvement de la map
        /*
        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mGoogleMap.clear();
                Log.d("MapFragment", "Camera has moved");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        LatLng poscamera = mGoogleMap.getCameraPosition().target;
                        mGoogleMap.addMarker(new MarkerOptions()
                                        .position(poscamera))
                                .setIcon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    }
                }, 50);
            }
        });

         */

        mGoogleMap.setOnCameraIdleListener(() -> {
            if (mGoogleMap != null) {
                Log.d(TAG, "OnCameraIdle");
                LatLng poscamera = mGoogleMap.getCameraPosition().target;
                if (poscamera != null) {
                    Log.d(TAG, "clearGoogleMap_CameraIdle");
                    mGoogleMap.clear();
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(poscamera)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    fetchRestaurants(poscamera.latitude, poscamera.longitude);
                }
            } else {
                Log.e(TAG, "GoogleMap is not initialized.");
            }
        });
    }

    // Demande la permission + laisser l'observation sur les position GPS
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

            boolean permissionsGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            Log.d(TAG, "Permissions granted: " + permissionsGranted);

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                observeLocation();
            } else {
                Log.d(TAG, "Permission Denied");
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void observeLocation() {
        boolean hasLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "Permissions granted: " + hasLocationPermission);

        if (hasLocationPermission) {
            locationViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), location -> {
                if (location != null) {
                    if (location.getLatitude() != null && location.getLongitude() != null) {


                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // enregistrer la dernière position gps en variable d'inst
                        // pour recentrer la carte sur cette position à la demande de l'utilisa
                        lastGPSLatitude = latitude;
                        lastGPSLongitude = longitude;

                        updateMap(latitude, longitude);
                    }
                }
            });
        } else {
            Log.d(TAG, "Location permission is missing, cannot observe location.");
        }
    }


 // n'utilise plus GPSStatus, mais directement double latitude et longitude
    // mais reçoit des coordonnée null > donc erreur
    // /!\ updateMap se gère comme updateLocationUI du ListRestaurantFragment ?
    private void updateMap(double latitude, double longitude) {
        if (mGoogleMap == null) {
            Log.d(TAG, "Map not initialized");
            return;
        }

        LatLng currentPosition = new LatLng(latitude, longitude);

        Log.d(TAG, "Updating map with position: " + currentPosition);

        Log.d(TAG, "clearGoogleMap_updateMap");
        mGoogleMap.clear();
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));

        if (restaurantViewModel != null) {

            fetchRestaurants(latitude, longitude);

        } else {
            Log.d(TAG, "restaurantViewModel is null");
        }
    }

    public void fetchRestaurants(double latitude, double longitude) {
        String location = latitude + "," + longitude;
        int radius = 1000;
        String type = "restaurant";

        restaurantViewModel.getLunchesForToday().observe(getViewLifecycleOwner(), lunches -> {
            if (lunches != null) {
                restaurantViewModel.getAllRestaurants(location, radius, type, BuildConfig.google_maps_api)
                        .observe(getViewLifecycleOwner(), listRestaurants -> {
                            if (listRestaurants != null) {

                                // bien ou pas de retirer le clear de la liste de restaurant ????
                                mRestaurants.clear();
                                mRestaurants.addAll(listRestaurants);

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

                                Collections.sort(restaurantItems);

                                addMarkers(restaurantItems);
                            }
                        });
            }
        });
    }

    private static final String TAG = "MapFragment";
    private void addMarkers(List<RestaurantItem> restaurantItems) {

        Log.d(TAG, "addMarker Start");

        for (RestaurantItem item : restaurantItems) {

            Log.d(TAG, item.toString());

            if (item.getLatitude() != null && item.getLongitude() != null) {
                LatLng position = new LatLng(item.getLatitude(), item.getLongitude());
                float markerColor = item.getNbParticipant() > 0 ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED;

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(item.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

                if (marker != null) {
                    marker.setTag(item.getOrigin().getId());
                }
            }
        }
        Log.d(TAG, "addMarker finish");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "refresh locationRepository");
        locationViewModel.refresh();
        Log.d(TAG, "refresh SupportMapFragment");
        mapFragment.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapFragment.onSaveInstanceState(outState);
    }

    // greencode à regarder
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }
}
