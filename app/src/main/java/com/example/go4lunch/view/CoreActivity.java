package com.example.go4lunch.view;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.view.broadcast.MyBroadcastReceiver;
import com.example.go4lunch.model.repository.LunchRepository;
import com.example.go4lunch.view.fragment.ListRestaurantFragment;
import com.example.go4lunch.view.fragment.ListWorkmatesLunchWithYouFragment;
import com.example.go4lunch.view.fragment.MapFragment;
import com.example.go4lunch.viewmodel.LocationViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

/**
 * CoreActivity is the main activity that manages the navigation and user interface
 * of the Go4Lunch application, including the navigation drawer, bottom navigation,
 * and notification alarm setup.
 */
public class CoreActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // DrawerLayout for navigation drawer
    private DrawerLayout drawerLayout;

    // ViewModel for location updates
    private LocationViewModel locationViewModel;

    // Tag for logging
    private final String TAG = "CA";

    // Alarm and notification constants
    public static final boolean ALARM_FIRED_IMMEDIATELY = false;

    public static String CHANNEL_ID;
    public static String CHANNEL_NAME;

    /**
     * Called when the activity is first created.
     * Sets up the Toolbar, DrawerLayout, BottomNavigationView, and notification alarm.
     *
     * @param savedInstanceState Saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

        // Initialisation avec getString()
        CHANNEL_ID = getString(R.string.notification_channel_id);
        CHANNEL_NAME = getString(R.string.notification_channel_name);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Toggle to open/close the Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configure the header of Drawer
        configureDrawerHeader();

        // Configure the BottomNavigationView
        configureBottomNavigationView();

        // Configure the daily notification alarm
        configureAlarm();
    }

    /**
     * Configures the header section of the navigation drawer,
     * displaying the user's name, email, and profile picture.
     */
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

            // Display user's name and email
            textUserName.setText(userName != null ? userName : getString(R.string.user_name_unavailable));
            textUserMail.setText(userMail != null ? userMail : getString(R.string.user_email_unavailable));

            // Display user's profile picture using Glide
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

    /**
     * Configures the BottomNavigationView to switch between different fragments:
     * - MapFragment
     * - ListRestaurantFragment
     * - ListWorkmatesLunchWithYouFragment
     */
    private void configureBottomNavigationView() {
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

        // Display the MapFragment by default
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            Fragment defaultFragment = new MapFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, defaultFragment)
                    .commit();
        }
    }

    /**
     * Configures a daily alarm to trigger a lunch reminder notification.
     * The alarm is set to repeat every day at 12:00 PM.
     */
    private void configureAlarm() {
        Log.d(TAG, "configureAlarm: Starting alarm configuration");

        // Create a notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "configureAlarm: Creating notification channel");

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, // id
                    CHANNEL_NAME, // name
                    NotificationManager.IMPORTANCE_HIGH // importance
            );

            // Register the channel with the system
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        // Get calendar instance for today's date
        Calendar calendar = Calendar.getInstance();

        if (!ALARM_FIRED_IMMEDIATELY) {
            // Set the alarm to start at 12:00 PM
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }

        Log.d(TAG, "configureAlarm: Alarm set for " + calendar.getTime());

        // Create an Intent to broadcast to the AlarmReceiver
        Intent intent = new Intent(this, MyBroadcastReceiver.class);

        // Create a PendingIntent to be triggered when the alarm goes off
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to repeat every day
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, // alarm type
                ALARM_FIRED_IMMEDIATELY ? System.currentTimeMillis() + 10000 : calendar.getTimeInMillis(), // else start 10 seconds later
                AlarmManager.INTERVAL_DAY, // interval
                pendingIntent // pending intent
        );
        Log.d(TAG, "configureAlarm: Alarm configured successfully");
    }

    /**
     * Handles navigation item selections from the navigation drawer.
     *
     * @param item The selected menu item
     * @return true if the item is handled, false otherwise
     */
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

    /**
     * Opens the user's lunch profile, showing the restaurant they chose for today.
     * If no restaurant is chosen, displays a message to the user.
     */
    private void openYourLunchProfile() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;

        if (userId != null) {
            LunchRepository lunchRepository = LunchRepository.getInstance(this);
            lunchRepository.getTodayLunch(userId).observe(this, lunch -> {
                if (lunch != null && lunch.getRestaurant() != null) {
                    Intent intent = new Intent(this, DetailRestaurantActivity.class);
                    intent.putExtra("restaurant", lunch.getRestaurant());
                    startActivity(intent);
                } else {
                    showNoRestaurantMessage();
                }
            });
        } else {
            Log.e(TAG, "Utilisateur non authentifié");
            showNoRestaurantMessage();
        }
    }



    private void showNoRestaurantMessage() {
        Toast.makeText(this, getString(R.string.no_restaurant_chosen), Toast.LENGTH_LONG).show();
    }



    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }



    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
                .revokeAccess()
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(this, AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (locationViewModel != null){
            locationViewModel.refresh();
        }
    }

}