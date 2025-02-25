package com.example.go4lunch.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.model.bo.Lunch;
import com.example.go4lunch.model.bo.Restaurant;
import com.example.go4lunch.model.bo.User;
import com.example.go4lunch.view.adapter.WorkmateAdapter;
import com.example.go4lunch.viewmodel.ListWorkmatesLunchWithYouViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays the list of workmates and their lunch choices.
 */
public class ListWorkmatesLunchWithYouFragment extends Fragment {

    private ListWorkmatesLunchWithYouViewModel viewModel;
    private RecyclerView recyclerView;
    private WorkmateAdapter adapter;
    private final String TAG = "LWLWYF";

    /**
     * Default constructor.
     */
    public ListWorkmatesLunchWithYouFragment() {
    }

    /**
     * Called to create the view for this fragment.
     * Initializes the RecyclerView and ViewModel, and observes data changes.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_workmates_lunch_with_you, container, false);
        Log.d(TAG, "onCreatedView Start");

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_workmates_lunch_with_you);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up adapter with an empty list initially
        adapter = new WorkmateAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ListWorkmatesLunchWithYouViewModel.class);

        // Observe workmates and their lunch choices
        observeWorkmatesAndLunches();

        return view;
    }

    /**
     * Observes changes in workmates and lunch data to update the UI accordingly.
     */
    private void observeWorkmatesAndLunches() {
        viewModel.getAllWorkmates().observe(getViewLifecycleOwner(), users -> {
            Log.d(TAG, "Number of workmates: " + users.size());

            viewModel.getTodayLunches().observe(getViewLifecycleOwner(), lunches -> {
                Log.d(TAG, "Number of lunches: " + lunches.size());

                // List to track workmates who have chosen a restaurant
                List<String> idWorkmatesThatHaveLunch = new ArrayList<>();
                for (Lunch lunch : lunches) {
                    if (!idWorkmatesThatHaveLunch.contains(lunch.getUser().getId())) {
                        idWorkmatesThatHaveLunch.add(lunch.getUser().getId());
                    }
                }

                // List of workmates with their selected restaurant
                List<Pair<User, Restaurant>> workmatesWithRestaurants = new ArrayList<>();
                for (Lunch lunch : lunches) {
                    workmatesWithRestaurants.add(new Pair<>(lunch.getUser(), lunch.getRestaurant()));
                }

                // Add workmates who have not selected a restaurant
                for (User user : users) {
                    if (!idWorkmatesThatHaveLunch.contains(user.getId())) {
                        workmatesWithRestaurants.add(new Pair<>(user, null));
                    }
                }

                // Update the adapter with the new list
                adapter.setWorkmatesWithRestaurants(workmatesWithRestaurants);
            });
        });
    }

    /**
     * Called after the view has been created. Used for additional setup if needed.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated Start");
    }
}
