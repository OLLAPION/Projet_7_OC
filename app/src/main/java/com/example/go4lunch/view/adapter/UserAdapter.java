package com.example.go4lunch.view.adapter;
// UserAdapter.java
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.bo.User;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter with only the User's for the recyclerView that DetailRestaurantActivity.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    private static final String TAG = "UA";

    public UserAdapter() {
        this.userList = new ArrayList<>();
    }

    /**
     * Create and initialize a new ViewHolder for each user item.
     *
     * @param parent The parent ViewGroup in which the item view will be placed.
     * @param viewType The view type of the new item.
     * @return A new ViewHolder for a user item.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * Bind data to the ViewHolder for each user item.
     *
     * @param holder The ViewHolder to bind the data to.
     * @param position The position in the data list.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Get the user at the current position
        User user = userList.get(position);

        // Set the user's name in the TextView
        holder.nameTextView.setText(user.getName());

        // If the user has an avatar, load it using Glide, otherwise use a default image
        if (user.getAvatar() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getAvatar())
                    .placeholder(R.drawable.ic_list_workmate_avatar)
                    .error(R.drawable.ic_user_avatar)
                    .circleCrop()
                    .into(holder.avatarPhotoView);
        } else {
            holder.avatarPhotoView.setImageResource(R.drawable.ic_user_avatar);
        }
    }

    /**
     * Get the total number of items in the list.
     *
     * @return The size of the user list.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * Update the list of users and notify the adapter of the changes.
     *
     * @param list The new list of users.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<User> list){
        this.userList = list;
        notifyDataSetChanged();
        Log.d(TAG, "User list updated. New size: " + userList.size());
    }

    /**
     * ViewHolder for binding user data to the views.
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView avatarPhotoView;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            avatarPhotoView = itemView.findViewById(R.id.avatarPhotoView);
        }
    }
}
