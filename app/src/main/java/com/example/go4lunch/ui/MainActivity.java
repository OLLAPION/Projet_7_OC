package com.example.go4lunch.ui;

import static com.example.go4lunch.BuildConfig.google_maps_api;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.go4lunch.R;
import com.example.go4lunch.pojo.RestaurantsAnswer;
import com.example.go4lunch.pojo.Result;
import com.example.go4lunch.services.RestaurantRepository;
import com.example.go4lunch.services.RetrofitMapsApi;
import com.example.go4lunch.services.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // mieux vaut déclarer cette variable ici ou dans le onCreate ?
    // SDK 34 ???
    private Button btnGetRestaurants;
    private RestaurantRepository restaurantRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetRestaurants = findViewById(R.id.btnGetRestaurants);
        restaurantRepository = new RestaurantRepository(RetrofitService.getRestaurantApi());

        btnGetRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // j'appelle ma fonction qui a ma petite requete
                getRestaurants();
            }
        });
    }

    private void getRestaurants() {
        // Les paramètres de la requête
        String location = "37.4219999,-122.0840575";
        int radius = 1000;
        String type = "restaurant";
        String key = google_maps_api;

        // J'appelle le repository pour recécupèrer les restaurants
        Call<RestaurantsAnswer> call = restaurantRepository.getAllRestaurants(location, radius, type, key);

        // Requête asynchrone
        call.enqueue(new Callback<RestaurantsAnswer>() {
            @Override
            public void onResponse(@NonNull Call<RestaurantsAnswer> call, @NonNull Response<RestaurantsAnswer> response) {
                if (response.isSuccessful()) {
                    RestaurantsAnswer result = response.body();
                    assert result != null;
                    for (Result r : result.getResults())
                    {
                        Log.e("MainActivity", r.getName());
                    }
                    // Si je veux faire quelque chose avec la liste des restaurants (result.getResults())
                } else {
                    Log.e("MainActivity", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RestaurantsAnswer> call, @NonNull Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
            }
        });
    }
}

