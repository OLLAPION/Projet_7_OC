package com.example.go4lunch.ui;

import static com.example.go4lunch.BuildConfig.google_maps_api;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.location.GPSStatus;
import com.example.go4lunch.pojo.RestaurantsAnswer;
import com.example.go4lunch.pojo.Result;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.WorkmateRepository;
import com.example.go4lunch.services.RetrofitService;
import com.example.go4lunch.view.LocationViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

import android.widget.TextView;


import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnGetRestaurants;
    private Button btnGetDetailRestaurant;
    private Button btnGetGPSPosition;
    private Button btnGoCoreActivity;
    private RestaurantRepository restaurantRepository;
    private WorkmateRepository workmateRepository;
    private static final int RC_SIGN_IN = 123;
    private LocationViewModel locationViewModel;
    private TextView textViewLatitude;
    private TextView textViewLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        btnGetRestaurants = findViewById(R.id.btnGetRestaurants);
        btnGetDetailRestaurant = findViewById(R.id.btnGetDetailRestaurant);
        btnGetGPSPosition = findViewById(R.id.btnGetGPSPosition);
        btnGoCoreActivity= findViewById(R.id.btnGoCoreActivity);

        restaurantRepository = new RestaurantRepository(RetrofitService.getRestaurantApi());
        workmateRepository = new WorkmateRepository();

        textViewLatitude = findViewById(R.id.textViewLatitude);
        textViewLongitude = findViewById(R.id.textViewLongitude);
        // faire une factory
        locationViewModel = new LocationViewModel(MainApplication.getApplication());


        btnGetRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // j'appelle ma fonction qui a ma petite requete pour recolter la liste des resto
                 //getRestaurants();
                // OK

                // je test la création d'un lunch
                //testCreateLunch();
                // OK

                // je test les méthodes du LunchRepository
                // testLunchRepositoryMethods();
                // NOK A faire méthode par méthode

                // Création d'un compte puis recuperation de l'id de ce compte
                //BUG lorsque j'ajoute la possibilité de me connecter avec GOOGLE
                startSignInActivity();
                //testGetWormateWithDelay();
                // OK

                // Va chercher l'id du user connecté
                //testGetWormate();
                // OK

                // après une deconnexion le testGetWormate() renvoi aucun User connecté !!!
                //signOutCurrentUser();
                // OK

                // va chercher les noms de tous les workmates
                //testGetAllWorkmates();
                // Va chercher que le workmate connécté

                // test les méthodes liées au 'Like'
                // ajoute une seule ligne de like restaurant
                //likeRestaurant();
                // le check ne fonctionne pas et donne que le dernier restaurant liké


                // notification activé
                //notificationOn();
                // OK

                // notification désactivé
                //notificationOff();
                //OK

                // test checkIfWorkmateChooseRestaurant
                //testCheckIfWorkmateChooseRestaurant();
            }
        });

        btnGetDetailRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detailRestaurant();

            }
        });


        btnGetGPSPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestPermissions();

                locationViewModel.getLocationLiveData().observe(MainActivity.this, new Observer<GPSStatus>() {
                    @Override
                    public void onChanged(GPSStatus location) {
                        updateLocationUI(location);
                    }
                });

            }
        });

        btnGoCoreActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CoreActivity.class);
                startActivity(intent);
            }
        });

    }


    private void testCheckIfWorkmateChooseRestaurant() {
        Restaurant restaurant = new Restaurant("R1",
                "Chez Ollapion",
                "1 rue de la jeunesse",
                "![](C:/Users/Ollapion/AppData/Local/Temp/kross.jpg)",
                "10h",
                "3 étoiles",
                "www.ollapion.com",
                "Restaurant_Café_Jeu");

        User currentUser_2 = new User();
        currentUser_2.setId("D7rZ2O9j8vVHuLwxHrgnrLTT3mv1");

        LunchRepository lunchRepository = LunchRepository.getInstance(MainApplication.getApplication());

        LiveData<Boolean> isChosenLiveData = lunchRepository.checkIfCurrentWorkmateChoseThisRestaurantForLunch(restaurant, currentUser_2.getId());

        isChosenLiveData.observe(this, isChosen -> {
            if (isChosen) {
                Log.d("Main_2024", "delete Lunch > restaurant : " + restaurant.getId() + " User : " + currentUser_2.getId());
            } else {
                Log.d("Main_2024", "create Lunch > restaurant : " + restaurant.getId() + " User : " + currentUser_2.getId());
            }
        });
    }

    private void detailRestaurant() {
        Restaurant restaurant_1 = new Restaurant("R1",
                "Chez Ollapion",
                "1 rue de la jeunesse",
                "![](C:/Users/Ollapion/AppData/Local/Temp/kross.jpg)",
                "10h",
                "3 étoiles",
                "www.ollapion.com",
                "Restaurant_Café_Jeu");
        DetailRestaurantActivity.navigate(this, restaurant_1);
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"
        },0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (locationViewModel != null){
         locationViewModel.refresh();
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateLocationUI(GPSStatus location) {
        if (location != null) {
            if(location.getLatitude() != null && location.getLongitude() != null){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                textViewLatitude.setText(String.valueOf(latitude));
                textViewLongitude.setText(String.valueOf(longitude));
            } else if (location.getQuerying()) {
                textViewLatitude.setText("querying");
                textViewLongitude.setText("querying");
            } else {
                textViewLatitude.setText("Permission incorrect");
                textViewLongitude.setText("Permission incorrect");
            }
        }
    }

    private void notificationOn() {
        workmateRepository.createOrUpdateWorkmate(true);
        Log.d("Active notification", "Notifications activated");

    }

    private void notificationOff() {
        workmateRepository.createOrUpdateWorkmate(false);
        Log.d("disabled notification", "Notifications disabled");

    }

    private void likeRestaurant() {

        Restaurant restaurant_1 = new Restaurant("R1",
                "Chez Ollapion",
                "1 rue de la jeunesse",
                "![](C:/Users/Ollapion/AppData/Local/Temp/kross.jpg)",
                "10h",
                "3 étoiles",
                "www.ollapion.com",
                "Restaurant_Café_Jeu");

        Restaurant restaurant_2 = new Restaurant("R2",
                "Chez Ben",
                "2 rue de la jeunesse",
                "![](C:/Users/Ollapion/AppData/Local/Temp/kross.jpg)",
                "10h",
                "1 étoiles",
                "www.chezBen.com",
                "Restaurant_Café_Jeu");


        /*
        workmateRepository.addLikedRestaurant(restaurant_1);
        Log.d("addLikedRestaurant", "Restaurant liked: " + restaurant_1.getName());

         */


        workmateRepository.addLikedRestaurant(restaurant_2);
        Log.d("addLikedRestaurant", "Restaurant liked: " + restaurant_2.getName());


/*
        workmateRepository.deleteLikedRestaurant(restaurant_1);
        Log.d("deleteLikedRestaurant", "Restaurant dislike: " + restaurant_1.getName());

 */




        workmateRepository.checkIfCurrentWorkmateLikeThisRestaurant(restaurant_1)
                .observe(this, isLiked -> {
                    if (isLiked != null) {
                        if (isLiked) {
                            Log.d("CheckIfIsLiked", "Yes it is (resto_1).");
                        } else {
                            Log.d("CheckIfIsLiked", "No isn't.(resto_1)");
                        }
                    } else {
                        Log.d("CheckIfIsLiked", "maybe have a problem.");
                    }
                });
        workmateRepository.checkIfCurrentWorkmateLikeThisRestaurant(restaurant_2)
                .observe(this, isLiked -> {
                    if (isLiked != null) {
                        if (isLiked) {
                            Log.d("CheckIfIsLiked", "Yes it is (resto_2).");
                        } else {
                            Log.d("CheckIfIsLiked", "No isn't.(resto_2)");
                        }
                    } else {
                        Log.d("CheckIfIsLiked", "maybe have a problem.");
                    }
                });
    }


    private void testGetAllWorkmates() {
        workmateRepository.getAllWorkmates().observe(this, workmates -> {
            if (workmates != null) {
                for (User workmate : workmates) {
                    Log.d("getAllWorkmates", "Workmate: " + workmate.getName());
                }
            } else {
                Log.d("getAllWorkmates", "No workmates found.");
            }
        });
    }


    private void startSignInActivity(){

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers =
                Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build());

        /*
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        */

        // Launch the activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        //.setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        //.setLogo(R.drawable.ic_logo_auth)
                        .build(),
                RC_SIGN_IN);
    }


    private void testCreateLunch() {
        Restaurant chosenRestaurant = new Restaurant(
                "R1",
                "Chez Ollapion",
                "1 rue de la jeunesse",
                "![](C:/Users/Ollapion/AppData/Local/Temp/kross.jpg)",
                "10h",
                "3 étoiles",
                "www.ollapion.com",
                "Restaurant_Café_Jeu"
        );

        User currentUser = new User(
                "U1",
                "Benjamin Pallo",
                "Benjamin.pallo@yahoo.fr",
                "![](C:/Users/Ollapion/AppData/Local/Temp/kross.jpg)"
        );

        //LunchRepository.getInstance(MainActivity.this).createLunch(chosenRestaurant, currentUser);
    }

    private void testLunchRepositoryMethods() {
        Restaurant chosenRestaurant = new Restaurant(
                "R1",
                "Chez Ollapion",
                "1 rue de la jeunesse",
                "![](C:/Users/Ollapion/AppData/Local/Temp/kross.jpg)",
                "10h",
                "3 étoiles",
                "www.ollapion.com",
                "Restaurant_Café_Jeu"
        );

        User currentUser = new User(
                "U1",
                "Benjamin Pallo",
                "Benjamin.pallo@yahoo.fr",
                "![](C:/Users/Ollapion/AppData/Local/Temp/kross.jpg)"
        );

        //LunchRepository.getInstance(MainActivity.this).createLunch(chosenRestaurant, currentUser);

        // Test de getTodayLunch
        LunchRepository.getInstance(MainActivity.this).getTodayLunch(currentUser.getId()).observe(this, lunch -> {
            if (lunch != null) {
                Log.e("MainActivity", "Today's Lunch: " + lunch.getRestaurant().getName());
            } else {
                Log.e("MainActivity", "No Lunch for today.");
            }
        });

        // Test de getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant
        LunchRepository.getInstance(MainActivity.this).getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(chosenRestaurant)
                .observe(this, workmates -> {
                    if (workmates != null && !workmates.isEmpty()) {
                        for (User workmate : workmates) {
                            Log.e("MainActivity", "Workmate who chose the restaurant: " + workmate.getName());
                        }
                    } else {
                        Log.e("MainActivity", "No workmates chose the restaurant for today's lunch.");
                    }
                });


        // Test de checkIfCurrentWorkmateChoseThisRestaurantForLunch
        LunchRepository.getInstance(MainActivity.this).checkIfCurrentWorkmateChoseThisRestaurantForLunch(chosenRestaurant, currentUser.getId())
                .observe(this, isChosen -> {
                    if (isChosen != null && isChosen) {
                        Log.e("MainActivity", "Current workmate chose this restaurant for lunch today.");
                    } else {
                        Log.e("MainActivity", "Current workmate did not choose this restaurant for lunch today.");
                    }
                });

        // Test de deleteLunch
        //LunchRepository.getInstance(MainActivity.this).deleteLunch(chosenRestaurant, currentUser.getId());
    }

