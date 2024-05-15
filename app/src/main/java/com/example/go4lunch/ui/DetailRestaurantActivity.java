package com.example.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.WorkmateRepository;
import com.example.go4lunch.view.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetailRestaurantActivity extends AppCompatActivity {

    RecyclerView recyclerView = null;
    UserAdapter adapter = null;
    Restaurant restaurant = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        Intent intent = getIntent();
        restaurant = (Restaurant) intent.getSerializableExtra("restaurant");

        configureComponante();
        configureRecyclerView();
        configureLike();
        configureRestaurantChoice();
        configureFab();

    }

    private void configureFab() {
        FloatingActionButton fab = findViewById(R.id.fabWorkmateWantToEat);
        fab.setOnClickListener(view -> {
            checkIfWorkmateChoseThisRestaurantForLunch();
        });
    }

    // modifier le nom de la methode car ce n'est pas un check !!!
    private void checkIfWorkmateChoseThisRestaurantForLunch() {

        User currentUser_2 = new User();
        currentUser_2.setId("loZeOfSdeMYU7WF8ilcWCMLsdLJ2");

        LunchRepository lunchRepository = LunchRepository.getInstance(MainApplication.getApplication());

        LiveData<Boolean> isChosenLiveData = lunchRepository.checkIfCurrentWorkmateChoseThisRestaurantForLunch(restaurant, currentUser_2.getId());

        isChosenLiveData.observe(this, isChosen -> {
            if (isChosen) {
                Log.d("WKM_2024", "delete Lunch > restaurant : " + restaurant.getId() + " User : " + currentUser_2.getId());
                lunchRepository.deleteLunch(restaurant, currentUser_2.getId());
            } else {
                Log.d("WKM_2024", "create Lunch > restaurant : " + restaurant.getId() + " User : " + currentUser_2.getId());
                lunchRepository.createLunch(restaurant, currentUser_2);
            }
        });
    }

    private void configureRestaurantChoice() {

        User currentUser_1 = new User();
        currentUser_1.setId("loZeOfSdeMYU7WF8ilcWCMLsdLJ2");

        LunchRepository lunchRepository = LunchRepository.getInstance(MainApplication.getApplication());

        LiveData<Boolean> isChosenLiveData = lunchRepository.checkIfCurrentWorkmateChoseThisRestaurantForLunch(restaurant, currentUser_1.getId());

        isChosenLiveData.observe(this, isChosen -> {
            FloatingActionButton fab = findViewById(R.id.fabWorkmateWantToEat);
            if (isChosen) {
                fab.setImageResource(R.drawable.ic_chosen);
            } else {
                fab.setImageResource(R.drawable.ic_not_chosen);
            }
        });
    }

    private void configureLike() {
        WorkmateRepository workmateRepository = new WorkmateRepository();
        LiveData<Boolean> isLikedLiveData = workmateRepository.checkIfCurrentWorkmateLikeThisRestaurant(restaurant);


        isLikedLiveData.observe(this, isLiked -> {
            ImageButton likeButton = findViewById(R.id.likeButton);
            if (isLiked) {
                likeButton.setImageResource(R.drawable.ic_star_on);
            } else {
                likeButton.setImageResource(R.drawable.ic_star_off);
            }
        });

        findViewById(R.id.likeButton).setOnClickListener(view -> {
            LiveData<Boolean> isLikedLiveData2 = workmateRepository.checkIfCurrentWorkmateLikeThisRestaurant(restaurant);

            isLikedLiveData2.observe(this, isLiked -> {
                ImageButton likeButton = findViewById(R.id.likeButton);
                if (isLiked) {
                    //changer l'image lors du clique donc off et pas on
                    likeButton.setImageResource(R.drawable.ic_star_off);
                    workmateRepository.deleteLikedRestaurant(restaurant);
                } else {
                    likeButton.setImageResource(R.drawable.ic_star_on);
                    workmateRepository.addLikedRestaurant(restaurant);
                }
            });
        });
    }


    private void configureRecyclerView(){

        adapter = new UserAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // utiliser le viewModel du LunchRepository
        LunchRepository lunchRepository = LunchRepository.getInstance(MainApplication.getApplication());
        LiveData<ArrayList<User>> workmateLiveData = lunchRepository.getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(restaurant);
        workmateLiveData.observe(this, workmates -> {
            if (workmates != null) {
                for (User workmate : workmates) {
                    Log.d("getAllWorkmates", "Workmate: " + workmate.getName());
                }
                adapter.updateList(workmates);
            } else {
                Log.d("getAllWorkmates", "No workmates found.");
                adapter.updateList(new ArrayList<>());
            }
        });

    }

    private void configureComponante(){

        recyclerView = findViewById(R.id.recyclerView);
        TextView restaurantNameTextView = findViewById(R.id.restaurantNameTextView);
        TextView restaurantAddressTextView = findViewById(R.id.restaurantAddressTextView);
        TextView restaurantPhotoTextView = findViewById(R.id.restaurantPhotoTextView);
        TextView restaurantOpeningHoursTextView = findViewById(R.id.restaurantOpeningHoursTextView);
        TextView restaurantStarsTextView = findViewById(R.id.restaurantStarsTextView);
        TextView restaurantWebsiteTextView = findViewById(R.id.restaurantWebsiteTextView);
        TextView restaurantTypeOfRestaurantTextView = findViewById(R.id.restaurantTypeOfRestaurantTextView);

        restaurantNameTextView.setText(restaurant.getName());
        restaurantAddressTextView.setText(restaurant.getAddress());
        restaurantPhotoTextView.setText(restaurant.getPhoto());
        restaurantOpeningHoursTextView.setText(restaurant.getOpeningHours());
        restaurantStarsTextView.setText(restaurant.getStars());
        restaurantWebsiteTextView.setText(restaurant.getWebsite());
        restaurantTypeOfRestaurantTextView.setText(restaurant.getTypeOfRestaurant());

    }

    /**
     * Used to navigate to this activity
     * @param activity the activity that calls this method
     */
    public static void navigate(MainActivity activity, Restaurant restaurant) {
        Intent anIntent = new Intent(activity, DetailRestaurantActivity.class);
        anIntent.putExtra("restaurant", (Serializable) restaurant);
        ActivityCompat.startActivity(activity, anIntent, null);
    }
}
