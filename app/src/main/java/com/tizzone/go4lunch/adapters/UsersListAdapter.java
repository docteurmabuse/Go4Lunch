package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.tizzone.go4lunch.databinding.UsersListItemBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.utils.RestaurantHelper;

public class UsersListAdapter extends FirestoreRecyclerAdapter<User, UsersListAdapter.UserViewHolder> {

    private Context context;
    private Restaurant restaurant;
    private final FirestoreRecyclerOptions<User> options;
    //FOR COMMUNICATION
    private final Listener callback;
    private UsersListItemBinding userBinding;
    private String currentUserId;

    public UsersListAdapter(FirestoreRecyclerOptions<User> options, Listener callback) {
        super(options);
        this.options = options;
        this.callback = callback;
        notifyDataSetChanged();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {
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

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        userBinding = UsersListItemBinding.inflate(inflater, parent, false);
        return new UserViewHolder(userBinding);
    }

    public interface Listener {
        void onDataChanged();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public UsersListItemBinding userBinding;

        public UserViewHolder(@NonNull UsersListItemBinding userBinding) {
            super(userBinding.getRoot());
            this.userBinding = userBinding;
        }
    }
}
