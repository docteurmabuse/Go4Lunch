package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tizzone.go4lunch.databinding.UsersListItemBinding;
import com.tizzone.go4lunch.models.user.User;

import java.util.ArrayList;
import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserViewHolder> {
    private final Context mContext;
    private List<User> mUsers = new ArrayList<>();
    private UsersListItemBinding userBinding;
    private String userLunch;

    public UsersListAdapter(List<User> mUsers, Context mContext) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public UsersListAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        userBinding = UsersListItemBinding.inflate(inflater, parent, false);
        return new UsersListAdapter.UserViewHolder(userBinding);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mUsers.get(position);

        //display place thumbnail
        if (user.getPhotoUrl() != null) {
            String imageUrl = user.getPhotoUrl();
            Glide.with(holder.itemView)
                    .load(imageUrl)
                    .into(holder.avatarView);
        }

        holder.userText.setText(user.getUserName() + userLunch);
    }


    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarView;
        TextView userText;

        public UserViewHolder(@NonNull UsersListItemBinding userBinding) {
            super(userBinding.getRoot());
            avatarView = userBinding.avatarView;
            userText = userBinding.avatarTextView;
        }
    }
}
