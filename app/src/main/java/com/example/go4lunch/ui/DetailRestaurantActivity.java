package com.example.go4lunch.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

// le like ne fonctionne pas sur l'emulateur, mais uniquement sur mon telephone et même le design bug
// ça reste sur Star on clicked
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
        configureClickListeners();
    }

    /**
     * Configure les écouteurs de clic pour callButton et restaurantWebsiteButton.
     */
    private void configureClickListeners() {
        ImageButton callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCallButton();
            }
        });

        ImageButton websiteButton = findViewById(R.id.restaurantWebsiteButton);
        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickWebsiteButton();
            }
        });
    }

    /**
     * Méthode pour gérer le clic sur le bouton d'appel.
     */
    private void onClickCallButton() {
        // mon modèle restaurant n'a pas de variable pour le telephoner

        /*
        String phoneNumber = restaurant.getPhoneNumber();

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } else {
            Toast.makeText(this, "No phone number available", Toast.LENGTH_SHORT).show();
        }

         */
    }


    /**
     * Méthode pour gérer le clic sur le bouton du site Web du restaurant.
     */
    private void onClickWebsiteButton() {
        String websiteUrl = restaurant.getWebsite();

        if (websiteUrl != null && !websiteUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
            startActivity(intent);
        } else {
            Toast.makeText(this, "No website URL available", Toast.LENGTH_SHORT).show();
        }
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
        ImageView restaurantStar = findViewById(R.id.restaurantStar);
        ImageButton likeButton = findViewById(R.id.likeButton);

        // j'observe l'état initial pour vérifier si le restaurant est aimé
        viewModel.checkIfWorkmateLikeThisRestaurant(restaurant).observe(this, isLiked -> {
            if (isLiked) {
                restaurantStar.setImageResource(R.drawable.ic_star_on);
                Log.d("ConfigureLike_1", "Étoile activée");
            } else {
                restaurantStar.setImageResource(R.drawable.ic_star_off);
                Log.d("ConfigureLike_2", "Étoile désactivée");
            }
        });

        // j'applique les modification grace au viewModel et je change l'apparence
        likeButton.setOnClickListener(view -> {
            viewModel.checkIfWorkmateLikeThisRestaurant(restaurant).observe(this, isLiked -> {
                if (isLiked) {
                    restaurantStar.setImageResource(R.drawable.ic_star_off);
                    viewModel.deleteLikedRestaurant(restaurant);
                    Log.d("ConfigureLike_3", "Étoile désactivée cliquée");
                } else {
                    restaurantStar.setImageResource(R.drawable.ic_star_on);
                    viewModel.addLikedRestaurant(restaurant);
                    Log.d("ConfigureLike_4", "Étoile activée cliquée");
                }
                viewModel.checkIfWorkmateLikeThisRestaurant(restaurant).removeObservers(this);
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
