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

public class SettingsActivity extends AppCompatActivity {

    private SettingsViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch switchNotifications = findViewById(R.id.switch_notifications);

        // Initialiser le ViewModel
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Observer l'état actuel des notifications
        viewModel.getIsNotificationActive().observe(this, isActive -> {
            if (isActive != null) {
                switchNotifications.setChecked(isActive);
            } else {
                switchNotifications.setChecked(false); // Par défaut, désactivé si aucune donnée
            }
        });

        // Gérer les changements de l'utilisateur
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setNotificationActive(isChecked); // Met à jour via le ViewModel
            if (isChecked) {
                configureAlarm();
            } else {
                cancelAlarm();
            }
        });
    }

    private void configureAlarm() {
        Log.d("SettingsActivity", "Configurer l'alarme pour les notifications");

        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, // règle l'alarme pour le lendemain
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

    private void cancelAlarm() {
        Log.d("SettingsActivity", "Annuler l'alarme pour les notifications");

        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
