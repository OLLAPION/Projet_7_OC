package com.example.go4lunch;

import android.app.Application;

/**
 * MainApplication class to provide a global context.
 */
public class MainApplication extends Application {

    // Holds the application context
    private static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the application context
        sApplication = this;
    }

    /**
     * Returns the global application context.
     */
    public static Application getApplication() {
        return sApplication;
    }
}
