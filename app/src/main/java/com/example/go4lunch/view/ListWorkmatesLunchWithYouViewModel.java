package com.example.go4lunch.view;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.WorkmateRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for managing the ListWorkmatesLunchWithYouFragment.
 */
public class ListWorkmatesLunchWithYouViewModel extends ViewModel {

    // The repository : WorkmateRepository
    private final WorkmateRepository workmateRepository;

    // The repository : LunchRepository
    private final LunchRepository lunchRepository;

    /**
     * Constructor initializing the repositories
     */
    public ListWorkmatesLunchWithYouViewModel() {
        this.workmateRepository = WorkmateRepository.getInstance();
        this.lunchRepository = LunchRepository.getInstance(MainApplication.getApplication());
    }

    /**
     * Get a list of all workmates from the repository.
     * @return LiveData containing the list of User objects (workmates).
     */
    public LiveData<List<User>> getAllWorkmates() {
        return workmateRepository.getAllWorkmates();
    }

    /**
     * Get a list of lunches planned for today.
     * @return LiveData containing the list of Lunch objects for today.
     */
    public LiveData<List<Lunch>> getTodayLunches() {
        return lunchRepository.getLunchesForToday();
    }
}