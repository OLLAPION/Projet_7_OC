package com.example.go4lunch.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;


public class ListWorkmatesLunchWithYouFragment extends Fragment {

    public ListWorkmatesLunchWithYouFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_workmates_lunch_with_you, container, false);
    }
}