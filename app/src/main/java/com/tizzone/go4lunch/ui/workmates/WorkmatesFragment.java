package com.tizzone.go4lunch.ui.workmates;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tizzone.go4lunch.adapters.UsersListAdapter;
import com.tizzone.go4lunch.databinding.FragmentWorkmatesBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.UserRepository;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.tizzone.go4lunch.utils.Constants.RESTAURANT;
import static com.tizzone.go4lunch.utils.Constants.USER_ID;

@AndroidEntryPoint
public class WorkmatesFragment extends Fragment implements UsersListAdapter.UserItemClickListener {

    private WorkmatesViewModel workmatesViewModel;
    private static final String TAG = "FirebaseAuthAppTag";
    private FragmentWorkmatesBinding workmatesBinding;
    @Inject
    public UserRepository userRepository;
    private RecyclerView workmatesRecyclerView;
    private UsersListAdapter adapter;
    private TextView textView;
    public UserViewModel userViewModel;
    public List<User> workmatesList;

    public WorkmatesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workmatesViewModel =
                new ViewModelProvider(this).get(WorkmatesViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        workmatesBinding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View root = workmatesBinding.getRoot();
        textView = workmatesBinding.textNotifications;
        workmatesRecyclerView = workmatesBinding.workmatesRecyclerView;
        workmatesRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));
        getWorkmatesList();
        return root;
    }

    private void getWorkmatesList() {
        userViewModel.getUsersList().observe(getViewLifecycleOwner(), users -> {
            assert this.getArguments() != null;
            String uid = this.getArguments().getString(USER_ID);
            Log.e(TAG, "Workmates list size " + (users.size()));
            users.removeIf(user -> (user.getUid().equals(uid)));
            workmatesList = new ArrayList<>(users);
            adapter.setUserList(workmatesList);
            textView.setVisibility(workmatesList.size() == 0 ? View.VISIBLE : View.GONE);
        });
        adapter = new UsersListAdapter(this);
        workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        workmatesRecyclerView.setAdapter(this.adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        workmatesBinding = null;
    }

    @Override
    public void onUserClick(Restaurant restaurant) {
        Intent intent = new Intent(getContext(), PlaceDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESTAURANT, restaurant);
        intent.putExtras(bundle);
        startActivity(intent);
        Log.e(TAG, RESTAURANT + ": " + (restaurant.getName()));
    }
}