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

public class LunchRepository {
    private final MutableLiveData<List<Lunch>> listOfLunch = new MutableLiveData<>();

    private static LunchRepository sLunchRepository;


    private LunchRepository(Context context) {
        FirebaseApp.initializeApp(context);
    }

    public static LunchRepository getInstance(Context context) {
        if (sLunchRepository == null) {
            sLunchRepository = new LunchRepository(context);
        }
        return sLunchRepository;
    }

    private static Long toDay() {
        return Instant.now().truncatedTo(ChronoUnit.DAYS).toEpochMilli();
    }

    public void createLunch(Restaurant restaurantChosen, User workmate, CreateLunchCallBack callback) {
        if (restaurantChosen == null || workmate == null) {
            Log.d("LR_createLunch_1", "Error: restaurantChosen or workmate is null");
            return;
        }

        // objet Restaurant a un ID
        if (restaurantChosen.getId() == null || restaurantChosen.getId().isEmpty()) {
            Log.d("LR_createLunch_2", "Error: restaurantChosen has no valid ID");
            return;
        }

        // objet User a un ID
        if (workmate.getId() == null || workmate.getId().isEmpty()) {
            Log.d("LR_createLunch_3", "Error: workmate has no valid ID");
            return;
        }

        Lunch lunch = new Lunch(restaurantChosen, workmate);
        lunch.setDayDate(toDay());

        FirebaseFirestore.getInstance().collection("Lunch")
                .add(lunch)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LR_createLunch_4", "Lunch created successfully! Document ID: " + task.getResult().getId());
                        callback.onCreated();
                    } else {
                        Log.e("LR_createLunch_5", "Error creating lunch: " + task.getException().getMessage());
                    }
                })

                .addOnFailureListener(e -> {
                    Log.e("LR_createLunch_6", "Error creating lunch: " + e.getMessage());
                });
    }

    private Query getBaseQuery1(Restaurant restaurant, String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        if (restaurant == null || restaurant.getId() == null || restaurant.getId().isEmpty()
                 || userId == null || userId.isEmpty()) {
            Log.e("LR_getBaseQuery_1", "Error: Invalid parameters in getBaseQuery");
            return null;
        }
        Log.d("LR_GetBaseQuery_1", "getBaseQuery1 > restaurant : " + restaurant.getId() + " User : " + userId + " Day : " + toDay());
        return firestore.collection("Lunch")
                .whereEqualTo("restaurant.id", restaurant.getId())
                .whereEqualTo("dayDate", toDay())
                .whereEqualTo("user.id", userId);
    }

    // faire un getBaseQuery4 pour les lunch de la journée pour le fetchFerstaurant de ListRestaurantFragment
    private Query getBaseQuery2(Restaurant restaurant) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        if (restaurant == null || restaurant.getId() == null || restaurant.getId().isEmpty()) {
            Log.e("LR_getBaseQuery_2", "Error: Invalid parameters in getBaseQuery");
            return null;
        }

        return firestore.collection("Lunch")
                .whereEqualTo("restaurant.id", restaurant.getId())
                .whereEqualTo("dayDate", toDay());
    }

    private Query getBaseQuery3(String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        if (userId == null || userId.isEmpty()) {
            Log.e("LR_getBaseQuery_3", "Error: Invalid parameters in getBaseQuery");
            return null;
        }

        return firestore.collection("Lunch")
                .whereEqualTo("dayDate", toDay())
                .whereEqualTo("user.id", userId);
    }


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
                            todayLunch.postValue(lunch);
                            Log.d("LR_getTodayLunch_1", "Lunch retrieved successfully: " + lunch.toString());
                        } else {
                            todayLunch.postValue(null);
                            Log.d("LR_getTodayLunch_2", "No lunch found for today.");
                        }
                    } else {
                        // Gérer l'échec
                        Exception exception = task.getException();
                        if (exception != null) {
                            Log.e("LR_getTodayLunch_3", "Failed to retrieve lunch", exception);
                        }
                        todayLunch.postValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    todayLunch.postValue(null);
                    Log.e("LR_getTodayLunch_4", "Failed to retrieve lunch", e);
                });

        return todayLunch;
    }

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
                                lunches.add(lunch);
                            }
                            lunchesLiveData.postValue(lunches);
                        } else {
                            lunchesLiveData.postValue(lunches);
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            Log.e("LR_getLunchesForToday", "Failed to retrieve lunches", exception);
                        }
                        lunchesLiveData.postValue(lunches);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LR_getLunchesForToday", "Failed to retrieve lunches", e);
                    lunchesLiveData.postValue(lunches);
                });

        return lunchesLiveData;
    }

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
                                workmates.add(user);
                            }
                            workmatesLiveData.postValue(workmates);
                            Log.d("LR_getWorkmatesTh_1", "Workmates who already chose restaurant for today's lunch retrieved successfully: " + workmates.toString());
                        } else {
                            workmatesLiveData.postValue(workmates);
                            Log.d("LR_getWorkmatesTh_2", "No workmates found who chose restaurant for today's lunch for restaurant: " + restaurant.getName());
                        }
                    } else {
                        // Handling failure
                        Exception exception = task.getException();
                        if (exception != null) {
                           Log.e("LR_getWorkmatesTh_3", "Failed to retrieve workmates who chose restaurant for today's lunch", exception);
                        }
                        workmatesLiveData.postValue(workmates);
                    }
                })
                .addOnFailureListener(e -> {
                    workmatesLiveData.postValue(workmates);
                    Log.e("LR_getWorkmatesTh_4", "Failed to retrieve workmates who chose restaurant for today's lunch", e);
                });

        return workmatesLiveData;
    }

    public MutableLiveData<Boolean> checkIfCurrentWorkmateChoseThisRestaurantForLunch(Restaurant restaurant, String userId) {
        MutableLiveData<Boolean> isChosenLiveData = new MutableLiveData<>();

        getBaseQuery1(restaurant, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            isChosenLiveData.postValue(true);
                            Log.d("LR_checkIfCurrentWork_1", "Current workmate chose this restaurant for lunch: " + restaurant.getName());
                        } else {
                            isChosenLiveData.postValue(false);
                            Log.d("LR_checkIfCurrentWork_2", "Current workmate did not choose this restaurant for lunch: " + restaurant.getName());
                        }
                    } else {
                        // Gérer l'échec
                        Exception exception = task.getException();
                        if (exception != null) {
                            Log.e("LR_checkIfCurrentWork_3", "Failed to check if current workmate chose this restaurant for lunch", exception);
                        }
                        isChosenLiveData.postValue(false);
                    }
                })
                .addOnFailureListener(e -> {
                    isChosenLiveData.postValue(false);
                    Log.e("LR_checkIfCurrentWork_4", "Failed to check if current workmate chose this restaurant for lunch", e);
                });

        return isChosenLiveData;
    }

    public void deleteLunch(Restaurant restaurant, String userId, DeleteLunchCallBack callback) {
        getBaseQuery1(restaurant, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                document.getReference().delete();
                            }
                        }
                        Log.e("LR_deleteLunch_0", "deleteLunch successfull");
                        callback.onDeleted();
                    } else {
                        // Si la tâche échoue à être en succès
                        Exception exception = task.getException();
                        if (exception != null) {
                            Log.e("LR_deleteLunch_1", "Task failed to be successful", exception);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LR_deleteLunch_2", "Failed to execute task", e);
                });
    }
}
