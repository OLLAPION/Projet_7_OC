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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.model.location.GPSStatus;
import com.example.go4lunch.ui.CoreActivity;
import com.example.go4lunch.ui.MainActivity;
import com.example.go4lunch.ui.RestaurantItem;
import com.example.go4lunch.pojo.RestaurantsAnswer;
import com.example.go4lunch.pojo.Result;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.services.RetrofitMapsApi;
import com.example.go4lunch.services.RetrofitService;
import com.example.go4lunch.view.LocationViewModel;
import com.example.go4lunch.view.RestaurantAdapter;

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

    private LocationViewModel locationViewModel;
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

        // manque un appel au viewmodel pour recuperer la position GPS > observe > fetchrestaurant sur gps workmate
        // a retirer
        //fetchRestaurants(48.8566, 2.3522); // position de Paris pour l'exemple

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

    private void fetchRestaurants(double latitude, double longitude) {
        String location = latitude + "," + longitude;
        // les constantes sont à mettre en tant que static final en haut de la classe
        int radius = 1000;
        String type = "restaurant";
        String apiKey = BuildConfig.google_maps_api;

        //utiliser le viewmodel et mettre cette logique dans le viewmodel > renvoi un Livedata

        // ne pas utiliser le result > RestaurantItem
        // CAD ne pas utiliser les POJO ? Si oui, comment je ne vois pas
        restaurantRepository.getAllRestaurants(location, radius, type, apiKey).enqueue(new Callback<RestaurantsAnswer>() {
            @Override
            public void onResponse(Call<RestaurantsAnswer> call, Response<RestaurantsAnswer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Result> results = response.body().getResults();
                    List<RestaurantItem> restaurantItems = new ArrayList<>();
                    for (Result result : results) {
                        // si le resultat est null aller chercher les valeurs
                        if (result != null) {
                            String name = result.getName();
                            String vicinity = result.getVicinity();
                            Double rating = result.getRating();
                            // Si les valeurs sont null les recuperer
                            if (name != null && vicinity != null && rating != null) {
                                String photoUrl = (result.getPhotos() != null && !result.getPhotos().isEmpty())
                                        ? "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + result.getPhotos().get(0).getPhotoReference() + "&key=" + apiKey
                                        : null;
                                restaurantItems.add(new RestaurantItem(
                                        name,
                                        vicinity,
                                        rating,
                                        photoUrl
                                ));
                            }
                        }
                    }
                    adapter.updateData(restaurantItems);
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