package com.example.go4lunch.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.WorkmateRepository;
import com.example.go4lunch.view.DetailRestaurantViewModel;
import com.example.go4lunch.view.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// le like ne fonctionne pas sur l'emulateur, mais uniquement sur mon telephone
public class DetailRestaurantActivity extends AppCompatActivity {

    /** The recyclerview to display the list of workmates who have chosen to eat in the restaurant */
    RecyclerView recyclerView = null;

    /** The UserAdapter : for the workmates */
    UserAdapter adapter = null;

    /** The Restaurant Object */
    Restaurant restaurant = null;

    /** The repository : WorkmateRepository */
    private DetailRestaurantViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        viewModel = new ViewModelProvider(this).get(DetailRestaurantViewModel.class);

        Intent intent = getIntent();
        restaurant = (Restaurant) intent.getSerializableExtra("restaurant");

        configureComponante();
        configureRecyclerView();
        configureLike();
        configureRestaurantChoice();
        configureFab();
    }

    /**
     * Configures the FloatingActionButton to allow users to mark or unmark a restaurant for lunch.
     */
    private void configureFab() {
        FloatingActionButton fab = findViewById(R.id.fabWorkmateWantToEat);
        fab.setOnClickListener(view -> {
            handleWorkmateLunchChoice();

        });

    }

    /**
     * Checks if a workmate has chosen this restaurant for lunch.
     * If yes, removes the lunch.
     * If no, creates a lunch for this restaurant.
     */
    private void handleWorkmateLunchChoice() {

        User currentUser_2 = new User();
        currentUser_2.setId("D7rZ2O9j8vVHuLwxHrgnrLTT3mv1");
        currentUser_2.setName("Name_2");
        currentUser_2.setAvatar("Avatar_2");

        viewModel.checkIfWorkmateChoseThisRestaurantForLunch(restaurant, currentUser_2.getId())
                .observe(this, isChosen -> {
                    if (isChosen) {
                        Log.d("WKM_2024", "delete Lunch > restaurant : " + restaurant.getId() + " User : " + currentUser_2.getId());
                        viewModel.deleteLunch(restaurant, currentUser_2.getId(), () -> {
                            configureRecyclerView();
                            configureRestaurantChoice();
                        });
                    } else {
                        Log.d("WKM_2024", "create Lunch > restaurant : " + restaurant.getId() + " User : " + currentUser_2.getId());
                        viewModel.createLunch(restaurant, currentUser_2, () -> {
                            configureRecyclerView();
                            configureRestaurantChoice();
                        });
                    }

                });
    }

    /**
     * Configures the appearance of the FloatingActionButton based on the user's restaurant choice.
     */
    private void configureRestaurantChoice() {

        User currentUser_1 = new User();
        currentUser_1.setId("D7rZ2O9j8vVHuLwxHrgnrLTT3mv1");
        currentUser_1.setName("Name_2");
        currentUser_1.setAvatar("Avatar_2");
        Log.d("FAB_1", "check fzb : ");

        viewModel.getIsRestaurantChosenLiveData(restaurant, currentUser_1.getId()).observe(this, isChosen -> {
            FloatingActionButton fab = findViewById(R.id.fabWorkmateWantToEat);
            Log.d("FAB_2", "check fzb : " + isChosen);
            if (isChosen) {
                fab.setImageResource(R.drawable.ic_chosen);
            } else {
                fab.setImageResource(R.drawable.ic_not_chosen);
            }
        });

    }


    /**
     * Configures the appearance of the "like" icon based on the workmates like this restaurant.
     * Add & Delete from firebase using the ViewModel
     */
    private void configureLike() {
        viewModel.checkIfWorkmateLikeThisRestaurant(restaurant).observe(this, isLiked -> {
            ImageView restaurantStar = findViewById(R.id.restaurantStar);
            if (isLiked) {
                restaurantStar.setImageResource(R.drawable.ic_star_on);
            } else {
                restaurantStar.setImageResource(R.drawable.ic_star_off);
            }
        });

        findViewById(R.id.likeButton).setOnClickListener(view -> {
            LiveData<Boolean> isLikedLiveData2 = viewModel.checkIfWorkmateLikeThisRestaurant(restaurant);

            isLikedLiveData2.observe(this, isLiked -> {
                ImageView restaurantStar2 = findViewById(R.id.restaurantStar);
                if (isLiked) {
                    restaurantStar2.setImageResource(R.drawable.ic_star_off);
                    viewModel.deleteLikedRestaurant(restaurant);
                } else {
                    restaurantStar2.setImageResource(R.drawable.ic_star_on);
                    viewModel.addLikedRestaurant(restaurant);
                }
            });
        });
    }


    /**
     * Configures the RecyclerView to display the list of workmates who have already chosen this restaurant for lunch.
     */
    private void configureRecyclerView(){

        adapter = new UserAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        viewModel.getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(restaurant)
                .observe(this, workmates -> {
                    if (workmates != null) {
                        Log.d("RecyclerViewDebug_1", "Number of users retrieved: " + workmates.size());
                        for (User workmate : workmates) {
                            Log.d("RecyclerViewDebug_2", "Workmate name: " + workmate.getName());
                        }
                        adapter.updateList(workmates);
                    } else {
                        Log.d("RecyclerViewDebug_3", "No workmates found.");
                        adapter.updateList(new ArrayList<>());
                    }
                });
    }

    /**
     * Initializes the visual components of the activity with the restaurant details.
     */
    private void configureComponante(){

        recyclerView = findViewById(R.id.recyclerView);
        TextView restaurantNameTextView = findViewById(R.id.restaurantNameTextView);
        TextView restaurantAddressTextView = findViewById(R.id.restaurantAddressTextView);
        TextView restaurantTypeOfRestaurantTextView = findViewById(R.id.restaurantTypeOfRestaurantTextView);
        ImageView restaurantPhotoImageView = findViewById(R.id.restaurantPhotoImageView);
        ImageView restaurantStarsImageView = findViewById(R.id.restaurantStar);
        ImageButton restaurantWebsiteImageButton = findViewById(R.id.restaurantWebsiteButton);

        restaurantNameTextView.setText(restaurant.getName());
        restaurantAddressTextView.setText(restaurant.getAddress());
        restaurantTypeOfRestaurantTextView.setText(restaurant.getTypeOfRestaurant());

        // Supprimez les lignes de chargement avec Picasso
        // Picasso.get().load(restaurant.getPhoto()).into(restaurantPhotoImageView);
        // Picasso.get().load(restaurant.getPhoto()).into(restaurantStarsImageView);
        // Picasso.get().load(restaurant.getPhoto()).into(restaurantWebsiteImageButton);

        Glide.with(this)
                .load(restaurant.getPhoto())
                .apply(new RequestOptions().centerCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(restaurantPhotoImageView);

        Glide.with(this)
                .load(R.drawable.ic_star_button)
                .into(restaurantStarsImageView);

        Glide.with(this)
                .load(R.drawable.ic_call)
                .into(restaurantStarsImageView);

        Glide.with(this)
                .load(R.drawable.ic_website)
                .into(restaurantWebsiteImageButton);
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
