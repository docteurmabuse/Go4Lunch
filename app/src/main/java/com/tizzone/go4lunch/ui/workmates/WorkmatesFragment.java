package com.tizzone.go4lunch.ui.workmates;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.tizzone.go4lunch.adapters.UsersListAdapter;
import com.tizzone.go4lunch.databinding.FragmentWorkmatesBinding;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import javax.inject.Inject;

public class WorkmatesFragment extends Fragment implements UsersListAdapter.UserListener {

    private WorkmatesViewModel workmatesViewModel;

    private FragmentWorkmatesBinding workmatesBinding;
    private RecyclerView workmatesRecyclerView;

    private UsersListAdapter adapter;
    private TextView textView;
    private String uid;

    @Inject
    UsersListAdapter usersListAdapter;
    @Inject
    RequestManager requestManager;
    @Inject
    UserViewModel userViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        workmatesBinding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View root = workmatesBinding.getRoot();
        textView = workmatesBinding.textNotifications;
        workmatesRecyclerView = workmatesBinding.workmatesRecyclerView;
        setRecyclerView();
        // getWorkmatesList(this.getArguments().getString("userId"));
        return root;
    }

    private void setRecyclerView() {
        workmatesRecyclerView.setHasFixedSize(true);
        workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        workmatesRecyclerView.setAdapter(usersListAdapter);
        usersListAdapter.setClickListener(this);
        usersListAdapter.startListening();
    }

    private void getWorkmatesList(String uid) {

        // adapter = new UsersListAdapter(generateOptionsForAdapter(UserHelper.getWorkmates(uid)), Glide.with(this), this, "hFwyQ2wqySd5qpFcUSe9FiCClyC2", true);
//
//        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                workmatesRecyclerView.smoothScrollToPosition(adapter.getItemCount()); // Scroll to bottom on new messages
//            }
//        });
        workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        workmatesRecyclerView.setAdapter(usersListAdapter);

//        if (usersListAdapter.getItemCount() == 0) {
//            workmatesViewModel =
//                    new ViewModelProvider(this).get(WorkmatesViewModel.class);
//            workmatesViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
//        }
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        usersListAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersListAdapter.stopListening();
    }

    // --------------------
    // CALLBACK
    // --------------------

//    @Override
//    public void onDataChanged() {
//        textView.setVisibility(this.usersListAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        workmatesBinding = null;
    }

    @Override
    public void onUserClick(Context context, String uid) {

    }
}