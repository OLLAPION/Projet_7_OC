package com.example.go4lunch.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.repository.WorkmateRepository;

public class SettingsViewModel extends ViewModel {

    private final WorkmateRepository repository;

    public SettingsViewModel() {
        repository = WorkmateRepository.getInstance();
    }

    public LiveData<Boolean> getIsNotificationActive() {
        return repository.getIsNotificationActive();
    }

    public void setNotificationActive(boolean isActive) {
        repository.createOrUpdateWorkmate(isActive);
    }
}
