package com.example.go4lunch.services;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

/**
 * Custom Application class for initializing global configurations.
 * This class is executed before any other component (Activity, Service, etc.)
 * and is used to set up Firebase at the application start.
 */
public class MyApplication extends Application {

    // TAG for logs
    private String TAG = "MA";

    /**
     * Called when the application is starting, before any other application objects have been created.
     * This is where FirebaseApp is initialized to ensure Firebase services are available globally.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: Initializing FirebaseApp");
        // Initialize Firebase for the application
        FirebaseApp.initializeApp(this);
    }
}

