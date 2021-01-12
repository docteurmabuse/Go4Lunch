package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.tizzone.go4lunch.databinding.UsersListItemBinding;
import com.tizzone.go4lunch.models.User;

public class UsersListAdapter extends FirestoreRecyclerAdapter<User, UsersListAdapter.UserViewHolder> {
    private UsersListItemBinding userBinding;
    private String userLunch;
    //FOR DATA
    private final RequestManager glide;
    private final String idCurrentUser;
    //FOR COMMUNICATION
    private final Listener callback;

    public UsersListAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide, Listener callback, String idCurrentUser) {
        super(options);
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
        //  User user= users.get(position);
        //  User 2 = new User("122112",false,"Ben","https://lh3.googleusercontent.com/a-/AOh14GgjDPW9btHlUI8CJCUHHodyZxrGaZt3BRZssJybow=s96-c",null,"ChIJP_-HCS9u5kcRsj9b1x7Pl8w");
        //mUsers.add(user1);
        //display place thumbnail

        holder.updateWithUser(user, this.idCurrentUser, this.glide);
//        if (user.getPhotoUrl() != null) {
//            String imageUrl = user.getPhotoUrl();
//            Glide.with(holder.itemView)
//                    .load(imageUrl)
//                    .into(holder.avatarView);
//        }
//
//        holder.userText.setText(user.getUserName() + userLunch);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        userBinding = UsersListItemBinding.inflate(inflater, parent, false);
        return new UserViewHolder(userBinding);
    }

    public interface Listener {
        void onDataChanged();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarView;
        TextView userText;

        public UserViewHolder(@NonNull UsersListItemBinding userBinding) {
            super(userBinding.getRoot());
            avatarView = userBinding.avatarView;
            userText = userBinding.avatarTextView;
        }

        public void updateWithUser(User user, String idCurrentUser, RequestManager glide) {
            // Check if current user is the sender
            Boolean isCurrentUser = user.getUid().equals(idCurrentUser);
            this.userText.setText(user.getUserName());
            if (user.getPhotoUrl() != null) {
                glide.load(user.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(avatarView);
            }

        }
    }

}
