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

public class ListWorkmatesLunchWithYouViewModel extends ViewModel {

    private final WorkmateRepository workmateRepository;
    private final LunchRepository lunchRepository;


    public ListWorkmatesLunchWithYouViewModel() {
        this.workmateRepository = WorkmateRepository.getInstance();
        this.lunchRepository = LunchRepository.getInstance(MainApplication.getApplication());
    }

    public LiveData<List<User>> getAllWorkmates() {
        return workmateRepository.getAllWorkmates();
    }


    public LiveData<Lunch> getTodayLunch(String workmateId) {
        return lunchRepository.getTodayLunch(workmateId);
    }

    public LiveData<List<Lunch>> getTodayLunches() {
        return lunchRepository.getLunchesForToday();
    }
}