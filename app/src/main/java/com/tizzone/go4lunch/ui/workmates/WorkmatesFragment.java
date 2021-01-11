package com.tizzone.go4lunch.ui.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.tizzone.go4lunch.adapters.UsersListAdapter;
import com.tizzone.go4lunch.databinding.FragmentWorkmatesBinding;
import com.tizzone.go4lunch.models.User;

import static com.tizzone.go4lunch.api.UserHelper.getUsersCollection;

public class WorkmatesFragment extends Fragment implements UsersListAdapter.Listener {

    private WorkmatesViewModel workmatesViewModel;
    private FragmentWorkmatesBinding workmatesBinding;
    private RecyclerView workmatesRecyclerView;
    private UsersListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        workmatesViewModel =
                new ViewModelProvider(this).get(WorkmatesViewModel.class);
        workmatesBinding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View root = workmatesBinding.getRoot();
        final TextView textView = workmatesBinding.textNotifications;
        workmatesRecyclerView = workmatesBinding.workmatesRecyclerView;
        workmatesViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        getWorkmatesList();
        return root;
    }

    private void getWorkmatesList() {
        Query query = getUsersCollection();
        FirestoreRecyclerOptions<User> workmates = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new UsersListAdapter(generateOptionsForAdapter(query), Glide.with(this), this, "hFwyQ2wqySd5qpFcUSe9FiCClyC2");
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                workmatesRecyclerView.smoothScrollToPosition(adapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        workmatesRecyclerView.setAdapter(this.adapter);
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
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        workmatesBinding.textNotifications.setVisibility(this.adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}