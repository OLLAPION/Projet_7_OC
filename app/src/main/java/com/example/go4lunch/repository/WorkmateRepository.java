package com.example.go4lunch.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
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

public class WorkmateRepository {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final String SUB_COLLECTION = "likedrestaurant" ;
    //mettre public
    private FirebaseUser getWorkmate() {
        return auth.getCurrentUser();
    }


    public Task<Void> signOut(Context context) {
        AuthUI authUI = AuthUI.getInstance();
        return authUI.signOut(context);
    }

    public static CollectionReference getWorkmatesCollection() {
        return FirebaseFirestore.getInstance().collection("workmates");
    }

    private User getFirebaseUserAsWorkmate() {

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

    public void createOrUpdateWorkmate() {
        createOrUpdateWorkmate(false);
    }

    public void createOrUpdateWorkmate(Boolean isNotificationActive) {
        User workmate = getFirebaseUserAsWorkmate();
        if (workmate != null) {
            workmate.setNotification(isNotificationActive);
            getWorkmatesCollection().document(workmate.getId()).set(workmate)
                .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("WR_createOrUpdateWork_1", "Workmate created/updated successfully: " + workmate.toString());
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        Log.e("WR_createOrUpdateWork_2", "Failed to create/update workmate", exception);
                    }
                }
            })
            .addOnFailureListener(e -> {
                Log.e("WR_createOrUpdateWork_3", "Failed to create/update workmate", e);
            });
        }
    }

    public LiveData<ArrayList<User>> getAllWorkmates() {
        MutableLiveData<ArrayList<User>> liveData = new MutableLiveData<>();
        getWorkmatesCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> workmates = new ArrayList<>();
                for (DocumentSnapshot snapshot : task.getResult()) {
                    User workmate = snapshot.toObject(User.class);
                    if (workmate != null) {
                        workmates.add(workmate);
                        Log.d("WR_getAllWorkmates_1", "User retrieved: " + workmate.getName());
                    } else {
                        Log.e("WR_getAllWorkmates_2", "Error: User is null for document ID: " + snapshot.getId());
                    }
                }
                liveData.setValue(workmates);
            } else {
                Log.e("WR_getAllWorkmates_3", "Error getting workmates", task.getException());
                liveData.setValue(new ArrayList<>());
            }
        })
        .addOnFailureListener(e -> {
            Log.e("WR_getAllWorkmates_4", "Failed to retrieve workmates", e);
            liveData.setValue(new ArrayList<>());
        });
        return liveData;
    }


    public void addLikedRestaurant(Restaurant restaurant) {
        User workmate = getFirebaseUserAsWorkmate();
        if (workmate != null) {
            getWorkmatesCollection().document(workmate.getId()).collection(SUB_COLLECTION).add(restaurant)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("WR_addLikedRestaurant_1", "Restaurant liked: " + restaurant.getName());
                } else {
                    Log.e("WR_addLikedRestaurant_2", "Failed to add liked restaurant", task.getException());
                }
            })
                    .addOnFailureListener(e -> {
                        Log.e("WR_addLikedRestaurant_3", "Failed to add liked restaurant", e);
                    });
        }
    }


    public void deleteLikedRestaurant(Restaurant restaurant) {
        User workmate = getFirebaseUserAsWorkmate();
        if (workmate != null) {
            getWorkmatesCollection().document(workmate.getId()).collection(SUB_COLLECTION)
                    .whereEqualTo("name", restaurant.getName())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("WR_deleteLikedRestaur_1", "Failed to query for liked restaurant", task.getException());
                            return;
                        }

                        Log.d("WR_deleteLikedRestaur_2", "Query for liked restaurant complete");

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            documentSnapshot.getReference().delete();
                        }

                    })
                    .addOnFailureListener(e -> {
                        Log.e("WR_deleteLikedRestaur_3", "Failed to query for liked restaurant", e);
                    });
        }
    }




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
                        Log.d("WR_checkIfCurrentWork_1", "Restaurant like status retrieved: " + isLiked);
                    } else {
                        Log.e("WR_checkIfCurrentWork_2", "Failed to query liked restaurants", task.getException());
                        isLikedLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("WR_checkIfCurrentWork_3", "Failed to query liked restaurants", e);
                    isLikedLiveData.setValue(false);
                });
    } else {
        Log.d("WR_checkIfCurrentWork_4", "Workmate is null");
        isLikedLiveData.setValue(false);
    }
    return isLikedLiveData;
}


    public LiveData<Boolean> getIsNotificationActive() {
        MutableLiveData<Boolean> isNotificationActiveLiveData = new MutableLiveData<>();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            //remplcer par la factorisation de la collection
            FirebaseFirestore.getInstance().collection("workmates")
                    .whereEqualTo("id", firebaseUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                Boolean notificationActive = task.getResult().toObjects(User.class).get(0).getNotification();
                                isNotificationActiveLiveData.postValue(notificationActive);
                                Log.d("NotificationActiveCheck", "Notification status retrieved: " + notificationActive);
                            } else {
                                isNotificationActiveLiveData.setValue(false);
                                Log.d("NotificationActiveCheck", "No document found for current user. Notifications assumed to be inactive.");
                            }
                        } else {
                            isNotificationActiveLiveData.setValue(false);
                            Log.e("NotificationActiveCheck", "Failed to retrieve notification status", task.getException());
                        }
                    });
        } else {
            isNotificationActiveLiveData.setValue(false);
            Log.d("NotificationActiveCheck", "Current user is null. Notifications assumed to be inactive.");
        }

        return isNotificationActiveLiveData;
    }



}
