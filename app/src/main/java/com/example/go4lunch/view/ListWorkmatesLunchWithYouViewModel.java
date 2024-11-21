package com.example.go4lunch.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.WorkmateRepository;

import java.util.ArrayList;

public class ListWorkmatesLunchWithYouViewModel extends ViewModel {

    private final WorkmateRepository workmateRepository;


    public ListWorkmatesLunchWithYouViewModel() {
        this.workmateRepository = WorkmateRepository.getInstance();
    }

    public LiveData<ArrayList<User>> getAllWorkmates() {
        return workmateRepository.getAllWorkmates();
    }
}