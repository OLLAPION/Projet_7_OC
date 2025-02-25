package com.example.go4lunch.viewmodel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.model.bo.location.GPSStatus;
import com.google.android.gms.location.LocationServices;
import com.example.go4lunch.model.repository.LocationRepository;

/**
 * ViewModel for managing user location and GPS permissions.
 */
public class LocationViewModel extends AndroidViewModel {

    // The repository : LocationRepository
    private final LocationRepository locationRepository;

    // MediatorLiveData to combine location and GPS permission status.
    private final MediatorLiveData<GPSStatus> locationLiveData = new MediatorLiveData<>();;

    // LiveData to track GPS permission status.
    private final MutableLiveData<Boolean> hasPermissionLiveData = new MutableLiveData<>();

    /**
     * Constructor of the class. Initializes the location repository and LiveData sources.
     *
     * @param application The application to access location services.
     */
    public LocationViewModel(@NonNull Application application) {
        super(application);
        // Initialize the repository to get the location data
        locationRepository = new LocationRepository(LocationServices.getFusedLocationProviderClient(application));
        // Add source for location data (updated when location changes)
        locationLiveData.addSource(locationRepository.getLocationLiveData(),location -> setStatus(location, hasPermissionLiveData.getValue()));
        // Add source for GPS permission data (updated when the permission status changes)
        locationLiveData.addSource(hasPermissionLiveData, hasGPSPermission -> setStatus(locationRepository.getLocationLiveData().getValue(), hasGPSPermission));
    }

    /**
     * Updates the combined GPS status (location and permission).
     *
     * @param location The current GPS location of the user (may be null if location is not available).
     * @param hasGPSPermission Indicates if the user has granted permission to access the location.
     */
    private void setStatus(Location location, Boolean hasGPSPermission) {
        if (location == null){
            // If location is unavailable and permissions are granted, send an active GPS status
            if (hasGPSPermission == null || !hasGPSPermission){
                locationLiveData.setValue(new GPSStatus(false, false));
            } else {
                locationLiveData.setValue(new GPSStatus(true, true));
            }
        } else{
            // If location is available, send the longitude and latitude
            locationLiveData.setValue(new GPSStatus(location.getLongitude(), location.getLatitude()));
        }
    }

    /**
     * Returns the LiveData that provides the current GPS status.
     *
     * @return LiveData containing the GPS status (location and permission).
     */
    public LiveData<GPSStatus> getLocationLiveData() {
        return locationLiveData;
    }

    /**
     * Refreshes the location and GPS permission status.
     * Updates `hasPermissionLiveData` and starts or stops the location request.
     */
    public void refresh(){
        // Check if the app has permission to access the location
        boolean hasGpsPermission = ContextCompat.checkSelfPermission(
                MainApplication.getApplication(), ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED;

        // Update the permission status in LiveData
        hasPermissionLiveData.setValue(hasGpsPermission);

        // If permission is granted, start the location request
        if (hasGpsPermission){
            locationRepository.startLocationRequest();
        }else{
            locationRepository.stopLocationRequest();
        }
    }
}
