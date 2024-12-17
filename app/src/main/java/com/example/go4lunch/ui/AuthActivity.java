package com.example.go4lunch.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.go4lunch.R;
import com.example.go4lunch.repository.WorkmateRepository;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;
import java.util.List;

public class AuthActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create providers list
        List<AuthUI.IdpConfig> providers =
                Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.EmailBuilder().build()
                );


        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.AuthTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_go4lunch_logo)
                        .build(),
                REQUEST_CODE_SIGN_IN);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SIGN_IN) {
            this.onSignIn(requestCode, resultCode, data);
        }
    }

    private void onSignIn(int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if(resultCode == RESULT_OK){


            LiveData<Boolean> isNotificationActiveLiveData = WorkmateRepository.getInstance().getIsNotificationActive();
            isNotificationActiveLiveData.observe(AuthActivity.this, isNotificationActive -> {
                WorkmateRepository.getInstance().createOrUpdateWorkmate(isNotificationActive);
            });


            // Start CoreActivity
            Intent intent = new Intent(getApplicationContext(), CoreActivity.class);
            startActivity(intent);
        }else {
            if(response == null){
                //ErrorCodes.NO_NETWORK
                Log.e("ERROR","Error code : "+ response.getError().getErrorCode());
            }
        }
    }


}