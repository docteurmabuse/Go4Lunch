package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tizzone.go4lunch.databinding.UsersListItemBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.utils.RestaurantHelper;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserViewHolder> {

    private Context context;
    private Restaurant restaurant;
    //FOR COMMUNICATION
    private UsersListItemBinding userBinding;
    private String currentUserId;
    private List<User> userList;

    public UsersListAdapter() {
        notifyDataSetChanged();
    }


//    @Override
//    public void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {
//        holder.userBinding.setWorkmates(user);
//        if (user.getLunchSpot() != null) {
//            RestaurantHelper.getRestaurantsById(user.getLunchSpot()).addOnSuccessListener(documentSnapshot -> {
//                restaurant = documentSnapshot.toObject(Restaurant.class);
//                holder.userBinding.setRestaurant(restaurant);
//            });
//            holder.itemView.setOnClickListener(view -> {
//                Intent intent = new Intent(context, PlaceDetailActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("RESTAURANT", restaurant);
//                intent.putExtras(bundle);
//                context.startActivity(intent);
//                });
//        }
//    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        userBinding = UsersListItemBinding.inflate(inflater, parent, false);
        return new UserViewHolder(userBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userBinding.setWorkmates(user);
        if (user.getLunchSpot() != null) {
            RestaurantHelper.getRestaurantsById(user.getLunchSpot()).addOnSuccessListener(documentSnapshot -> {
                restaurant = documentSnapshot.toObject(Restaurant.class);
                holder.userBinding.setRestaurant(restaurant);
            });
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, PlaceDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("RESTAURANT", restaurant);
                intent.putExtras(bundle);
                context.startActivity(intent);
            });
        }
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public UsersListItemBinding userBinding;

        public UserViewHolder(@NonNull UsersListItemBinding userBinding) {
            super(userBinding.getRoot());
            this.userBinding = userBinding;
        }
    }
}
