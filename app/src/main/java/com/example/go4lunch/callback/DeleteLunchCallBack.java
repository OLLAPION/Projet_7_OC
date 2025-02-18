package com.example.go4lunch.callback;

/**
 * Callback interface for lunch deletion events.
 * Used to notify when a lunch has been successfully deleted.
 */
public interface DeleteLunchCallBack {
    /**
     * Called when the lunch has been successfully deleted.
     * Implement this method to define the behavior after deletion.
     */
    void onDeleted();
}
