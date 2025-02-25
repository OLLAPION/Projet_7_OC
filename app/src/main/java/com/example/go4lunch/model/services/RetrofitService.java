package com.example.go4lunch.model.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitService is a utility class that provides a configured Retrofit instance
 * for making API requests to the Google Places API.
 */
public class RetrofitService {

    // Gson instance configured to be lenient with JSON parsing
    private static final Gson gson = new GsonBuilder().setLenient().create();

    // OkHttpClient instance for handling HTTP requests
    private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();

    // Base URL for Google Places API
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    // Singleton Retrofit instance configured with base URL, HTTP client, and JSON converter
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    /**
     * Provides an implementation of the RetrofitMapsApi interface.
     *
     * @return An instance of RetrofitMapsApi for making restaurant-related API requests.
     */
    public static RetrofitMapsApi getRestaurantApi() {
        return retrofit.create(RetrofitMapsApi.class);
    }
}