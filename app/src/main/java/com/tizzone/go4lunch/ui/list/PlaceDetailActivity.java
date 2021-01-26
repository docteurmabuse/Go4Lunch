package com.tizzone.go4lunch.ui.list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
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
import com.tizzone.go4lunch.api.RestaurantHelper;
import com.tizzone.go4lunch.api.UserHelper;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityPlaceDetailBinding;
import com.tizzone.go4lunch.databinding.ContentLayoutPlaceDetailActivityBinding;
import com.tizzone.go4lunch.databinding.FragmentListBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class PlaceDetailActivity extends BaseActivity implements UsersListAdapter.Listener {
    private static final String TAG = "1543";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 5873;
    private String mDetailAddress;
    private String mDetailName;
    private String placePhone;
    private Uri placeWebsite;
    private String lunchSpot;
    private String mDetailPhotoUrl;
    private String uid;
    private List<User> mUsers;
    private ArrayList restaurants;
    private List<String> favouriteRestaurantsList;
    private float ratingThreeStars;
    private float ratingFiveStarFloat;

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
        placeDetailBinding = ActivityPlaceDetailBinding.inflate(getLayoutInflater());
        contentLayoutBinding = placeDetailBinding.contentLayoutPlaceDetailActivity;
        View view = placeDetailBinding.getRoot();
        setContentView(view);
        favouriteRestaurantsList = new ArrayList<String>();

        if (this.getCurrentUser() != null) {
            uid = this.getCurrentUser().getUid();
        }


        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(this);

        toolbar = placeDetailBinding.detailToolbar;
        collapsingToolbar = placeDetailBinding.toolbarLayout;
        appbar = placeDetailBinding.appBarDetail;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        placeName = contentLayoutBinding.detailPlaceName;
        placeAddress = contentLayoutBinding.detailPlaceAddress;
        placeRatingBar = contentLayoutBinding.detailRatingBar;

        placesDetailsTitle = placeDetailBinding.placeDetailsTitle;
        placesDetailsAddress = placeDetailBinding.placeDetailsAddress;
        ImageView DetailImage = placeDetailBinding.mDetailImage;
        usersRecyclerView = placeDetailBinding.contentLayoutPlaceDetailActivity.usersSpotList;
        noWorkmates = placeDetailBinding.contentLayoutPlaceDetailActivity.noWorlmatesTextView;


        Intent intent = this.getIntent();
        if (intent != null) {

            restaurant = intent.getParcelableExtra("RESTAURANT");

            mDetailName = restaurant.getName();

            mDetailAddress = restaurant.getAddress();

            // Specify the fields to return.
            final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.WEBSITE_URI, Place.Field.PHONE_NUMBER, Place.Field.RATING);

            // Construct a request object, passing the place ID and fields array.
            currentPlaceId = restaurant.getUid();
            getUserDataFromFirestore(this.getCurrentUser().getUid());
            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(restaurant.getUid(), placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                placePhone = place.getPhoneNumber();
                placeWebsite = place.getWebsiteUri();

                placeAddress.setText(mDetailAddress);
                placeName.setText(mDetailName);


                if (place.getRating() != null) {
                    ratingFiveStar = place.getRating();
                } else {
                    ratingFiveStar = 1.5;
                }
                ratingFiveStarFloat = ratingFiveStar.floatValue();
                ratingThreeStars = (ratingFiveStarFloat * 3) / 5;
                placeRatingBar.setRating(ratingThreeStars);

                Log.i(TAG, "Place found: " + place.getName());
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });

            addOnOffsetChangedListener();

            AppCompatImageButton call = placeDetailBinding.contentLayoutPlaceDetailActivity.callButton;
            call.setOnClickListener(view1 -> dialPhoneNumber(placePhone));

            likeButton = placeDetailBinding.contentLayoutPlaceDetailActivity.starButton;
            likeButton.setOnClickListener(view1 -> addFavoriteInSharedPreferences());

            AppCompatImageButton website = placeDetailBinding.contentLayoutPlaceDetailActivity.websiteButton;
            website.setOnClickListener(view12 -> openWebPage(placeWebsite));

            mDetailPhotoUrl = restaurant.getPhotoUrl();
            Glide.with(DetailImage.getContext())
                    .load(mDetailPhotoUrl)
                    .into(DetailImage);

            fabOnClickListener();
            configureRecyclerView(currentPlaceId);
        }
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
                    placesDetailsTitle.setText(mDetailName);
                    placesDetailsAddress.setText(mDetailAddress);
                    placesDetailsTitle.setVisibility(View.VISIBLE);
                    placesDetailsAddress.setVisibility(View.VISIBLE);
                    placeAddress.setVisibility(View.INVISIBLE);
                    placeName.setVisibility(View.INVISIBLE);
                    findViewById(R.id.detail_title_layout).setVisibility(View.GONE);
                } else {
                    // Expanded
                    collapsingToolbar.setTitle("");
                    toolbar.setTitle(mDetailAddress);
                    // mDetailAddress = intent.getStringExtra("placeAddress");
                    placeAddress.setText(mDetailAddress);
                    placeName.setText(mDetailName);
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
                    restaurant.isOpen_now(), restaurant.getLocation()).addOnFailureListener(this.onFailureListener());
        }
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

//                    if (currentUser.getFavoriteRestaurants() != null) {
//                        favouriteRestaurantsList = currentUser.getFavoriteRestaurants();
//                    }
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

        this.currentPlaceId = placeId;
        this.usersListAdapter = new UsersListAdapter(generateOptionsForAdapter(UserHelper.getUsersLunchSpotWithoutCurrentUser(this.currentPlaceId, uid)), Glide.with(this), this, this.getCurrentUser().getUid(), false);
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