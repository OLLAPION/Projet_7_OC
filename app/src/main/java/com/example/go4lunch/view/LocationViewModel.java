package com.example.go4lunch.view;

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
import com.example.go4lunch.model.location.GPSStatus;
import com.google.android.gms.location.LocationServices;
import com.example.go4lunch.repository.LocationRepository;

public class LocationViewModel extends AndroidViewModel {

    private final LocationRepository locationRepository;
    // ecouter sur plusieurs liveDAta
    private final MediatorLiveData<GPSStatus> locationLiveData = new MediatorLiveData<>();;

    private final MutableLiveData<Boolean> hasPermissionLiveData = new MutableLiveData<>();

    public LocationViewModel(@NonNull Application application) {
        super(application);
        // voir les factorys viewmodel
        locationRepository = new LocationRepository(LocationServices.getFusedLocationProviderClient(application));
        locationLiveData.addSource(locationRepository.getLocationLiveData(),location -> setStatus(location, hasPermissionLiveData.getValue()));
        locationLiveData.addSource(hasPermissionLiveData, hasGPSPermission -> setStatus(locationRepository.getLocationLiveData().getValue(), hasGPSPermission));
    }

    private void setStatus(Location location, Boolean hasGPSPermission) {
        if (location == null){
            if (hasGPSPermission == null || !hasGPSPermission){
                locationLiveData.setValue(new GPSStatus(false, false));
            } else {
                locationLiveData.setValue(new GPSStatus(true, true));
            }
        } else{
            locationLiveData.setValue(new GPSStatus(location.getLongitude(), location.getLatitude()));
        }
    }

    public LiveData<GPSStatus> getLocationLiveData() {
        return locationLiveData;
    }

    public void refresh(){
        boolean hasGpsPermission = ContextCompat.checkSelfPermission(
                MainApplication.getApplication(), ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED;
        hasPermissionLiveData.setValue(hasGpsPermission);
        if (hasGpsPermission){
            locationRepository.startLocationRequest();
        }else{
            locationRepository.stopLocationRequest();
        }
    }


    /*
    public void startLocationRequest() {
        Application application = getApplication();
        if (ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationRepository.startLocationRequest();
    }


    public void stopLocationRequest() {
        locationRepository.stopLocationRequest();
    }

     */
}
