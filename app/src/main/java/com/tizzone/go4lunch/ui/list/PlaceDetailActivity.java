package com.tizzone.go4lunch.ui.list;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.adapters.UsersListAdapter;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityPlaceDetailBinding;
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

import static com.tizzone.go4lunch.MainActivity.myPreference;

@AndroidEntryPoint
public class PlaceDetailActivity extends BaseActivity implements UsersListAdapter.Listener {
    private static final String TAG = "1543";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 5873;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private static final String CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;

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
    private AppCompatImageButton website;
    private AppCompatImageButton call;

    private NotificationManagerCompat managerCompat;
    private NotificationCompat.Builder builder;

    private RecyclerView usersRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Restaurant restaurant;
    private static final int notificationId = 1578;
    private NotificationManager mNotifyManager;


    UsersListAdapter usersListAdapter;
    RequestManager requestManager;


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
        createNotificationChannel();
        placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);
        restaurant = new Restaurant();
        placeDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_place_detail);
        favouriteRestaurantsList = new ArrayList<>();

        if (this.getCurrentUser() != null) {
            uid = this.getCurrentUser().getUid();
        }

        initViews();
        Intent intent = this.getIntent();
        if (intent != null) {
            restaurant = (Restaurant) intent.getSerializableExtra("RESTAURANT");
            placeDetailBinding.setRestaurant(restaurant);
            currentPlaceId = restaurant.getUid();
            placesViewModel.setRestaurant(currentPlaceId);
            addOnOffsetChangedListener();
        }
    }

    private void initViews() {

        toolbar = placeDetailBinding.detailToolbar;
        collapsingToolbar = placeDetailBinding.toolbarLayout;
        appbar = placeDetailBinding.appBarDetail;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        placesDetailsTitle = placeDetailBinding.placeDetailsTitle;
        placesDetailsAddress = placeDetailBinding.placeDetailsAddress;
        detailImage = placeDetailBinding.mDetailImage;
        placeAddress = placeDetailBinding.contentLayoutPlaceDetailActivity.detailPlaceAddress;
        placeName = placeDetailBinding.contentLayoutPlaceDetailActivity.detailPlaceName;
        usersRecyclerView = placeDetailBinding.contentLayoutPlaceDetailActivity.usersSpotList;
        noWorkmates = placeDetailBinding.contentLayoutPlaceDetailActivity.noWorkmatesTextView;
        likeButton = placeDetailBinding.contentLayoutPlaceDetailActivity.starButton;
        website = placeDetailBinding.contentLayoutPlaceDetailActivity.websiteButton;
        call = placeDetailBinding.contentLayoutPlaceDetailActivity.callButton;
        addSpotLunch = placeDetailBinding.addSpotLunchButton;

        placesViewModel.getRestaurant().observe(this, restaurantDetail -> observeData(restaurantDetail));
    }

    private void observeData(Restaurant restaurant) {
        getUserDataFromFirestore();
        placeDetailBinding.setRestaurant(restaurant);
        this.restaurant = restaurant;
        placePhone = restaurant.getPhone();

        Double rating = (double) restaurant.getRating();
        if (rating != null) {
            ratingFiveStar = rating;
        } else {
            ratingFiveStar = 1.5;
        }
        ratingFiveStarFloat = ratingFiveStar.floatValue();
        ratingThreeStars = (ratingFiveStarFloat * 3) / 5;
        // placeRatingBar.setRating(ratingThreeStars);

        call.setOnClickListener(view1 -> dialPhoneNumber(placePhone));

        // likeButton.setOnClickListener(view1 -> addFavoriteInSharedPreferences());

        website.setOnClickListener(view12 -> openWebPage(placeWebsite));

        fabOnClickListener();
        configureRecyclerView(currentPlaceId);


        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.id.logo_go4lunch)
                .setContentTitle(getCurrentUser().getDisplayName() + "just choose a lunch spot")
                .setContentText("He's lunching at" + restaurant.getName())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Join" + getCurrentUser().getDisplayName() + "for lunch ..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }


    private void addOnOffsetChangedListener() {

        // Set title of Detail page
        appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //Collapsed
                toolbar.setTitle("placeName");
                toolbar.setSubtitle("placeAddress");
                placesDetailsTitle.setVisibility(View.VISIBLE);
                placesDetailsAddress.setVisibility(View.VISIBLE);
                placeAddress.setVisibility(View.INVISIBLE);
                placeName.setVisibility(View.INVISIBLE);
                findViewById(R.id.detail_title_layout).setVisibility(View.GONE);
            } else {
                // Expanded
                collapsingToolbar.setTitle("");
                placeAddress.setVisibility(View.VISIBLE);
                placeName.setVisibility(View.VISIBLE);
                findViewById(R.id.detail_title_layout).setVisibility(View.VISIBLE);
                placesDetailsTitle.setVisibility(View.INVISIBLE);
                placesDetailsAddress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void fabOnClickListener() {
        getUserDataFromFirestore();

        addSpotLunch.setOnClickListener(view -> {
            if (!isLunchSpot) {
                addLunchSpotInFirebase();
                //sendUsersNotification();
                addSpotLunchInSharedPreferences(restaurant.getUid());
                RestaurantHelper.incrementCounter(restaurant.getUid(), 1);
                addSpotLunch.setImageResource(R.drawable.ic_baseline_check_circle_24);
                isLunchSpot = true;
                Snackbar.make(view, "You're going to " + restaurant.getName() + " for lunch!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                sendUsersNotification();
//                managerCompat = NotificationManagerCompat.from(this);
//                managerCompat.notify(100, builder.build());
                NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
                mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

                FirebaseMessaging.getInstance().subscribeToTopic("lunch")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = getString(R.string.msg_subscribed);
                                if (!task.isSuccessful()) {
                                    msg = getString(R.string.msg_subscribe_failed);
                                }
                                Log.d(TAG, msg);
                                System.out.println(" tokens were subscribed successfully");
                                Toast.makeText(PlaceDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                addSpotLunchInSharedPreferences(null);
                addLunchSpotInFirebase();
                addSpotLunch.setImageResource(R.drawable.ic_baseline_add_circle_24);
                RestaurantHelper.incrementCounter(restaurant.getUid(), -1);
                isLunchSpot = false;
                Snackbar.make(view, "You're not going anymore to " + restaurant.getName() + " for lunch!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                FirebaseMessaging.getInstance().unsubscribeFromTopic("lunch")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                System.out.println(" tokens were unsubscribed successfully");
                            }
                        });
                //  sendUsersNotification();

            }
        });
    }

    private void sendUsersNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.id.logo_go4lunch)
                .setContentTitle(getCurrentUser().getDisplayName() + "just choose a lunch spot")
                .setContentText("He's lunching at" + restaurant.getName())
                .setChannelId(CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());
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
        //Add LunchSpot to SharedPreferences
        //addSpotLunchInSharedPreferences(currentPlaceId);
        //Add restaurant to user in Firebase
        UserHelper.updateLunchSpot(restaurant.getUid(), uid).addOnFailureListener(this.onFailureListener());
        String website = null;
        if (restaurant.getWebsiteUrl() != null) {
            website = (restaurant.getWebsiteUrl());
        }
        if (currentPlaceId != null) {
            //Add restaurant in firebase
            RestaurantHelper.createRestaurant(restaurant.getUid(), restaurant.getName(), restaurant.getAddress(), restaurant.getPhotoUrl(), restaurant.getRating(), 1,
                    null, restaurant.getLocation(), website, restaurant.getPhone()).addOnFailureListener(this.onFailureListener());

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

    private void addSpotLunchInSharedPreferences(String lunchSpot) {
        SharedPreferences sharedPref = getSharedPreferences(myPreference,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.lunchSpotId), lunchSpot);
        editor.apply();
    }

    private void getUserDataFromFirestore() {
        // 5 - Get additional data from Firestore

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                lunchSpot = documentSnapshot.toObject(User.class).getLunchSpot();
                favouriteRestaurantsList = documentSnapshot.toObject(User.class).getFavoriteRestaurants();
                if (favouriteRestaurantsList != null) {
                    addFavorite(favouriteRestaurantsList);
                }

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
        });
    }

    // --------------------
    // UI
    // --------------------
    // 5 - Configure RecyclerView with a Query
    private void configureRecyclerView(String placeId) {
        this.usersListAdapter = new UsersListAdapter(generateOptionsForAdapter(UserHelper.getUsersLunchSpotWithoutCurrentUser(placeId, uid))
                , this);
        usersListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                usersRecyclerView.smoothScrollToPosition(usersListAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        //  usersListAdapter.setClickListener(this);

        // usersRecyclerView.setHasFixedSize(true);
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

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "Go4Lunch Notification", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Go4Lunch");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, PlaceDetailActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getCurrentUser().getDisplayName() + " just choose a lunch spot")
                .setContentText("He's lunching at " + restaurant.getName())
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_logo_go4lunch);

    }
}

