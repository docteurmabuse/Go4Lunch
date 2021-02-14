package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.UsersListItemBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.utils.RestaurantHelper;

public class UsersListAdapter extends FirestoreRecyclerAdapter<User, UsersListAdapter.UserViewHolder> {

    private Context context;
    private Restaurant restaurant;

    public UsersListAdapter(FirestoreRecyclerOptions<User> options
    ) {
        super(options);
        notifyDataSetChanged();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {
        holder.userBinding.setWorkmates(user);
        if (user.getLunchSpot() != null) {
            RestaurantHelper.getRestaurantsById(user.getLunchSpot()).addOnSuccessListener(documentSnapshot -> {
                restaurant = documentSnapshot.toObject(Restaurant.class);
                setHolder(holder, user, restaurant);
            });
        } else {
            holder.updateWithUser(user, restaurant);
        }
    }

    private void setHolder(UserViewHolder holder, User user, Restaurant restaurant) {
        holder.updateWithUser(user, restaurant);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        com.tizzone.go4lunch.databinding.UsersListItemBinding userBinding = UsersListItemBinding.inflate(inflater, parent, false);
        return new UserViewHolder(userBinding);
    }


    public interface Listener {
        void onDataChanged();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView userText;
        private final UsersListItemBinding userBinding;

        public UserViewHolder(@NonNull UsersListItemBinding userBinding) {
            super(userBinding.getRoot());
            userText = userBinding.avatarTextView;
            this.userBinding = userBinding;
        }

        public void updateWithUser(User user, Restaurant restaurant) {
            // Check if current user is the sender
            if (itemView.getContext() instanceof PlaceDetailActivity) {
                String joiningText = context.getResources().getString(R.string.joining_text, user.getUserName());
                this.userText.setText(joiningText);
            } else {
                if (restaurant != null) {
                    Resources resources = context.getResources();
                    String lunchingText = String.format(resources.getString(R.string.lunching_text), user.getUserName(), restaurant.getName());
                    userText.setText(lunchingText);
                } else {
                    Resources resources = context.getResources();
                    String notDecidedYet = String.format(resources.getString(R.string.not_decided), user.getUserName());
                    userText.setText(notDecidedYet);
                }

                itemView.setOnClickListener(view -> {
                    final Context context = itemView.getContext();
                    Intent intent = new Intent(context, PlaceDetailActivity.class);
                    intent.putExtra("RESTAURANT", restaurant.getUid());
                    context.startActivity(intent);
                });

            }
        }

    }
}
