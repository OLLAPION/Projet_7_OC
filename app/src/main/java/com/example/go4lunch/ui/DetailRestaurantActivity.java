package com.example.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetailRestaurantActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        TextView restaurantNameTextView = findViewById(R.id.restaurantNameTextView);
        TextView restaurantAddressTextView = findViewById(R.id.restaurantAddressTextView);
        TextView restaurantPhotoTextView = findViewById(R.id.restaurantPhotoTextView);
        TextView restaurantOpeningHoursTextView = findViewById(R.id.restaurantOpeningHoursTextView);
        TextView restaurantStarsTextView = findViewById(R.id.restaurantStarsTextView);
        TextView restaurantWebsiteTextView = findViewById(R.id.restaurantWebsiteTextView);
        TextView restaurantTypeOfRestaurantTextView = findViewById(R.id.restaurantTypeOfRestaurantTextView);


        Intent intent = getIntent();
        Restaurant restaurant = (Restaurant) intent.getSerializableExtra("restaurant");


        restaurantNameTextView.setText(restaurant.getName());
        restaurantAddressTextView.setText(restaurant.getAddress());
        restaurantPhotoTextView.setText(restaurant.getPhoto());
        restaurantOpeningHoursTextView.setText(restaurant.getOpeningHours());
        restaurantStarsTextView.setText(restaurant.getStars());
        restaurantWebsiteTextView.setText(restaurant.getWebsite());
        restaurantTypeOfRestaurantTextView.setText(restaurant.getTypeOfRestaurant());

        // Recycler View affiche une seul personne ...
        /*
        List<User> users = new ArrayList<>();
        users.add(new User(userName, userAvatar));

        UserAdapter adapter = new UserAdapter(users);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

         */


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // workmate veut manger
        });
    }

    /**
     * Used to navigate to this activity
     * @param activity the activity that calls this method
     */
    public static void navigate(MainActivity activity, Restaurant neighbour) {
        // create the intent
        Intent anIntent = new Intent(activity, DetailRestaurantActivity.class);
        // push the neighbour to the intent
        anIntent.putExtra("restaurant", (Serializable) neighbour);
        // start the activity
        ActivityCompat.startActivity(activity, anIntent, null);
    }
}
