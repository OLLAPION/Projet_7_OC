package com.example.go4lunch.model.location;

public class GPSStatus {
    private final Double longitude;
    private final Double latitude;
    private final Boolean hasGPSPermission;
    private final Boolean querying;

    public GPSStatus(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.hasGPSPermission = true;
        this.querying = false;
    }

    public GPSStatus(Boolean hasGPSPermission, Boolean querying) {
        this.longitude = null;
        this.latitude = null;
        this.hasGPSPermission = hasGPSPermission;
        this.querying = querying;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Boolean getHasGPSPermission() {
        return hasGPSPermission;
    }

    public Boolean getQuerying() {
        return querying;
    }

    @Override
    public String toString() {
        return "GPSStatus{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", hasGPSPermission=" + hasGPSPermission +
                ", querying=" + querying +
                '}';
    }
}
