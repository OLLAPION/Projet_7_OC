package com.example.go4lunch.ui;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.broadcast.MyBroadcastReceiver;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.ui.fragment.ListRestaurantFragment;
import com.example.go4lunch.ui.fragment.ListWorkmatesLunchWithYouFragment;
import com.example.go4lunch.ui.fragment.MapFragment;
import com.example.go4lunch.view.LocationViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Configurez l'en-tête du Drawer
        configureDrawerHeader();

        // Sortir ça d'ici - Setup BottomNavigationView
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

    private void configureDrawerHeader() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

        TextView textUserName = headerView.findViewById(R.id.TextUserName);
        TextView textUserMail = headerView.findViewById(R.id.TextUserMail);
        ImageView imageUserAvatar = headerView.findViewById(R.id.imageUserAvatar);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            String userName = firebaseAuth.getCurrentUser().getDisplayName();
            String userMail = firebaseAuth.getCurrentUser().getEmail();
            Uri photoUrl = firebaseAuth.getCurrentUser().getPhotoUrl();

            textUserName.setText(userName != null ? userName : "Nom indisponible");
            textUserMail.setText(userMail != null ? userMail : "Email indisponible");

            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.ic_user_avatar)
                        .error(R.drawable.ic_user_avatar)
                        .circleCrop()
                        .into(imageUserAvatar);
            } else {
                imageUserAvatar.setImageResource(R.drawable.ic_user_avatar);
            }
        }
    }



    private void configureAlarm() {
        Log.d("CA_Config_Alarm", "configureAlarm: Starting alarm configuration");

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

        Log.d("CA_Config_Alarm", "configureAlarm: Alarm set for " + calendar.getTime());

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
        Log.d("CA_Config_Alarm", "configureAlarm: Alarm configured successfully");

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


    private void openYourLunchProfile() {
        // Récupérer l'ID de l'utilisateur courant
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;

        if (userId != null) {
            // Accéder à LunchRepository pour récupérer le déjeuner de l'utilisateur
            LunchRepository lunchRepository = LunchRepository.getInstance(this);

            // Récupérer le déjeuner d'aujourd'hui pour l'utilisateur
            lunchRepository.getTodayLunch(userId).observe(this, lunch -> {
                if (lunch != null && lunch.getRestaurant() != null) {
                    // Si un restaurant est trouvé, rediriger vers DetailRestaurantActivity
                    Intent intent = new Intent(this, DetailRestaurantActivity.class);
                    intent.putExtra("restaurantId", lunch.getRestaurant().getId());
                    startActivity(intent);
                } else {
                    // Si aucun restaurant n'est choisi, afficher un message
                    showNoRestaurantMessage();
                }
            });
        } else {
            // Si l'utilisateur n'est pas authentifié
            Log.e("CoreActivity", "Utilisateur non authentifié");
            showNoRestaurantMessage();
        }
    }

    private void showNoRestaurantMessage() {
        Toast.makeText(this, "Vous n'avez pas choisi de restaurant pour le moment", Toast.LENGTH_LONG).show();
    }



    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }



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