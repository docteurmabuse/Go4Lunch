package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.tizzone.go4lunch.databinding.UsersListItemBinding;
import com.tizzone.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersListAdapter extends FirestoreRecyclerAdapter<User, UsersListAdapter.UserViewHolder> {
    private final Context mContext;
    private final List<User> mUsers = new ArrayList<>();
    private UsersListItemBinding userBinding;
    private String userLunch;
    //FOR DATA
    private final RequestManager glide;
    private final String idCurrentUser;
    //FOR COMMUNICATION
    private final Listener callback;

    public UsersListAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide, Listener callback, String idCurrentUser, Context mContext) {
        super(options);
        this.mContext = mContext;
        this.glide = glide;
        this.idCurrentUser = idCurrentUser;
        this.callback = callback;

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
        user = mUsers.get(position);

        //display place thumbnail
        if (user.getPhotoUrl() != null) {
            String imageUrl = user.getPhotoUrl();
            Glide.with(holder.itemView)
                    .load(imageUrl)
                    .into(holder.avatarView);
        }

        holder.userText.setText(user.getUserName() + userLunch);
    }

    @NonNull
    @Override
    public UsersListAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        userBinding = UsersListItemBinding.inflate(inflater, parent, false);
        return new UsersListAdapter.UserViewHolder(userBinding);
    }

    public interface Listener {
        void onDataChanged();
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
