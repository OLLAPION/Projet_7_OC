package com.example.go4lunch.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.callback.CreateLunchCallBack;
import com.example.go4lunch.callback.DeleteLunchCallBack;
import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Singleton class responsible for managing lunch data using Firebase Firestore.
 * Handles operations like creating, retrieving, and deleting Lunch objects.
 */
public class LunchRepository {

    // Singleton instance of LunchRepository
    private static LunchRepository sLunchRepository;

    // TAG for logs
    private final String TAG = "LR";

    /**
     * Private constructor to initialize Firebase.
     * @param context Application context.
     */
    private LunchRepository(Context context) {
        FirebaseApp.initializeApp(context);
    }

    /**
     * Returns the singleton instance of LunchRepository.
     * @param context Application context.
     * @return Instance of LunchRepository.
     */
    public static LunchRepository getInstance(Context context) {
        if (sLunchRepository == null) {
            sLunchRepository = new LunchRepository(context);
        }
        return sLunchRepository;
    }

    /**
     * Converts the current date to a truncated timestamp.
     * @return The truncated current timestamp in milliseconds.
     */
    private static Long toDay() {
        return Instant.now().truncatedTo(ChronoUnit.DAYS).toEpochMilli();
    }

    /**
     * Creates a new lunch for a given restaurant and workmate after removing any previous lunch choices for the day.
     * @param restaurantChosen The selected restaurant for lunch.
     * @param workmate The user who chose the lunch.
     * @param callback The callback to notify the result.
     */
    public void createLunch(Restaurant restaurantChosen, User workmate, CreateLunchCallBack callback) {
        // Check if restaurantChosen and workmate are valid
        if (restaurantChosen == null || workmate == null) {
            Log.d(TAG, "Error: restaurantChosen or workmate is null");
            return;
        }

        // Delete previous lunches for the workmate
        getBaseQuery3(workmate.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Delete existing lunches for the day
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                document.getReference().delete();
                            }
                        }
                    }

                    // Add the new lunch to Firestore
                    Lunch lunch = new Lunch(restaurantChosen, workmate);
                    lunch.setDayDate(toDay());

                    FirebaseFirestore.getInstance().collection("Lunch")
                            .add(lunch)
                            .addOnCompleteListener(addTask -> {
                                if (addTask.isSuccessful()) {
                                    Log.d(TAG, "Lunch created successfully!");
                                    callback.onCreated();
                                } else {
                                    Log.e(TAG, "Error creating lunch");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error creating lunch");
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting previous lunches");
                });
    }

    /**
     * Creates a Firestore query to fetch a specific lunch for a restaurant and user.
     * @param restaurant The restaurant for the lunch.
     * @param userId The ID of the user.
     * @return Firestore query object.
     */
    private Query getBaseQuery1(Restaurant restaurant, String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        if (restaurant == null || userId == null) {
            Log.e(TAG, "Error: Invalid parameters");
            return null;
        }
        return firestore.collection("Lunch")
                .whereEqualTo("restaurant.id", restaurant.getId())
                .whereEqualTo("dayDate", toDay())
                .whereEqualTo("user.id", userId);
    }

    /**
     * Creates a Firestore query to fetch all lunches for a specific restaurant on the current day.
     * @param restaurant The restaurant for which we want the lunch choices.
     * @return Firestore query object.
     */
    private Query getBaseQuery2(Restaurant restaurant) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        if (restaurant == null) {
            Log.e(TAG, "Error: Invalid parameters");
            return null;
        }
        return firestore.collection("Lunch")
                .whereEqualTo("restaurant.id", restaurant.getId())
                .whereEqualTo("dayDate", toDay());
    }

    /**
     * Creates a Firestore query to fetch all lunches for a specific user on the current day.
     * @param userId The ID of the user.
     * @return Firestore query object.
     */
    private Query getBaseQuery3(String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        if (userId == null) {
            Log.e(TAG, "Error: Invalid parameters");
            return null;
        }
        return firestore.collection("Lunch")
                .whereEqualTo("dayDate", toDay())
                .whereEqualTo("user.id", userId);
    }

    /**
     * Retrieves the lunch chosen by a specific workmate for the current day.
     * @param workmateId The ID of the workmate.
     * @return LiveData object containing the lunch for today.
     */
    public LiveData<Lunch> getTodayLunch(String workmateId) {
        MutableLiveData<Lunch> todayLunch = new MutableLiveData<>();
        getBaseQuery3(workmateId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            Lunch lunch = document.toObject(Lunch.class);
                            todayLunch.postValue(lunch);  // Post the Lunch object
                        } else {
                            todayLunch.postValue(null);  // No Lunch found
                        }
                    } else {
                        todayLunch.postValue(null);  // Query failed
                    }
                })
                .addOnFailureListener(e -> todayLunch.postValue(null));  // Query failure
        return todayLunch;
    }

    /**
     * Retrieves all lunches for the current day.
     * @return LiveData object containing a list of lunches for today.
     */
    public LiveData<List<Lunch>> getLunchesForToday() {
        MutableLiveData<List<Lunch>> lunchesLiveData = new MutableLiveData<>();
        ArrayList<Lunch> lunches = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Lunch")
                .whereEqualTo("dayDate", toDay())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Lunch lunch = document.toObject(Lunch.class);
                                lunches.add(lunch);  // Add lunch to the list
                            }
                            lunchesLiveData.postValue(lunches); // Post list of lunches
                        } else {
                            lunchesLiveData.postValue(lunches); // No lunches found
                        }
                    } else {
                        lunchesLiveData.postValue(lunches); // Query failed
                    }
                })
                .addOnFailureListener(e -> lunchesLiveData.postValue(lunches)); // Query failure
        return lunchesLiveData;
    }

    /**
     * Retrieves a list of workmates who have chosen a specific restaurant for lunch today.
     * @param restaurant The restaurant selected for lunch.
     * @return LiveData object containing a list of workmates who have chosen the restaurant.
     */
    public LiveData<ArrayList<User>> getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(Restaurant restaurant) {
        MutableLiveData<ArrayList<User>> workmatesLiveData = new MutableLiveData<>();
        ArrayList<User> workmates = new ArrayList<>();

        getBaseQuery2(restaurant)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Lunch lunch = document.toObject(Lunch.class);
                                User user = lunch.getUser();
                                workmates.add(user); // Add workmate to the list
                            }
                            workmatesLiveData.postValue(workmates); // Post list of workmates
                        } else {
                            workmatesLiveData.postValue(workmates); // No workmates found
                        }
                    } else {
                        workmatesLiveData.postValue(workmates); // Query failed
                    }
                })
                .addOnFailureListener(e -> workmatesLiveData.postValue(workmates)); // Query failure
        return workmatesLiveData;
    }

    /**
     * Checks if a specific workmate has already chosen a specific restaurant for lunch today.
     * @param restaurant The restaurant to check.
     * @param userId The ID of the user.
     * @return LiveData object containing a boolean indicating whether the user chose the restaurant.
     */
    public MutableLiveData<Boolean> checkIfCurrentWorkmateChoseThisRestaurantForLunch(Restaurant restaurant, String userId) {
        MutableLiveData<Boolean> isChosenLiveData = new MutableLiveData<>();
        getBaseQuery1(restaurant, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            isChosenLiveData.postValue(true); // Restaurant chosen
                        } else {
                            isChosenLiveData.postValue(false); // Restaurant not chosen
                        }
                    } else {
                        isChosenLiveData.postValue(false); // Query failed
                    }
                })
                .addOnFailureListener(e -> isChosenLiveData.postValue(false)); // Query failure
        return isChosenLiveData;
    }

    /**
     * Deletes a specific lunch for a user and restaurant.
     * @param restaurant The restaurant for which the lunch will be deleted.
     * @param userId The ID of the user whose lunch will be deleted.
     * @param callback The callback to notify when the deletion is complete.
     */
    public void deleteLunch(Restaurant restaurant, String userId, DeleteLunchCallBack callback) {
        getBaseQuery1(restaurant, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                document.getReference().delete(); // Delete the lunch document
                            }
                        }
                        callback.onDeleted(); // Notify callback on success
                    } else {
                        // Handle failure
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to execute task", e);
                });
    }
}
