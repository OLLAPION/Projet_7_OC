package com.example.go4lunch.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.go4lunch.R;
import com.example.go4lunch.model.repository.WorkmateRepository;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;
import java.util.List;


/**
 * AuthActivity handles the user authentication process using FirebaseUI.
 * It supports sign-in with Google, Email, and Twitter providers.
 */
public class AuthActivity extends AppCompatActivity {

    // Request code for sign-in activity
    private static final int REQUEST_CODE_SIGN_IN = 123;

    // Tag for logging
    private String TAG = "AA";

    /**
     * Called when the activity is first created.
     * Initializes the sign-in process with multiple authentication providers.
     *
     * @param savedInstanceState Saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a list of sign-in providers (Google, Email, Twitter)
        List<AuthUI.IdpConfig> providers =
                Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.EmailBuilder().build()
                );

        // Start the sign-in activity with customized UI options
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.AuthTheme)             // Set custom theme
                        .setAvailableProviders(providers)        // Set providers for authentication
                        .setIsSmartLockEnabled(false, true)      // Disable Smart Lock
                        .setLogo(R.drawable.logo_central)        // Set custom logo
                        .build(),
                REQUEST_CODE_SIGN_IN);
    }

    /**
     * Handles the result of the sign-in activity.
     *
     * @param requestCode The request code from startActivityForResult
     * @param resultCode  The result code returned from the sign-in activity
     * @param data        The Intent containing sign-in result data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from the sign-in activity
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            this.onSignIn(requestCode, resultCode, data);
        }
    }

    /**
     * Handles the sign-in result and updates the workmate's notification status.
     * If sign-in is successful, navigates to the CoreActivity.
     *
     * @param requestCode The request code passed to onActivityResult
     * @param resultCode  The result code returned from the sign-in activity
     * @param data        The Intent containing sign-in result data
     */
    private void onSignIn(int requestCode, int resultCode, Intent data) {
        // Get the sign-in response from the intent
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Check if the sign-in was successful
        if (resultCode == RESULT_OK) {

            // Observe the current user's notification status from the repository
            LiveData<Boolean> isNotificationActiveLiveData = WorkmateRepository.getInstance().getIsNotificationActive();
            isNotificationActiveLiveData.observe(AuthActivity.this, isNotificationActive -> {
                // Create or update the workmate's data with the current notification status
                WorkmateRepository.getInstance().createOrUpdateWorkmate(isNotificationActive);
            });

            // Navigate to the CoreActivity after successful sign-in
            Intent intent = new Intent(getApplicationContext(), CoreActivity.class);
            startActivity(intent);

        } else {
            // Sign-in failed or was cancelled
            if (response == null) {
                // Log the error code if available (e.g., no network)
                Log.e(TAG, "Error code : " + response.getError().getErrorCode());
            }
        }
    }
}
