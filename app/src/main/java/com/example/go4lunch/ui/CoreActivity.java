package com.example.go4lunch.ui;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.go4lunch.R;
import com.example.go4lunch.broadcast.MyBroadcastReceiver;
import com.example.go4lunch.ui.fragment.ListRestaurantFragment;
import com.example.go4lunch.ui.fragment.ListWorkmatesLunchWithYouFragment;
import com.example.go4lunch.ui.fragment.MapFragment;
import com.example.go4lunch.view.LocationViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class CoreActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private LocationViewModel locationViewModel;
    public static final boolean NOTIFICATION_DEBUG = true;
    public static final String CHANNEL_ID = "Go4Lunch";
    public static final String CHANNEL_NAME = "LunchAlarm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Toggle to open/close the Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setup BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        });

        configureAlarm();
    }


    private void configureAlarm() {

        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.d("CA_Config_Alarm", "configureAlarm: Creating notification channel");

            // Create the NotificationChannel
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, // id
                    CHANNEL_NAME, // name
                    NotificationManager.IMPORTANCE_HIGH // importance
            );

            // Register the channel with the system
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        // Get calendar instance to day
        Calendar calendar = Calendar.getInstance();

        if (!NOTIFICATION_DEBUG) {
            // Set the alarm to start at 12:00
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }


        // Create an Intent to broadcast to the AlarmReceiver
        Intent intent = new Intent(this, MyBroadcastReceiver.class);

        // Create a PendingIntent to be triggered when the alarm goes off
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, // context
                0, // no need to request code
                intent, // intent to be triggered
                PendingIntent.FLAG_IMMUTABLE // PendingIntent flag
        );

        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to repeat every day
        // Warning : the alarm is not exact, it can be delayed by the system up to few minutes
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, // alarm type
                NOTIFICATION_DEBUG ? System.currentTimeMillis() + 10000 : calendar.getTimeInMillis(), // time to start
                AlarmManager.INTERVAL_DAY, // interval
                pendingIntent // pending intent
        );

    }


        @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_your_lunch:
                openYourLunchProfile();
                break;
            case R.id.menu_item_settings:
                openSettings();
                break;
            case R.id.menu_item_logout:
                logoutUser();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Ouvrir le profil pour voir le repas
    private void openYourLunchProfile() {
        // faire une class qui recupére le restaurant de l'utilisateur et qui envoie sur DetailRestaurantActivity
        // Sinon, affiche un message "vous n'avez pas choisi de restaurant pour le moment"
        Intent intent = new Intent(this, DetailRestaurantActivity.class);
        startActivity(intent);
    }

    // Ouvrir les paramètres
    private void openSettings() {
        /*
        Une class qui permet d'activer ou désactiver les notifications
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

         */
    }

    // Déconnexion
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Efface la pile d'activités
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // pour la partie GPS
    @Override
    public void onResume(){
        super.onResume();
        if (locationViewModel != null){
            locationViewModel.refresh();
        }
    }

}