package com.example.go4lunch.model.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.bo.Restaurant;
import com.example.go4lunch.model.bo.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

/**
 * Repository responsible for managing workmates (users) in the application.
 * Handles authentication, retrieving users, managing liked restaurants, and notification settings.
 */
public class WorkmateRepository {

    // Singleton instance
    private static WorkmateRepository instance;

    // Firebase instances
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    // Firestore sub-collection for liked restaurants
    private static final String SUB_COLLECTION = "likedrestaurant" ;

    // TAG for logs
    private final String TAG = "WR";

    // Private constructor for Singleton pattern
    public WorkmateRepository() {
    }

    /**
     * Singleton pattern to ensure only one instance of the repository is used.
     *
     * @return The single instance of WorkmateRepository.
     */
    public static  WorkmateRepository getInstance() {
        if (instance == null) {
            instance = new WorkmateRepository();
        }
        return instance;
    }

    /**
     * Signs out the currently authenticated user.
     *
     * @param context The application context.
     * @return A Task representing the sign-out process.
     */
    public Task<Void> signOut(Context context) {
        AuthUI authUI = AuthUI.getInstance();
        return authUI.signOut(context);
    }

    /**
     * Gets the Firestore collection reference for workmates.
     *
     * @return The collection reference for "workmates".
     */
    public static CollectionReference getWorkmatesCollection() {
        return FirebaseFirestore.getInstance().collection("workmates");
    }

    /**
     * Converts the current Firebase user into a User object.
     *
     * @return A User object representing the Firebase user or null if no user is authenticated.
     */
    public User getFirebaseUserAsWorkmate() {

        FirebaseUser firebaseUser = auth.getCurrentUser();
            if (firebaseUser != null) {
            String avatarUrl = (firebaseUser.getPhotoUrl() != null) ? firebaseUser.getPhotoUrl().toString() : null;
            return new User(
                    firebaseUser.getUid(),
                    firebaseUser.getDisplayName(),
                    firebaseUser.getEmail(),
                    avatarUrl
            );
        }
        return null;
    }

    /**
     * Creates or updates the current workmate's information in Firestore.
     *
     * @param isNotificationActive True if notifications are enabled, false otherwise.
     */
    public void createOrUpdateWorkmate(Boolean isNotificationActive) {
        User workmate = getFirebaseUserAsWorkmate();
        if (workmate != null) {
            // Set notification status
            workmate.setNotification(isNotificationActive);
            // merges fields instead of replacing the entire document
            getWorkmatesCollection().document(workmate.getId()).set(workmate, SetOptions.merge())
                .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Workmate created/updated successfully: " + workmate.toString());
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        Log.e(TAG, "Failed to create/update workmate", exception);
                    }
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to create/update workmate", e);
            });
        }
    }


    /**
     * Retrieves  all workmates from Firestore.
     *
     * @return LiveData containing a list of workmates.
     */
    public LiveData<List<User>> getAllWorkmates() {
        MutableLiveData<List<User>> liveData = new MutableLiveData<>();

        // Fetching all documents from the "workmates" collection
        getWorkmatesCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> workmates = new ArrayList<>();
                for (DocumentSnapshot snapshot : task.getResult()) {
                    User workmate = snapshot.toObject(User.class);
                    if (workmate != null) {
                        workmates.add(workmate);
                        Log.d(TAG, "User retrieved: " + workmate.getName());
                    } else {
                        Log.e(TAG, "Error: User is null for document ID: " + snapshot.getId());
                    }
                }
                liveData.setValue(workmates);
            } else {
                Log.e(TAG, "Error getting workmates", task.getException());
                liveData.setValue(new ArrayList<>());
            }
        })
        .addOnFailureListener(e -> {
            Log.e(TAG, "Failed to retrieve workmates", e);
            liveData.setValue(new ArrayList<>());
        });
        return liveData;
    }

    /**
     * Adds a liked restaurant for the current user.
     *
     * @param restaurant The restaurant to be liked.
     */
    public void addLikedRestaurant(Restaurant restaurant) {
        User workmate = getFirebaseUserAsWorkmate();
        if (workmate != null) {
            getWorkmatesCollection().document(workmate.getId()).collection(SUB_COLLECTION).add(restaurant)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Restaurant liked: " + restaurant.getName());
                } else {
                    Log.e(TAG, "Failed to add liked restaurant", task.getException());
                }
            })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to add liked restaurant", e);
                    });
        }
    }

    /**
     * Deletes a liked restaurant for the current user.
     *
     * @param restaurant The restaurant to be unliked.
     */
    public void deleteLikedRestaurant(Restaurant restaurant) {
        User workmate = getFirebaseUserAsWorkmate();
        if (workmate != null) {
            getWorkmatesCollection().document(workmate.getId()).collection(SUB_COLLECTION)
                    .whereEqualTo("name", restaurant.getName())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Failed to query for liked restaurant", task.getException());
                            return;
                        }
                        Log.d(TAG, "Query for liked restaurant complete");
                        // Delete all documents that match the query
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            documentSnapshot.getReference().delete();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to query for liked restaurant", e);
                    });
        }
    }

    /**
     * Checks if the current workmate has liked a specific restaurant.
     *
     * @param restaurant The restaurant to check.
     * @return LiveData containing true if liked, false otherwise.
     */
    public LiveData<Boolean> checkIfCurrentWorkmateLikeThisRestaurant(Restaurant restaurant) {
        MutableLiveData<Boolean> isLikedLiveData = new MutableLiveData<>();
        User workmate = getFirebaseUserAsWorkmate();
        if (workmate != null) {
         getWorkmatesCollection().document(workmate.getId()).collection(SUB_COLLECTION)
                    .whereEqualTo("id", restaurant.getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean isLiked = !task.getResult().isEmpty();
                            isLikedLiveData.setValue(isLiked);
                            Log.d(TAG, "Restaurant like status retrieved: " + isLiked);
                        } else {
                            Log.e(TAG, "Failed to query liked restaurants", task.getException());
                            isLikedLiveData.setValue(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to query liked restaurants", e);
                        isLikedLiveData.setValue(false);
                    });
        } else {
            Log.d(TAG, "Workmate is null");
            isLikedLiveData.setValue(false);
        }
        return isLikedLiveData;
    }

    /**
     * Retrieves the notification status for the current workmate.
     *
     * @return LiveData indicating whether notifications are active.
     */
    public LiveData<Boolean> getIsNotificationActive() {
        MutableLiveData<Boolean> isNotificationActiveLiveData = new MutableLiveData<>();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            getWorkmatesCollection()
                    .whereEqualTo("id", firebaseUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                Boolean notificationActive = task.getResult().toObjects(User.class).get(0).getNotification();
                                isNotificationActiveLiveData.postValue(notificationActive);
                                Log.d(TAG, "Notification status retrieved: " + notificationActive);
                            } else {
                                isNotificationActiveLiveData.setValue(false);
                                Log.d(TAG, "No document found for current user. Notifications assumed to be inactive.");
                            }
                        } else {
                            isNotificationActiveLiveData.setValue(false);
                            Log.e(TAG, "Failed to retrieve notification status", task.getException());
                        }
                    });
        } else {
            isNotificationActiveLiveData.setValue(false);
            Log.d(TAG, "Current user is null. Notifications assumed to be inactive.");
        }

        return isNotificationActiveLiveData;
    }
}
