package com.example.go4lunch.callback;

/**
 * Callback interface for lunch creation events.
 * Used to notify when a lunch has been successfully created.
 */
public interface CreateLunchCallBack {
    /**
     * Called when the lunch has been successfully created.
     * Implement this method to define the behavior after creation.
     */
    void onCreated ();
}
