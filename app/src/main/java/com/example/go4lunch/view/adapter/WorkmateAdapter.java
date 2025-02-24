package com.example.go4lunch.view.adapter;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter with the User's and the Restaurant's for the recyclerView that ListWorkmatesLunchWithYouFragment.
 */
public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateAdapter.WorkmateViewHolder> {

    private List<Pair<User, Restaurant>> workmatesWithRestaurants;

    public WorkmateAdapter(List<Pair<User, Restaurant>> workmatesWithRestaurants) {
        this.workmatesWithRestaurants = workmatesWithRestaurants;
    }

    public void setWorkmatesWithRestaurants(List<Pair<User, Restaurant>> workmatesWithRestaurants) {
        this.workmatesWithRestaurants = workmatesWithRestaurants;
        //this.workmatesWithRestaurants = workmatesWithRestaurants != null ? workmatesWithRestaurants : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workmate, parent, false);
        return new WorkmateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
        Pair<User, Restaurant> item = workmatesWithRestaurants.get(position);
        User user = item.first;
        Restaurant restaurant = item.second;

        holder.bind(user, restaurant);
    }

    @Override
    public int getItemCount() {
        return workmatesWithRestaurants.size();
    }


    static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        private final ImageView avatar;
        private final TextView nameWorkmate;
        private final TextView nameRestaurant;

        WorkmateViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.workmate_avatar);
            nameWorkmate = itemView.findViewById(R.id.workmate_name);
            nameRestaurant = itemView.findViewById(R.id.restaurant_name);
        }

        void bind(User workmate, Restaurant restaurant) {
            nameWorkmate.setText(workmate.getName());

            if (restaurant != null) {
                nameRestaurant.setText(restaurant.getName());
            } else {
                nameRestaurant.setText(itemView.getContext().getString(R.string.no_restaurant_chosen));
            }

            if (workmate.getAvatar() != null) {
                Glide.with(itemView.getContext())
                        .load(workmate.getAvatar())
                        .placeholder(R.drawable.ic_list_workmate_avatar)
                        .error(R.drawable.ic_user_avatar)
                        .circleCrop()
                        .into(avatar);
            } else {
                avatar.setImageResource(R.drawable.ic_user_avatar);
            }

        }
    }
}