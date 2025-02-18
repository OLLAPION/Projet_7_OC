package com.example.go4lunch.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.WorkmateRepository;
import com.example.go4lunch.view.DetailRestaurantViewModel;
import com.example.go4lunch.view.adapter.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * DetailRestaurantActivity
 * This activity displays detailed information about a selected restaurant,
 * including its name, address, type, and photo. It also allows users to:
 * - Call the restaurant
 * - Visit the restaurant's website
 * - Like or unlike the restaurant
 * - Mark the restaurant as their lunch choice
 * - View a list of workmates who have chosen the restaurant for lunch
 *
 * It uses ViewModel to handle data operations and observes LiveData for changes.
 */
public class DetailRestaurantActivity extends AppCompatActivity {

    /** RecyclerView to display the list of workmates who have chosen this restaurant */
    RecyclerView recyclerView = null;

    /** Adapter for managing the list of workmates */
    UserAdapter adapter = null;

    /** Selected restaurant object containing details */
    Restaurant restaurant = null;

    /** ViewModel for handling business logic and data operations */
    private DetailRestaurantViewModel viewModel;

    /** Tag used for logging */
    private String TAG = "DRA";

    /**
     * Called when the activity is created.
     * Initializes the activity, retrieves the restaurant from the intent,
     * and configures various UI components and functionalities.
     *
     * @param savedInstanceState Saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(DetailRestaurantViewModel.class);

        // Get restaurant from Intent
        Intent intent = getIntent();
        if (intent != null) {
            restaurant = (Restaurant) intent.getSerializableExtra("restaurant");
        }

        // Configure UI components and functionalities
        configureComponante();
        configureRecyclerView();
        configureLike();
        configureRestaurantChoice();
        configureFab();
        configureClickListeners();
    }

    /**
     * Configures click listeners for callButton and websiteButton.
     * - Call button: Initiates a phone call to the restaurant (if phone number is available)
     * - Website button: Opens the restaurant's website in a browser (if URL is available)
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
     * Handles the click event for the call button.
     * Initiates a phone call using an implicit intent if the restaurant's phone number is available.
     */
    private void onClickCallButton() {
        // Uncomment and implement if phone number is available in Restaurant model
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
     * Handles the click event for the website button.
     * Opens the restaurant's website in a browser if the URL is available.
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
     * Configures the FloatingActionButton to allow users to mark or unmark
     * the restaurant as their lunch choice.
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
        User currentUser = getCurrentUser();

        if (currentUser != null) {

            viewModel.checkIfWorkmateChoseThisRestaurantForLunch(restaurant, currentUser.getId())
                    .observe(this, isChosen -> {
                        if (isChosen) {
                            Log.d(TAG, "Details: Restaurant ID = " + restaurant.getId() + ", User ID = " + currentUser.getId());
                            viewModel.deleteLunch(restaurant, currentUser.getId(), () -> {
                                //viewModel.cleanUpLunch(restaurant, currentUser.getId());
                                configureRecyclerView();
                                configureRestaurantChoice();
                            });
                        } else {
                            Log.d(TAG, "Details: Restaurant ID = " + restaurant.getId() + ", User ID = " + currentUser.getId());
                            viewModel.createLunch(restaurant, currentUser, () -> {
                                configureRecyclerView();
                                configureRestaurantChoice();
                            });
                        }
                    });
        } else {
            Log.e(TAG, "No authenticated user found!");
        }
    }

    /**
     * Configures the appearance of the FloatingActionButton based on the user's lunch choice.
     * Displays different icons depending on whether the user chose the restaurant or not.
     */
    private void configureRestaurantChoice() {

        User currentUser = getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "check fzb for user: " + currentUser.getId());

