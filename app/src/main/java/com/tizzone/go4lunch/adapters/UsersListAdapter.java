package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.UsersListItemBinding;
import com.tizzone.go4lunch.models.User;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserViewHolder> {

    private List<User> userList;
    private final UserItemClickListener mListener;

    public UsersListAdapter(UserItemClickListener mListener) {
        this.mListener = mListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        UsersListItemBinding userBinding = DataBindingUtil.inflate(inflater, R.layout.users_list_item, parent, false);
        return new UserViewHolder(userBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userBinding.setWorkmates(user);
        holder.userBinding.setUserItemClick(mListener);
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    public interface UserItemClickListener {
        void onUserClick(User user);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public UsersListItemBinding userBinding;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userBinding = DataBindingUtil.bind(itemView);
        }
    }
}
