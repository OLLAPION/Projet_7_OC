package com.example.go4lunch.ui.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view.adapter.WorkmateAdapter;
import com.example.go4lunch.view.ListWorkmatesLunchWithYouViewModel;
import com.example.go4lunch.repository.LunchRepository;

import java.util.ArrayList;
import java.util.List;

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

        viewModel.getAllWorkmates().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                // Aucune trace dans les logs > Pas déclanché
                Log.d(TAG, "Nombre de workmates : " + users.size());
                adapter.setWorkmates(users);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated Start");

    }
}
/*
public class ListWorkmatesLunchWithYouFragment extends Fragment {

    private ListWorkmatesLunchWithYouViewModel viewModel;
    private WorkmateAdapter adapter;
    private Restaurant selectedRestaurant;


    public ListWorkmatesLunchWithYouFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_workmates_lunch_with_you, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_workmates_lunch_with_you);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new WorkmateAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialisation du ViewModel avec un ViewModelProvider.Factory pour passer le LunchRepository
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ListWorkmatesLunchWithYouViewModel(LunchRepository.getInstance(getContext()));
            }
        }).get(ListWorkmatesLunchWithYouViewModel.class);

        // viewModel.getAllLunchToDay -> avec lambda pour le resultat
        // viewModel.getAllWormates
        // faire un WorkmatesItem pour l'adapter

        // Chargement des workmates si un restaurant est sélectionné
        if (selectedRestaurant != null) {
            viewModel.fetchWorkmatesForTodayLunch(selectedRestaurant);
        }

        observeViewModel();

        return view;
    }

    private void observeViewModel() {
        viewModel.getWorkmatesLiveData().observe(getViewLifecycleOwner(), workmates -> {
            if (workmates != null) {
                adapter.setWorkmates(workmates);
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Permet de définir le restaurant sélectionné à partir d'une autre partie de l'application
    public void setSelectedRestaurant(Restaurant restaurant) {
        this.selectedRestaurant = restaurant;
        if (viewModel != null && selectedRestaurant != null) {
            viewModel.fetchWorkmatesForTodayLunch(selectedRestaurant);
        }
    }
}

 */
