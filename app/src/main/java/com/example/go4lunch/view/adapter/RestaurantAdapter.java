package com.example.go4lunch.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.ui.RestaurantItem;
import com.example.go4lunch.ui.DetailRestaurantActivity;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private List<RestaurantItem> restaurantItemList;
    private Context context;

    public RestaurantAdapter(List<RestaurantItem> restaurantItemList, Context context) {
        this.restaurantItemList = restaurantItemList;
        this.context = context;
    }

    public void updateData(List<RestaurantItem> newRestaurantItemList) {
        restaurantItemList = new ArrayList<>(newRestaurantItemList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        RestaurantItem restaurantItem = restaurantItemList.get(position);
        holder.bind(restaurantItem);
    }

    @Override
    public int getItemCount() {
        return restaurantItemList.size();
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView addressTextView;
        private final RatingBar ratingBar;
        private final ImageView photoImageView;
        private final TextView participantTextView;
        private final TextView distanceTextView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.restaurant_name);
            addressTextView = itemView.findViewById(R.id.restaurant_address);
            ratingBar = itemView.findViewById(R.id.restaurant_rating);
            photoImageView = itemView.findViewById(R.id.restaurant_photo);
            participantTextView = itemView.findViewById(R.id.restaurant_participants);
            distanceTextView = itemView.findViewById(R.id.restaurant_distance);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    RestaurantItem restaurantItem = restaurantItemList.get(position);

                    //test de convertir RestaurantItem en Restaurant
                    Restaurant restaurant = restaurantItem.getOrigin();

                    Intent intent = new Intent(itemView.getContext(), DetailRestaurantActivity.class);
                    intent.putExtra("restaurant", restaurant);
                    // passer un restaurant au lieu d'un restaurantItem > Pourquoi ???
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        // je peux faire distanceTextView et participanttextView comme nameTextView, address ...
        public void bind(RestaurantItem restaurantItem) {
            nameTextView.setText(restaurantItem.getName());
            addressTextView.setText(restaurantItem.getAddress());
            ratingBar.setRating((float) restaurantItem.getRating());
            Glide.with(photoImageView.getContext())
                    .load(restaurantItem.getPhotoUrl())
                    .into(photoImageView);
            participantTextView.setText(itemView.getContext().getString(R.string.participants_count, restaurantItem.getNbParticipant()));
            // DEbug
            double distance = restaurantItem.getDistance();
            Log.d("RestaurantAdapter", "Binding distance: " + distance);

            distanceTextView.setText(itemView.getContext().getString(R.string.distance_text, restaurantItem.getDistance()));
        }
    }
}
