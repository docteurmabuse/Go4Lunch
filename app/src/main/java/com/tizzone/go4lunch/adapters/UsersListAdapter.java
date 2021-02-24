package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
    private final FirestoreRecyclerOptions<User> options;
    //FOR COMMUNICATION
    private final Listener callback;
    private UsersListItemBinding userBinding;


    public UsersListAdapter(FirestoreRecyclerOptions<User> options, Listener callback
    ) {
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

        Resources resources = holder.itemView.getContext().getResources();
        if (user.getLunchSpot() != null) {
            if (holder.itemView.getContext() instanceof PlaceDetailActivity) {
//                String joiningText = resources.getString(R.string.joining_text, user.getUserName());
//                holder.userText.setText(joiningText);
            } else {
                RestaurantHelper.getRestaurantsById(user.getLunchSpot()).addOnSuccessListener(documentSnapshot -> {
                    restaurant = documentSnapshot.toObject(Restaurant.class);
                    holder.userBinding.setRestaurant(restaurant);
                    // String lunchingText = String.format(resources.getString(R.string.lunching_text), user.getUserName(), restaurant.getName());
                    // holder.userText.setText(lunchingText);
                });
                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(context, PlaceDetailActivity.class);
                    intent.putExtra("RESTAURANT", restaurant.getUid());
                    context.startActivity(intent);
                });
            }
        } else {
            String notDecidedYet = String.format(resources.getString(R.string.not_decided), user.getUserName());
            //holder.userText.setText(notDecidedYet);
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
        public TextView userText;
        public UsersListItemBinding userBinding;

        public UserViewHolder(@NonNull UsersListItemBinding userBinding) {
            super(userBinding.getRoot());
            this.userBinding = userBinding;
            // userText = userBinding.avatarTextView;
        }

        public void updateWithUser(User user, Restaurant restaurant) {
            // Check if current user is the sender
            Resources resources = itemView.getContext().getResources();

            if (itemView.getContext() instanceof PlaceDetailActivity) {
                String joiningText = resources.getString(R.string.joining_text, user.getUserName());
                this.userText.setText(joiningText);
            } else {
                if (restaurant != null) {
                    String lunchingText = String.format(resources.getString(R.string.lunching_text), user.getUserName(), restaurant.getName());
                    userText.setText(lunchingText);
                } else {
                    String notDecidedYet = String.format(resources.getString(R.string.not_decided), user.getUserName());
                    userText.setText(notDecidedYet);
                }

                itemView.setOnClickListener(view -> {
                    final Context context = itemView.getContext();
                    Intent intent = new Intent(context, PlaceDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("RESTAURANT", restaurant);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                });

            }
        }

    }
}
