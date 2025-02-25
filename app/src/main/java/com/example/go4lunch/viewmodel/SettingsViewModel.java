package com.example.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.repository.WorkmateRepository;

/**
 * ViewModel for managing the SettingsActivity.
 */
public class SettingsViewModel extends ViewModel {

    // The repository : WorkmateRepository
    private final WorkmateRepository workmateRepository;

    /**
     * Constructor of the SettingsViewModel.
     */
    public SettingsViewModel() {
        workmateRepository = WorkmateRepository.getInstance();
    }

    /**
     * Gets the LiveData object that holds the notification status (active or inactive).
     *
     * @return LiveData<Boolean> indicating whether notifications are active.
     */
    public LiveData<Boolean> getIsNotificationActive() {
        return workmateRepository.getIsNotificationActive();
    }

    /**
     * Updates the notification status by saving the new value in the repository.
     *
     * @param isActive A boolean indicating whether notifications should be active.
     */
    public void setNotificationActive(boolean isActive) {
        workmateRepository.createOrUpdateWorkmate(isActive);
    }
}
