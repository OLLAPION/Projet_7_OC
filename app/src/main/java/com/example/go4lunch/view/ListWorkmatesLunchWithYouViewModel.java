package com.example.go4lunch.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.LunchRepository;

import java.util.ArrayList;

public class ListWorkmatesLunchWithYouViewModel extends ViewModel {

    private final LunchRepository lunchRepository;
    private final MutableLiveData<ArrayList<User>> workmatesLiveData = new MutableLiveData<>();


    public ListWorkmatesLunchWithYouViewModel(LunchRepository lunchRepository) {
        this.lunchRepository = lunchRepository;
    }

    public void fetchWorkmatesForTodayLunch(Restaurant restaurant) {
        lunchRepository.getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(restaurant)
                .observeForever(workmates -> workmatesLiveData.setValue(workmates));
    }

    public LiveData<ArrayList<User>> getWorkmatesLiveData() {
        return workmatesLiveData;
        // dans User mettre un getAllWorkmate
    }
}