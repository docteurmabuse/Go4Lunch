package com.tizzone.go4lunch.ui.list;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.adapters.UsersListAdapter;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityPlaceDetailBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

import static com.tizzone.go4lunch.utils.Constants.RESTAURANT_ID;
import static com.tizzone.go4lunch.utils.Constants.lunchSpotId;
import static com.tizzone.go4lunch.utils.Constants.myPreference;


@AndroidEntryPoint
public class PlaceDetailActivity extends BaseActivity implements UsersListAdapter.UserItemClickListener {

    private PlacesViewModel placesViewModel;
    private ActivityPlaceDetailBinding placeDetailBinding;
    private AppBarLayout appbar;
    private AppCompatImageButton website;
    private AppCompatImageButton call;
    private RecyclerView usersRecyclerView;
    private Restaurant restaurant;
    private UsersListAdapter usersListAdapter;
    private UserViewModel userViewModel;
    // Define a Place ID.
    private String currentPlaceId;
    private String placePhone;
    private String currentUserId;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getCurrentUser() != null) {
            currentUserId = this.getCurrentUser().getUid();
        }
        placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setUserId(currentUserId);
        restaurant = new Restaurant();
        placeDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_place_detail);
        placeDetailBinding.setUserViewModel(userViewModel);
        Intent intent = this.getIntent();
        if (intent.getStringExtra(RESTAURANT_ID) != null) {
            currentPlaceId = intent.getStringExtra(RESTAURANT_ID);
        }
        placesViewModel.setRestaurant(currentPlaceId);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = placeDetailBinding.detailToolbar;
        setSupportActionBar(toolbar);
        appbar = placeDetailBinding.appBarDetail;
        addOnOffsetChangedListener();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        usersRecyclerView = placeDetailBinding.contentLayoutPlaceDetailActivity.usersSpotList;
        website = placeDetailBinding.contentLayoutPlaceDetailActivity.websiteButton;
        call = placeDetailBinding.contentLayoutPlaceDetailActivity.callButton;
        observeData();
    }

    private void observeData() {
        placesViewModel.setRestaurant(currentPlaceId);
        placesViewModel.getRestaurant().observe(this, this::getRestaurantDetailFromApi);
        userViewModel.getUserLunchInThatSpotList(currentPlaceId);
        if (this.getCurrentUser() != null) {
            userViewModel.getUserLunchInfoFromFirestore(this.getCurrentUser().getUid(), currentPlaceId);
        }
        userViewModel.getIsLunchSpot().observe(this, isLunchSpot -> {
        });
        userViewModel.getIsFavoriteLunchSpot().observe(this, isFavoriteLunchSpot -> placeDetailBinding.setUserViewModel(userViewModel));
        userViewModel.getFabClickResult().observe(this, this::fabOnClick);
        userViewModel.getIsAppBarCollapsed().observe(this, isCollapsed -> placeDetailBinding.setUserViewModel(userViewModel));
    }

    private void getRestaurantDetailFromApi(Restaurant restaurant) {
        if (restaurant != null) {
            this.restaurant = restaurant;
        }
        placeDetailBinding.setRestaurant(restaurant);
        configureRecyclerView(currentPlaceId);
        assert restaurant != null;
        placePhone = restaurant.getPhone();
        call.setOnClickListener(view1 -> dialPhoneNumber(placePhone));
        website.setOnClickListener(view12 -> openWebPage(restaurant.getWebsiteUrl()));
    }

    private void addOnOffsetChangedListener() {
        // Set title of Detail page
        appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> userViewModel.setAppBarIsCollapsed(Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0));
    }

    //Dial restaurant's phone number
    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //Visit restaurant's website
    public void openWebPage(String webUrl) {
        if (webUrl != null && webUrl.startsWith("http")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                showSnackBar(placeDetailBinding.getRoot(), getString(R.string.no_website_notification));
            }
        }
    }

    private void addSpotLunchInSharedPreferences(String restaurantId) {
        SharedPreferences sharedPref = getSharedPreferences(myPreference,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(lunchSpotId, restaurantId);
        editor.apply();
    }

    // Configure RecyclerView with a Query
    private void configureRecyclerView(String placeId) {
        this.usersListAdapter = new UsersListAdapter(this);
        userViewModel.getUserListLunchInThatSpot().observe(this, users -> {
            users.removeIf(user -> {
                if (user.getLunchSpotId() != null) {
                    return (user.getUid().equals(currentUserId) || !user.getLunchSpotId().equals(placeId));
                }
                return true;
            });
            List<User> workmatesList = new ArrayList<>(users);
            usersListAdapter.setUserList(workmatesList);
        });
        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(this.usersListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        placeDetailBinding = null;
    }

    public void fabOnClick(boolean isLunchSpot) {
        if (isLunchSpot) {
            addSpotLunchInSharedPreferences(restaurant.getUid());
            showSnackBar(placeDetailBinding.getRoot(), "You're going to " + restaurant.getName() + " for lunch!");
        } else {
            addSpotLunchInSharedPreferences(null);
            showSnackBar(placeDetailBinding.getRoot(), "You're not going anymore to" + restaurant.getName() + " for lunch!");
        }
    }

    @Override
    public void onUserClick(User user) {
    }

    private void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}

