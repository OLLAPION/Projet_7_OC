package com.example.go4lunch.model.bo.location;

/**
 * Model class representing the status of GPS including longitude, latitude, permission status, and querying status.
 */
public class GPSStatus {
    private final Double longitude; // Longitude coordinate
    private final Double latitude; // Latitude coordinate
    private final Boolean hasGPSPermission; // Indicates whether the app has GPS permission
    private final Boolean querying; // Indicates whether GPS is currently querying

    /**
     * Constructor for initializing GPS status with longitude and latitude coordinates.
     * @param longitude The longitude coordinate.
     * @param latitude The latitude coordinate.
     */
    public GPSStatus(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.hasGPSPermission = true; // Assume GPS permission is granted
        this.querying = false; // Set querying status to false as GPS is not currently querying
    }

    /**
     * Constructor for initializing GPS status with permission and querying status.
     * @param hasGPSPermission Indicates whether the app has GPS permission.
     * @param querying Indicates whether GPS is currently querying.
     */
    public GPSStatus(Boolean hasGPSPermission, Boolean querying) {
        this.longitude = null; // No coordinates available
        this.latitude = null; // No coordinates available
        this.hasGPSPermission = hasGPSPermission;
        this.querying = querying;
    }

    /**
     * Get the longitude coordinate.
     * @return The longitude coordinate.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Get the latitude coordinate.
     * @return The latitude coordinate.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Check if the app has GPS permission.
     * @return True if the app has GPS permission, false otherwise.
     */
    public Boolean getHasGPSPermission() {
        return hasGPSPermission;
    }

    /**
     * Check if GPS is currently querying.
     * @return True if GPS is currently querying, false otherwise.
     */
    public Boolean getQuerying() {
        return querying;
    }

    /**
     * Get the string representation of the GPSStatus object.
     * @return A string representation of the GPSStatus object.
     */
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
