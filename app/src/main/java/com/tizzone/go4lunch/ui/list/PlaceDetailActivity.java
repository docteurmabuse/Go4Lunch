package com.tizzone.go4lunch.ui.list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.adapters.UsersListAdapter;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityPlaceDetailBinding;
import com.tizzone.go4lunch.databinding.ContentLayoutPlaceDetailActivityBinding;
import com.tizzone.go4lunch.databinding.FragmentListBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.utils.RestaurantHelper;
import com.tizzone.go4lunch.utils.UserHelper;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaceDetailActivity extends BaseActivity implements UsersListAdapter.Listener {
    private static final String TAG = "1543";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 5873;
    private static final String CHANNEL_ID = "4578";
    private String mDetailAddress;
    private String mDetailName;
    private String placePhone;
    private Uri placeWebsite;
    private String lunchSpot;
    private String mDetailPhotoUrl;
    private String uid;
    private List<String> favouriteRestaurantsList;
    private float ratingThreeStars;
    private float ratingFiveStarFloat;
    private PlacesViewModel placesViewModel;
    private String key;
    private Restaurant restaurantDetail;
    @Nullable
    boolean isOpen;

    private ActivityPlaceDetailBinding placeDetailBinding;
    private ContentLayoutPlaceDetailActivityBinding contentLayoutBinding;
    private FragmentListBinding listBinding;

    private FloatingActionButton addSpotLunch;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appbar;
    // Define a Place ID.
    private String currentPlaceId;
    private PlacesClient placesClient;
    private boolean isLunchSpot;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placesDetailsTitle;
    private TextView placesDetailsAddress;
    private TextView noWorkmates;
    private ImageView detailImage;
    private RatingBar placeRatingBar;
    private Double ratingFiveStar;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> favouritesArray;
    private AppCompatImageButton likeButton;


    private UsersListAdapter usersListAdapter;
    private RecyclerView usersRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Gson gson;
    private String json;
    private Restaurant restaurant;


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
        placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);
        restaurant = new Restaurant();
        placeDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_place_detail);

        favouriteRestaurantsList = new ArrayList<String>();

        if (this.getCurrentUser() != null) {
            uid = this.getCurrentUser().getUid();
        }
        key = getText(R.string.google_maps_key).toString();

        toolbar = placeDetailBinding.detailToolbar;
        collapsingToolbar = placeDetailBinding.toolbarLayout;
        appbar = placeDetailBinding.appBarDetail;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        placesDetailsTitle = placeDetailBinding.placeDetailsTitle;
        placesDetailsAddress = placeDetailBinding.placeDetailsAddress;
        detailImage = placeDetailBinding.mDetailImage;
        usersRecyclerView = contentLayoutBinding.usersSpotList;
        noWorkmates = contentLayoutBinding.noWorkmatesTextView;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentPlaceId = extras.getString("RESTAURANT");
            placesViewModel.setRestaurant(currentPlaceId);
            placesViewModel.getRestaurant().observe(this, new Observer<Restaurant>() {
                @Override
                public void onChanged(Restaurant restaurantDetail) {
                    observeData(restaurantDetail);
                }
            });

            getUserDataFromFirestore(this.getCurrentUser().getUid());

            addOnOffsetChangedListener();

        }
    }

    private void observeData(Restaurant restaurant) {
        placeDetailBinding.setRestaurant(restaurant);
        placePhone = restaurant.getPhone();
        placeAddress.setText(mDetailAddress);
        placeName.setText(mDetailName);
        Double rating = (double) restaurant.getRating();
        if (rating != null) {
            ratingFiveStar = rating;
        } else {
            ratingFiveStar = 1.5;
        }
        ratingFiveStarFloat = ratingFiveStar.floatValue();
        ratingThreeStars = (ratingFiveStarFloat * 3) / 5;
        placeRatingBar.setRating(ratingThreeStars);
        AppCompatImageButton call = contentLayoutBinding.callButton;

        call.setOnClickListener(view1 -> dialPhoneNumber(placePhone));

        likeButton = contentLayoutBinding.starButton;
        likeButton.setOnClickListener(view1 -> addFavoriteInSharedPreferences());

        AppCompatImageButton website = contentLayoutBinding.websiteButton;
        website.setOnClickListener(view12 -> openWebPage(placeWebsite));

        mDetailPhotoUrl = restaurant.getPhotoUrl() + key;
        Glide.with(detailImage.getContext())
                .load(mDetailPhotoUrl)
                .into(detailImage);

        fabOnClickListener();
        configureRecyclerView(currentPlaceId);
    }


    private void addOnOffsetChangedListener() {

        // Set title of Detail page
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    // Collapsed
                    // collapsingToolbar.setCollapsedTitleTextColor(0xffffff);
                    toolbar.setTitle("placeName");
                    toolbar.setSubtitle("placeAddress");
                    placesDetailsTitle.setText(restaurant.getName());
                    placesDetailsTitle.setText(restaurant.getAddress());
                    placesDetailsTitle.setVisibility(View.VISIBLE);
                    placesDetailsAddress.setVisibility(View.VISIBLE);
                    placeAddress.setVisibility(View.INVISIBLE);
                    placeName.setVisibility(View.INVISIBLE);
                    findViewById(R.id.detail_title_layout).setVisibility(View.GONE);
                } else {
                    // Expanded
                    collapsingToolbar.setTitle("");
                    // mDetailAddress = intent.getStringExtra("placeAddress");
                    placeAddress.setVisibility(View.VISIBLE);
                    placeName.setVisibility(View.VISIBLE);
                    findViewById(R.id.detail_title_layout).setVisibility(View.VISIBLE);
                    placesDetailsTitle.setVisibility(View.INVISIBLE);
                    placesDetailsAddress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void fabOnClickListener() {
        addSpotLunch = placeDetailBinding.addSpotLunchButton;
        if (uid != null) {
            getUserDataFromFirestore(uid);
        }

        addSpotLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLunchSpot) {
                    addLunchSpotInFirebase();
                    addSpotLunch.setImageResource(R.drawable.ic_baseline_check_circle_24);
                    isLunchSpot = true;
                    Snackbar.make(view, "You're going to " + mDetailName + " for lunch!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    addLunchSpotInFirebase();
                    addSpotLunch.setImageResource(R.drawable.ic_baseline_add_circle_24);
                    isLunchSpot = false;
                    Snackbar.make(view, "You're not going anymore to " + mDetailName + " for lunch!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
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
    public void openWebPage(Uri url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //Add Lunch Spot
    private void addLunchSpotInFirebase() {
        //Add restaurant to user in Firebase
        UserHelper.updateLunchSpot(currentPlaceId, uid).addOnFailureListener(this.onFailureListener());

        if (currentPlaceId != null) {
            //Add restaurant in firebase
            RestaurantHelper.createRestaurant(currentPlaceId, mDetailName, mDetailAddress, mDetailPhotoUrl, ratingFiveStarFloat, 1,
                    isOpen, restaurant.getLocation(), placeWebsite.toString(), placePhone).addOnFailureListener(this.onFailureListener());

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.id.logo_go4lunch)
                .setContentTitle(getCurrentUser().getDisplayName() + "just choose a lunch spot")
                .setContentText("He's lunching at" + restaurant.getName())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    //Add favourite in Firebase
    private void addFavouriteInFirebase(List<String> restaurants, String idLunchSpot) {
        //Add restaurant to user in Firebase
        UserHelper.updateFavoriteRestaurants(restaurants, uid).addOnFailureListener(this.onFailureListener());
    }

    //Add Like to a restaurant
    private void addFavorite(List<String> favouriteRestaurantsList) {
        if (favouriteRestaurantsList.size() == 0) {
            likeButton.setImageResource(R.drawable.ic_baseline_star_outline_24);
        } else {
            {
                boolean like = favouriteRestaurantsList.contains(currentPlaceId);
                if (!like) {
                    likeButton.setImageResource(R.drawable.ic_baseline_star_outline_24);
                } else {
                    likeButton.setImageResource(R.drawable.ic_baseline_star_24);
                }
            }
        }
    }

    private void addFavoriteInSharedPreferences() {
        if (favouriteRestaurantsList.size() == 0) {
            favouriteRestaurantsList.add(currentPlaceId);
            likeButton.setImageResource(R.drawable.ic_baseline_star_24);
            addFavouriteInFirebase(favouriteRestaurantsList, uid);
            Toast.makeText(PlaceDetailActivity.this, "You add this restaurant to your favourite", Toast.LENGTH_LONG).show();
        } else {
            boolean like = favouriteRestaurantsList.contains(currentPlaceId);
            if (!like) {
                favouriteRestaurantsList.add(currentPlaceId);
                addFavouriteInFirebase(favouriteRestaurantsList, uid);
                likeButton.setImageResource(R.drawable.ic_baseline_star_24);
                Toast.makeText(PlaceDetailActivity.this, "You add this restaurant to your favourite", Toast.LENGTH_LONG).show();
            } else {
                favouriteRestaurantsList.remove(currentPlaceId);
                addFavouriteInFirebase(favouriteRestaurantsList, uid);
                likeButton.setImageResource(R.drawable.ic_baseline_star_outline_24);
                Toast.makeText(PlaceDetailActivity.this, "You remove this restaurant to your favourite", Toast.LENGTH_LONG).show();
            }
        }

    }


    private void getUserDataFromFirestore(String uid) {
        // 5 - Get additional data from Firestore

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                lunchSpot = documentSnapshot.toObject(User.class).getLunchSpot();
                favouriteRestaurantsList = documentSnapshot.toObject(User.class).getFavoriteRestaurants();
                if (favouriteRestaurantsList != null) {
                    addFavorite(favouriteRestaurantsList);
                    if (lunchSpot != null) {
                        isLunchSpot = lunchSpot.equals(currentPlaceId);
                        if (!isLunchSpot) {
                            addSpotLunch.setImageResource(R.drawable.ic_baseline_add_circle_24);
                        } else {
                            addSpotLunch.setImageResource(R.drawable.ic_baseline_check_circle_24);
                        }
                    } else {
                        isLunchSpot = false;
                        addSpotLunch.setImageResource(R.drawable.ic_baseline_add_circle_24);
                    }
                }
            }
        });
    }

    // --------------------
    // UI
    // --------------------
    // 5 - Configure RecyclerView with a Query
    private void configureRecyclerView(String placeId) {
        this.usersListAdapter = new UsersListAdapter(generateOptionsForAdapter(UserHelper.getUsersLunchSpotWithoutCurrentUser(placeId, uid)), Glide.with(this), this, this.getCurrentUser().getUid(), false);
        usersListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                usersRecyclerView.smoothScrollToPosition(usersListAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(this.usersListAdapter);
    }

    // 6 - Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }


    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        noWorkmates.setVisibility(this.usersListAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        placeDetailBinding = null;
    }
}