            viewModel.checkIfWorkmateChoseThisRestaurantForLunch(restaurant, currentUser.getId()).observe(this, isChosen -> {
                FloatingActionButton fab = findViewById(R.id.fabWorkmateWantToEat);
                Log.d(TAG, "check fzb : " + isChosen);
                if (isChosen) {
                    fab.setImageResource(R.drawable.ic_chosen);
                } else {
                    fab.setImageResource(R.drawable.ic_not_chosen);
                }
            });
        } else {
            Log.e(TAG, "No authenticated user found!");
        }
    }

    /**
     * Retrieves the currently authenticated user from Firebase.
     *
     * @return User object containing user details or null if not authenticated
     */
    private User getCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            User user = new User();
            user.setId(currentUser.getUid());
            user.setName(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Anonymous");
            user.setAvatar(currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "");
            return user;
        }
        return null;
    }




    /**
     * Configures the like button functionality.
     * - Displays a filled star if the user likes the restaurant
     * - Displays an empty star if the user does not like the restaurant
     * - Updates Firebase when the like status changes
     */
    private void configureLike() {
        ImageView restaurantStar = findViewById(R.id.restaurantStar);
        ImageButton likeButton = findViewById(R.id.likeButton);

        // observe the initial state to check if the restaurant is liked
        viewModel.checkIfWorkmateLikeThisRestaurant(restaurant).observe(this, isLiked -> {
            if (isLiked) {
                restaurantStar.setImageResource(R.drawable.ic_star_on);
                Log.d(TAG, "Star On");
            } else {
                restaurantStar.setImageResource(R.drawable.ic_star_off);
                Log.d(TAG, "Star Off");
            }
        });

        // apply the modifications using the viewModel and I change the appearance
        likeButton.setOnClickListener(view -> {
            viewModel.checkIfWorkmateLikeThisRestaurant(restaurant).observe(this, isLiked -> {
                if (isLiked) {
                    restaurantStar.setImageResource(R.drawable.ic_star_off);
                    viewModel.deleteLikedRestaurant(restaurant);
                    Log.d(TAG, "Étoile désactivée cliquée");
                } else {
                    restaurantStar.setImageResource(R.drawable.ic_star_on);
                    viewModel.addLikedRestaurant(restaurant);
                    Log.d(TAG, "Étoile activée cliquée");
                }
                viewModel.checkIfWorkmateLikeThisRestaurant(restaurant).removeObservers(this);
            });
        });

    }


    /**
     * Configures the RecyclerView to display the list of workmates who have already chosen this restaurant for lunch.
     */
    private void configureRecyclerView(){

        // Initialize the adapter
        adapter = new UserAdapter();

        // Set up the layout manager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Attach the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        // Observe the list of workmates who chose this restaurant for lunch
        viewModel.getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(restaurant)
                .observe(this, workmates -> {
                    // Check if the list is not null
                    if (workmates != null) {
                        Log.d(TAG, "Number of users retrieved: " + workmates.size());

                        // Log each workmate's name for debugging purposes
                        for (User workmate : workmates) {
                            Log.d(TAG, "Workmate name: " + workmate.getName());
                        }
                        // Update the adapter with the list of workmates
                        adapter.updateList(workmates);
                    } else {
                        // If no workmates are found, update the adapter with an empty list
                        Log.d(TAG, "No workmates found.");
                        adapter.updateList(new ArrayList<>());
                    }
                });
    }

    /**
     * Initializes the visual components of the activity with the restaurant details.
     */
    private void configureComponante(){

        // Initialize RecyclerView for displaying workmates
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize TextViews for restaurant name, address, and type
        TextView restaurantNameTextView = findViewById(R.id.restaurantNameTextView);
        TextView restaurantAddressTextView = findViewById(R.id.restaurantAddressTextView);
        TextView restaurantTypeOfRestaurantTextView = findViewById(R.id.restaurantTypeOfRestaurantTextView);

        // Initialize ImageViews for restaurant photo, call button, and website button
        ImageView restaurantPhotoImageView = findViewById(R.id.restaurantPhotoImageView);
        ImageView restaurantCallImageView = findViewById(R.id.callButton);
        ImageButton restaurantWebsiteImageButton = findViewById(R.id.restaurantWebsiteButton);

        // Set the restaurant name, address, and type in the corresponding TextViews
        restaurantNameTextView.setText(restaurant.getName());
        restaurantAddressTextView.setText(restaurant.getAddress());
        restaurantTypeOfRestaurantTextView.setText(restaurant.getTypeOfRestaurant());

        Log.d(TAG, "Restaurant Details: " + restaurant);
        Log.d(TAG, "Avant Photo URL" + restaurant.getPhoto());

        // Construct the URL for the restaurant's photo using the Google Places API
        String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                + restaurant.getPhoto()
                + "&key=" + BuildConfig.google_maps_api;

        // Load the restaurant's photo using Glide
        Glide.with(this)
                .load(photoUrl)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error))
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Failed to load image", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Image loaded successfully");
                        return false;
                    }
                })
                .into(restaurantPhotoImageView);

        Log.d(TAG, "Après Photo URL" + restaurant.getPhoto());

        // Load the call icon using Glide
        Glide.with(this)
                .load(R.drawable.ic_call)
                .into(restaurantCallImageView);

        // Load the website icon using Glide
        Glide.with(this)
                .load(R.drawable.ic_website)
                .into(restaurantWebsiteImageButton);
    }



    /**
     * Used to navigate to this activity
     * @param activity the activity that calls this method
     */
    public static void navigate(Context activity, Restaurant restaurant) {
        // Create an intent to start the DetailRestaurantActivity
        Intent anIntent = new Intent(activity, DetailRestaurantActivity.class);

        // Add the restaurant object as a Serializable extra to the intent
        anIntent.putExtra("restaurant", (Serializable) restaurant);

        // Start the activity with the intent
        ActivityCompat.startActivity(activity, anIntent, null);
    }
}
