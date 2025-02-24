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

/**
 * Adapter class for binding restaurant data to a RecyclerView.
 * The adapter is responsible for inflating item views, binding data to each item,
 * and handling item clicks to show detailed information.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private List<RestaurantItem> restaurantItemList; // List to hold restaurant items
    private Context context; // Context to access resources and start new activities

    private static final String TAG = "RA";

    /**
     * Constructor for the adapter to initialize the restaurant list and context.
     */
    public RestaurantAdapter(List<RestaurantItem> restaurantItemList, Context context) {
        this.restaurantItemList = restaurantItemList;
        this.context = context;
    }

    /**
     * Updates the data in the adapter and notifies the RecyclerView to refresh.
     *
     * @param newRestaurantItemList the new list of restaurant items to display.
     */
    public void updateData(List<RestaurantItem> newRestaurantItemList) {
        restaurantItemList = new ArrayList<>(newRestaurantItemList);
        notifyDataSetChanged(); // Notify the adapter that data has changed
    }

    /**
     * Creates and returns a new ViewHolder instance for the restaurant items.
     *
     * @param parent   the parent view to attach the view holder to.
     * @param viewType the type of the view (used for multiple view types).
     * @return a new ViewHolder instance.
     */
    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     *
     * @param holder   the ViewHolder to bind data to.
     * @param position the position in the dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        RestaurantItem restaurantItem = restaurantItemList.get(position);
        holder.bind(restaurantItem); // Bind the data to the ViewHolder
    }

    /**
     * Returns the total number of items in the data set.
     *
     * @return the size of the restaurant item list.
     */
    @Override
    public int getItemCount() {
        return restaurantItemList.size();
    }

    /**
     * ViewHolder class that holds references to the views for each item in the RecyclerView.
     * It also handles item click interactions.
     */
    class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView addressTextView;
        private final RatingBar ratingBar;
        private final ImageView photoImageView;
        private final TextView participantTextView;
        private final TextView distanceTextView;

        /**
         * Constructor for the ViewHolder that initializes the views and sets the click listener.
         *
         * @param itemView the item view containing all the restaurant data views.
         */
        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views from the layout
            nameTextView = itemView.findViewById(R.id.restaurant_name);
            addressTextView = itemView.findViewById(R.id.restaurant_address);
            ratingBar = itemView.findViewById(R.id.restaurant_rating);
            photoImageView = itemView.findViewById(R.id.restaurant_photo);
            participantTextView = itemView.findViewById(R.id.restaurant_participants);
            distanceTextView = itemView.findViewById(R.id.restaurant_distance);

            // Set up an item click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    RestaurantItem restaurantItem = restaurantItemList.get(position);
                    Restaurant restaurant = restaurantItem.getOrigin(); // Get the Restaurant object

                    // Start a new activity to show the details of the clicked restaurant
                    Intent intent = new Intent(itemView.getContext(), DetailRestaurantActivity.class);
                    intent.putExtra("restaurant", restaurant);
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        /**
         * Binds the data of a RestaurantItem to the respective views.
         *
         * @param restaurantItem the restaurant item to bind to the views.
         */
        public void bind(RestaurantItem restaurantItem) {
            // Set the name if available
            if (restaurantItem.getName() != null) {
                nameTextView.setText(restaurantItem.getName());
            }

            // Set the address if available
            if (restaurantItem.getAddress() != null) {
                addressTextView.setText(restaurantItem.getAddress());
            }

            // Set the rating if available
            if (restaurantItem.getRating() != null) {
                ratingBar.setRating(restaurantItem.getRating().floatValue());
            }

            // Load the restaurant's photo using Glide if available
            if (restaurantItem.getPhotoUrl() != null) {
                Glide.with(photoImageView.getContext())
                        .load(restaurantItem.getPhotoUrl())
                        .into(photoImageView);
            }

            // Set the number of participants if available
            if (restaurantItem.getNbParticipant() != null) {
                participantTextView.setText(itemView.getContext().getString(R.string.participants_count, restaurantItem.getNbParticipant()));
            }

            // Set the distance if available
            if (restaurantItem.getDistance() != null) {
                Double distance = restaurantItem.getDistance();
                Log.d(TAG, "Binding distance: " + distance);

                // Set the distance text
                distanceTextView.setText(itemView.getContext().getString(R.string.distance_text, restaurantItem.getDistance()));
            }
        }
    }
}
