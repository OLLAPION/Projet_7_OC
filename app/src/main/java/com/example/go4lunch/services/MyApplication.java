package com.example.go4lunch.services;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("MyApplication", "onCreate: Initializing FirebaseApp");
        FirebaseApp.initializeApp(this);
    }
}

