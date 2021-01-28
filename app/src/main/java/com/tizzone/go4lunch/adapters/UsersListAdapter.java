package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.UsersListItemBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.utils.RestaurantHelper;

import java.util.ArrayList;
import java.util.List;

public class UsersListAdapter extends FirestoreRecyclerAdapter<User, UsersListAdapter.UserViewHolder> {
    private UsersListItemBinding userBinding;
    private String userLunch;

    //FOR DATA
    private final RequestManager glide;
    private final String idCurrentUser;
    private final boolean isWorkmatesView;
    private Context context;
    //FOR COMMUNICATION
    private final Listener callback;
    private Restaurant restaurant;


    public UsersListAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide, Listener callback, String idCurrentUser, boolean isWorkmatesView) {
        super(options);
        this.glide = glide;
        this.idCurrentUser = idCurrentUser;
        this.callback = callback;
        this.isWorkmatesView = isWorkmatesView;
        List<Restaurant> restaurants = new ArrayList<>();

    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {
        if (user.getLunchSpot() != null) {
            RestaurantHelper.getRestaurants(user.getLunchSpot()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    restaurant = documentSnapshot.toObject(Restaurant.class);
                    setHolder(holder, user, restaurant);
                }
            });
        } else {
            holder.updateWithUser(user, this.idCurrentUser, this.glide, this.isWorkmatesView, restaurant);
        }
    }

    private void setHolder(UserViewHolder holder, User user, Restaurant restaurant) {
        holder.updateWithUser(user, this.idCurrentUser, this.glide, this.isWorkmatesView, restaurant);
        if (isWorkmatesView) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Context context = holder.itemView.getContext();
                    Intent intent = new Intent(context, PlaceDetailActivity.class);
                    intent.putExtra("RESTAURANT", restaurant);
                    context.startActivity(intent);
                }
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
        private final ImageView avatarView;
        private final TextView userText;
        private Restaurant restaurant;

        public UserViewHolder(@NonNull UsersListItemBinding userBinding) {
            super(userBinding.getRoot());
            avatarView = userBinding.avatarView;
            userText = userBinding.avatarTextView;
        }

        public void updateWithUser(User user, String idCurrentUser, RequestManager glide, boolean isWorkmatesView, Restaurant restaurant) {
            // Check if current user is the sender
            if (!isWorkmatesView) {
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

            }
            if (user.getPhotoUrl() != null) {
                glide.load(user.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(avatarView);
            } else {
                glide.load(R.mipmap.ic_workmates)
                        .apply(RequestOptions.circleCropTransform())
                        .into(avatarView);
            }
        }

    }
}
