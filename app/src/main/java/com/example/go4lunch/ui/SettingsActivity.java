package com.example.go4lunch.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.R;
import com.example.go4lunch.broadcast.MyBroadcastReceiver;
import com.example.go4lunch.view.SettingsViewModel;

/**
 * Activity for managing user settings related to notifications.
 * Provides a UI where the user can enable or disable daily notifications.
 */
public class SettingsActivity extends AppCompatActivity {

    // ViewModel to manage the state of notifications
    private SettingsViewModel viewModel;
    // Tag for logs
    private final String TAG = "SA";

    /**
     * Called when the activity is created.
     * This method initializes the UI components, sets up the ViewModel, and observes the state of notifications.
     * It also handles user interaction with the notification switch.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get the switch widget for enabling/disabling notifications
        Switch switchNotifications = findViewById(R.id.switch_notifications);

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Observe the current state of notifications from the ViewModel
        viewModel.getIsNotificationActive().observe(this, isActive -> {
            if (isActive != null) {
                switchNotifications.setChecked(isActive); // Update the switch state based on the data
            } else {
                switchNotifications.setChecked(false); // Default to off if no data is available
            }
        });

        // Handle user interaction with the switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setNotificationActive(isChecked); // Update the ViewModel with the new notification state
            if (isChecked) {
                configureAlarm(); // If enabled, set up the alarm for notifications
            } else {
                cancelAlarm(); // If disabled, cancel the alarm
            }
        });
    }

    /**
     * Configures an alarm to send notifications to the user every day.
     * This method sets up a repeating alarm to trigger at a specified time.
     */
    private void configureAlarm() {
        Log.d(TAG, "Configurer l'alarme pour les notifications");

        // Create an Intent that will be used by the BroadcastReceiver to send notifications
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        // Create a PendingIntent that will trigger the BroadcastReceiver at the scheduled time
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Get the AlarmManager system service to manage the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            // Set the alarm to trigger every day starting tomorrow
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, // Alarm triggers the next day
                    AlarmManager.INTERVAL_DAY, // Interval between each alarm (1 day)
                    pendingIntent
            );
        }
    }

    /**
     * Cancels the alarm for notifications when the user disables notifications.
     * This method ensures no further notifications will be sent once the user turns off the switch.
     */
    private void cancelAlarm() {
        Log.d(TAG, "Annuler l'alarme pour les notifications");

        // Create an Intent and PendingIntent similar to the ones used for configuring the alarm
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Get the AlarmManager system service to cancel the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            // Cancel the alarm using the same PendingIntent
            alarmManager.cancel(pendingIntent);
        }
    }
}
