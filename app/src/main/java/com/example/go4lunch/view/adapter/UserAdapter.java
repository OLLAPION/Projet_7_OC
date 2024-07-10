package com.example.go4lunch.view.adapter;
// UserAdapter.java
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter() {
        this.userList = new ArrayList<>();
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.avatarPhotoView.setText(user.getAvatar());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // utiliser DiffUtil pour gagner en perfomance et éviter de regenerer l'affichage d'element qui n'ont pas changé
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<User> list){
        this.userList = list;
        notifyDataSetChanged();
        Log.d("AdapterDebug", "User list updated. New size: " + userList.size());
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView avatarPhotoView;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            avatarPhotoView = itemView.findViewById(R.id.avatarPhotoView);
        }

        public void bind(User user) {
            nameTextView.setText(user.getName());
            avatarPhotoView.setText(user.getAvatar());
        }
    }
}
