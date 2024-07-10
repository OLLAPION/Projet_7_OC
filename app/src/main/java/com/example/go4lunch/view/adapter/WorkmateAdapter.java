package com.example.go4lunch.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateAdapter.WorkmateViewHolder> {

    private final List<User> workmates = new ArrayList<>();

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workmate, parent, false);
        return new WorkmateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
        User workmate = workmates.get(position);
        holder.bind(workmate);
    }

    @Override
    public int getItemCount() {
        return workmates.size();
    }

    public void setWorkmates(List<User> workmates) {
        this.workmates.clear();
        this.workmates.addAll(workmates);
        notifyDataSetChanged();
    }

    static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        private final ImageView avatar;
        private final TextView name;

        WorkmateViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.workmate_avatar);
            name = itemView.findViewById(R.id.workmate_name);
        }

        void bind(User workmate) {
            name.setText(workmate.getName());
            Glide.with(itemView.getContext())
                    .load(workmate.getAvatar())
                    .placeholder(R.drawable.ic_list_workmate_avatar)
                    .into(avatar);
        }
    }
}