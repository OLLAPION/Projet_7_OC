package com.example.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.go4lunch.R;
import com.example.go4lunch.ui.fragment.ListRestaurantFragment;
import com.example.go4lunch.ui.fragment.ListWorkmatesLunchWithYouFragment;
import com.example.go4lunch.ui.fragment.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

        // Récupérer la barre de navigation en bas
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Ajouter un listener pour les clics sur les éléments de la barre de navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.menu_item_MapFragment:
                    selectedFragment = new MapFragment();
                    break;
                case R.id.menu_item_ListRestaurantFragment:
                    selectedFragment = new ListRestaurantFragment();
                    break;
                case R.id.menu_item_ListWorkmatesLunchWithYouFragment:
                    selectedFragment = new ListWorkmatesLunchWithYouFragment();
                    break;
            }
            // Remplacer le fragment actuellement affiché par le fragment sélectionné
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        });
    }
}