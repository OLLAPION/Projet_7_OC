package com.example.go4lunch.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.WorkmateRepository;
import java.util.ArrayList;

// Faire le CRUD pour le ViewModel ???
// mon observeForever peut-il faire une fuite mémoire ???
// Un ViewModel par Repository ou un ViewModel par activity/fragment

public class DetailRestaurantViewModel extends ViewModel {

    /** The repository : LunchRepository */
    private static LunchRepository lunchRepository;

    /** The repository : WorkmateRepository */
    private WorkmateRepository workmateRepository;

    /**
     * Constructor to initialize the ViewModel with the repository's lunchRepository and workmateRepository.
     */
    public DetailRestaurantViewModel() {
        lunchRepository = LunchRepository.getInstance(MainApplication.getApplication());
        workmateRepository = new WorkmateRepository();
    }


    /**
     * Checks if the workmate chose the restaurant for lunch.
     * @param restaurant The restaurant to check.
     * @param userId The workmate ID.
     * @return LiveData representing whether the current workmate likes the restaurant.
     */
    public LiveData<Boolean> checkIfWorkmateChoseThisRestaurantForLunch(Restaurant restaurant, String userId) {
        MutableLiveData<Boolean> isChosenLiveData = new MutableLiveData<>();
        lunchRepository.checkIfCurrentWorkmateChoseThisRestaurantForLunch(restaurant, userId)
                .observeForever(isChosenLiveData::setValue);
        return isChosenLiveData;
    }

    /**
     * Gets LiveData to check if the restaurant is chosen by the user.
     * @param restaurant The restaurant to check.
     * @param userId The workmate ID.
     * @return LiveData representing whether the restaurant is chosen by the user.
     */
    public LiveData<Boolean> getIsRestaurantChosenLiveData(Restaurant restaurant, String userId) {
        return lunchRepository.checkIfCurrentWorkmateChoseThisRestaurantForLunch(restaurant, userId);
    }

    /**
     * Checks if the current workmate likes the restaurant.
     * @param restaurant The restaurant to check.
     * @return LiveData representing whether the current workmate likes the restaurant.
     */
    public LiveData<Boolean> checkIfWorkmateLikeThisRestaurant(Restaurant restaurant) {
        return workmateRepository.checkIfCurrentWorkmateLikeThisRestaurant(restaurant);
    }

    /**
     * Gets LiveData representing the list of workmates who have chosen the restaurant for today's lunch.
     * @param restaurant The restaurant to check.
     * @return LiveData representing the list of workmates who have chosen the restaurant for today's lunch.
     */
    public LiveData<ArrayList<User>> getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(Restaurant restaurant) {
        return lunchRepository.getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(restaurant);
    }


    /**
     * Creates a lunch for the specified restaurant and workmate.
     * @param restaurant The restaurant for which to create the lunch.
     * @param currentUser The current workmate.
     */
    public void createLunch(Restaurant restaurant, User currentUser) {
        lunchRepository.createLunch(restaurant, currentUser);
    }

    /**
     * Deletes the lunch for the specified restaurant and workmate.
     * @param restaurant The restaurant for which to delete the lunch.
     * @param userId The ID of the workmate.
     */
    public void deleteLunch(Restaurant restaurant, String userId) {
        lunchRepository.deleteLunch(restaurant, userId);
    }

    /**
     * Deletes the liked status of the restaurant.
     * @param restaurant The restaurant for which to delete the liked status.
     */
    public void deleteLikedRestaurant(Restaurant restaurant) {
        workmateRepository.deleteLikedRestaurant(restaurant);
    }

    /**
     * Adds the liked status to the restaurant.
     * @param restaurant The restaurant for which to add the liked status.
     */
    public void addLikedRestaurant(Restaurant restaurant) {
        workmateRepository.addLikedRestaurant(restaurant);
    }
}