// J'ai commenté les deux methodes ci dessous pour pouvoir utiliser un User fixe !
    /*
    private void testGetWormateWithDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                testGetWormate(); // Exécuter la méthode testGetWormate()
            }
        }, 30000); // Délai de 30 secondes en millisecondes (30000 ms)
    }

     */


    /*
    private void testGetWormate() {

        FirebaseUser firebaseUser = workmateRepository.getWorkmate();
        if (firebaseUser != null) {
            Log.d("getWorkmate", "Current User ID: " + firebaseUser.getUid());
            Log.d("getWorkmate", "Current User Name: " + firebaseUser.getDisplayName());
        } else {
            Log.e("getWorkmate", "User not logged in.");
        }

    }

     */

    private void signOutCurrentUser() {
        workmateRepository.signOut(MainActivity.this)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("signOut", "User signed out successfully");
                    } else {
                        Log.e("signOut", "Error signing out user", task.getException());
                    }
                });
    }

    private void getRestaurants() {
        // Les paramètres de la requête
        String location = "37.4219999,-122.0840575";
        int radius = 1000;
        String type = "restaurant";
        String key = google_maps_api;

        // J'appelle le repository pour recécupèrer les restaurants
        Call<RestaurantsAnswer> call = restaurantRepository.getAllRestaurants(location, radius, type, key);

        // Requête asynchrone
        call.enqueue(new Callback<RestaurantsAnswer>() {
            @Override
            public void onResponse(@NonNull Call<RestaurantsAnswer> call, @NonNull Response<RestaurantsAnswer> response) {
                if (response.isSuccessful()) {
                    RestaurantsAnswer result = response.body();
                    assert result != null;
                    for (Result r : result.getResults())
                    {
                        Log.e("MainActivity", r.getName());
                    }
                    // Si je veux faire quelque chose avec la liste des restaurants (result.getResults())
                } else {
                    Log.e("MainActivity", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RestaurantsAnswer> call, @NonNull Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
            }
        });
    }
}

