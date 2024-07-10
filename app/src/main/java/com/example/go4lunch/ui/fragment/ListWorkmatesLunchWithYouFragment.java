package com.example.go4lunch.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.view.adapter.WorkmateAdapter;
import com.example.go4lunch.view.ListWorkmatesLunchWithYouViewModel;
import com.example.go4lunch.repository.LunchRepository;

public class ListWorkmatesLunchWithYouFragment extends Fragment {

    private ListWorkmatesLunchWithYouViewModel viewModel;
    private WorkmateAdapter adapter;
    private Restaurant selectedRestaurant;

    public ListWorkmatesLunchWithYouFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_workmates_lunch_with_you, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_workmates_lunch_with_you);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorkmateAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                // factory personnalisé je passe en argument mon LunchRepository à mon ViewModel
                // C'est bien ou pas vu que c'est surligné ?
                return (T) new ListWorkmatesLunchWithYouViewModel(LunchRepository.getInstance(getContext()));
            }
        }).get(ListWorkmatesLunchWithYouViewModel.class);

        //viewModel = new ViewModelProvider(this).get(ListWorkmatesLunchWithYouViewModel.class);

        observeViewModel();
        fetchWorkmatesForTodayLunch();
    }

    private void observeViewModel() {
        viewModel.getWorkmatesLiveData().observe(getViewLifecycleOwner(), workmates -> {
            if (workmates != null) {
                adapter.setWorkmates(workmates);
            }
        });
    }

    private void fetchWorkmatesForTodayLunch() {
        if (selectedRestaurant != null) {
            viewModel.fetchWorkmatesForTodayLunch(selectedRestaurant);
        }
    }
}