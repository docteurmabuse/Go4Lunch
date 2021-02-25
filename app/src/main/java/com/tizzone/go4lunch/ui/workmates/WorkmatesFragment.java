package com.tizzone.go4lunch.ui.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.tizzone.go4lunch.adapters.UsersListAdapter;
import com.tizzone.go4lunch.databinding.FragmentWorkmatesBinding;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.UserRepository;
import com.tizzone.go4lunch.utils.FirebaseDataSource;
import com.tizzone.go4lunch.utils.UserHelper;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkmatesFragment extends Fragment implements UsersListAdapter.Listener {

    private WorkmatesViewModel workmatesViewModel;

    private FragmentWorkmatesBinding workmatesBinding;
    @Inject
    public UserRepository userRepository;

    @Inject
    public FirebaseDataSource firebaseDataSource;
    private RecyclerView workmatesRecyclerView;
    private UsersListAdapter adapter;
    private TextView textView;
    public UserViewModel userViewModel;

    public WorkmatesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workmatesViewModel =
                new ViewModelProvider(this).get(WorkmatesViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        workmatesBinding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View root = workmatesBinding.getRoot();
        textView = workmatesBinding.textNotifications;
        workmatesRecyclerView = workmatesBinding.workmatesRecyclerView;
        getWorkmatesList();
        return root;
    }

    private void getWorkmatesList() {
        userViewModel.getUsersLiveData().observe(getViewLifecycleOwner(), users1 -> {
            for (User user : users1) {
                System.out.println("ViewModel is working in workmatesFragment" + user.getUserName());
            }
        });
        String uid = this.getArguments().getString("userId");
        adapter = new UsersListAdapter(generateOptionsForAdapter(UserHelper.getWorkmates(uid)), this);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                workmatesRecyclerView.smoothScrollToPosition(adapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        workmatesRecyclerView.setAdapter(this.adapter);

        if (adapter.getItemCount() == 0) {
            workmatesViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        }
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

    @Override
    public void onDataChanged() {
        textView.setVisibility(this.adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        workmatesBinding = null;
    }

}