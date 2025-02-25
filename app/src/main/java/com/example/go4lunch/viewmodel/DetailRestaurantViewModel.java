package com.example.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.model.repository.callback.CreateLunchCallBack;
import com.example.go4lunch.model.repository.callback.DeleteLunchCallBack;
import com.example.go4lunch.model.bo.Restaurant;
import com.example.go4lunch.model.bo.User;
import com.example.go4lunch.model.repository.LunchRepository;
import com.example.go4lunch.model.repository.WorkmateRepository;
import java.util.ArrayList;

/**
 * ViewModel for the DetailRestaurantActivity
 */
public class DetailRestaurantViewModel extends ViewModel {

    // The repository : LunchRepository
    private static LunchRepository lunchRepository;

    // The repository : WorkmateRepository
    private WorkmateRepository workmateRepository;

    /**
     * Constructor to initialize the ViewModel with the repository's lunchRepository and workmateRepository.
     */
    public DetailRestaurantViewModel() {
        lunchRepository = LunchRepository.getInstance(MainApplication.getApplication());
        workmateRepository = WorkmateRepository.getInstance();
    }

    /**
     * Checks if the workmate chose this restaurant for lunch.
     * @param restaurant The restaurant to check.
     * @param userId The workmate ID.
     * @return LiveData representing whether the workmate selected this restaurant for Lunch.
     */
    public LiveData<Boolean> checkIfWorkmateChoseThisRestaurantForLunch(Restaurant restaurant, String userId) {
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
    public void createLunch(Restaurant restaurant, User currentUser, CreateLunchCallBack callBack) {
        lunchRepository.createLunch(restaurant, currentUser, callBack);
    }

    /**
     * Deletes the lunch for the specified restaurant and workmate.
     * @param restaurant The restaurant for which to delete the lunch.
     * @param userId The ID of the workmate.
     */
    public void deleteLunch(Restaurant restaurant, String userId, DeleteLunchCallBack callback) {
        lunchRepository.deleteLunch(restaurant, userId, callback);
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
