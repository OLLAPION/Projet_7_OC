package com.example.go4lunch.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view.adapter.WorkmateAdapter;
import com.example.go4lunch.view.ListWorkmatesLunchWithYouViewModel;
import com.example.go4lunch.repository.LunchRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListWorkmatesLunchWithYouFragment extends Fragment {

    private ListWorkmatesLunchWithYouViewModel viewModel;
    private RecyclerView recyclerView;
    private WorkmateAdapter adapter;
    private final String TAG = "LWLWYF";


    public ListWorkmatesLunchWithYouFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_workmates_lunch_with_you, container, false);
        Log.d(TAG, "onCreatedView Start");

        recyclerView = view.findViewById(R.id.recycler_view_workmates_lunch_with_you);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new WorkmateAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ListWorkmatesLunchWithYouViewModel.class);


        // Récupération de tous les workmates et des lunchs
        // En faire une méthode à part
        viewModel.getAllWorkmates().observe(getViewLifecycleOwner(), users -> {
            Log.d(TAG, "Nombre de workmates : " + users.size());
            viewModel.getTodayLunches().observe(getViewLifecycleOwner(), lunches -> {
                Log.d(TAG, "Nombre de lunches : " + lunches.size());
                List<String> idWormatesThatHasLunch = new ArrayList<>();
                for (Lunch lunch : lunches) {
                    if (! idWormatesThatHasLunch.contains(lunch.getUser().getId())) {
                        idWormatesThatHasLunch.add(lunch.getUser().getId());
                    }
                }

                List<Pair<User, Restaurant>> workmatesWithRestaurants = new ArrayList<>();

                for (Lunch lunch : lunches) {
                    workmatesWithRestaurants.add(new Pair<>(lunch.getUser(), lunch.getRestaurant()));
                }

                for (User user : users) {
                    if (!idWormatesThatHasLunch.contains(user.getId())){
                        workmatesWithRestaurants.add(new Pair<>(user, null));
                    }

                }
                adapter.setWorkmatesWithRestaurants(workmatesWithRestaurants);
            });

        });

        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated Start");

    }
}
