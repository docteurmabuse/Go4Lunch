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

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.adapters.UsersListAdapter;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityPlaceDetailBinding;
import com.tizzone.go4lunch.databinding.FragmentListBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

import static com.tizzone.go4lunch.utils.Constants.CHANNEL_ID;
import static com.tizzone.go4lunch.utils.Constants.NOTIFICATION_ID;
import static com.tizzone.go4lunch.utils.Constants.PRIMARY_CHANNEL_ID;
import static com.tizzone.go4lunch.utils.Constants.TAG;
import static com.tizzone.go4lunch.utils.Constants.myPreference;
import static com.tizzone.go4lunch.utils.Constants.notificationId;


@AndroidEntryPoint
public class PlaceDetailActivity extends BaseActivity implements UsersListAdapter.UserItemClickListener {

    private String mDetailAddress;
    private String mDetailName;
    private String placePhone;
    private Uri placeWebsite;
    private String lunchSpot;
    private String mDetailPhotoUrl;
    private String currentUserId;
    private List<String> favouriteRestaurantsList;
    private float ratingThreeStars;
    private float ratingFiveStarFloat;
    private PlacesViewModel placesViewModel;
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
    private boolean isLunchSpot;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placesDetailsTitle;
    private TextView placesDetailsAddress;
    private TextView noWorkmates;

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
    private NotificationManager mNotifyManager;
    private UsersListAdapter usersListAdapter;
    private UserViewModel userViewModel;

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
        if (this.getCurrentUser() != null) {
            currentUserId = this.getCurrentUser().getUid();
        }
        placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setUserId(currentUserId);
        restaurant = new Restaurant();
        placeDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_place_detail);
        placeDetailBinding.setUserViewModel(userViewModel);
        favouriteRestaurantsList = new ArrayList<>();
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
        placeAddress = placeDetailBinding.contentLayoutPlaceDetailActivity.detailPlaceAddress;
        placeName = placeDetailBinding.contentLayoutPlaceDetailActivity.detailPlaceName;
        usersRecyclerView = placeDetailBinding.contentLayoutPlaceDetailActivity.usersSpotList;
        noWorkmates = placeDetailBinding.contentLayoutPlaceDetailActivity.noWorkmatesTextView;
        likeButton = placeDetailBinding.contentLayoutPlaceDetailActivity.starButton;
        website = placeDetailBinding.contentLayoutPlaceDetailActivity.websiteButton;
        call = placeDetailBinding.contentLayoutPlaceDetailActivity.callButton;
        addSpotLunch = placeDetailBinding.addSpotLunchButton;
        placesViewModel.getRestaurant().observe(this, this::observeData);
    }

    private void observeData(Restaurant restaurant) {
        userViewModel.getUserLunchInThatSpotList(currentPlaceId);
        if (this.getCurrentUser() != null) {
            userViewModel.getIsLunchSpotFromFirestore(this.getCurrentUser().getUid(), currentPlaceId);
        }
        userViewModel.getIsLunchSpot().observe(this, isLunchSpot -> {
            placeDetailBinding.setUserViewModel(userViewModel);
        });
        userViewModel.getIsFavoriteLunchSpot().observe(this, isFavoriteLunchSpot -> {
            placeDetailBinding.setUserViewModel(userViewModel);
        });
        userViewModel.getFabClickResult().observe(this, isFavoriteLunchSpot -> {
            fabOnClick(isFavoriteLunchSpot);
        });

        placeDetailBinding.setRestaurant(restaurant);
        this.restaurant = restaurant;
        placePhone = restaurant.getPhone();
        // placeRatingBar.setRating(ratingThreeStars);

        call.setOnClickListener(view1 -> dialPhoneNumber(placePhone));

        // likeButton.setOnClickListener(view1 -> addFavoriteInSharedPreferences());

        website.setOnClickListener(view12 -> openWebPage(placeWebsite));

        //fabOnClickListener();
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

/*
    private void fabOnClickListener() {
        getUserDataFromFirestore();
        addSpotLunch.setOnClickListener(view -> {
            if (!isLunchSpot) {
                addLunchSpotInFirebase();
                addSpotLunchInSharedPreferences(restaurant.getUid());
                RestaurantHelper.incrementCounter(restaurant.getUid(), 1);
                addSpotLunch.setImageResource(R.drawable.ic_baseline_check_circle_24);
                isLunchSpot = true;
                Snackbar.make(view, "You're going to " + restaurant.getName() + " for lunch!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                sendUsersNotification();
                NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
                mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
                FirebaseMessaging.getInstance().subscribeToTopic("lunch")
                        .addOnCompleteListener(task -> {
                            String msg = getString(R.string.msg_subscribed);
                            if (!task.isSuccessful()) {
                                msg = getString(R.string.msg_subscribe_failed);
                            }
                            Log.d(TAG, msg);
                            System.out.println(" tokens were subscribed successfully");
                            Toast.makeText(PlaceDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
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
            }
        });
    }
*/

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

    private void addSpotLunchInSharedPreferences(String lunchSpot) {
        SharedPreferences sharedPref = getSharedPreferences(myPreference,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.lunchSpotId), lunchSpot);
        editor.apply();
    }

    // Configure RecyclerView with a Query
    private void configureRecyclerView(String placeId) {
        this.usersListAdapter = new UsersListAdapter(this);
        userViewModel.getUserListLunchInThatSpot().observe(this, users -> {
            users.removeIf(user -> {
                if (user.getLunchSpot() != null) {
                    return (user.getUid().equals(currentUserId) || !user.getLunchSpot().equals(placeId));
                }
                return true;
            });
            List<User> workmatesList = new ArrayList<User>(users);
            usersListAdapter.setUserList(workmatesList);
            noWorkmates.setVisibility(workmatesList.size() == 0 ? View.VISIBLE : View.GONE);
            for (User user : workmatesList) {
                System.out.println("ViewModel is working in workmatesFragment" + user.getUserEmail());
                Log.e(TAG, "size rx1: " + user.getLunchSpot() + " :" + placeId);
            }
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
                .setContentTitle(Objects.requireNonNull(getCurrentUser()).getDisplayName() + " just choose a lunch spot")
                .setContentText("He's lunching at " + restaurant.getName())
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_logo_go4lunch);
    }

    @Override
    public void onUserClick(Restaurant restaurant) {
    }

    public void fabOnClick(boolean isLunchSpot) {
        if (isLunchSpot) {
            Snackbar.make(placeDetailBinding.getRoot(), "You're going to " + restaurant.getName() + " for lunch!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            sendUsersNotification();
            addSpotLunchInSharedPreferences(restaurant.getUid());
            NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
            mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
            FirebaseMessaging.getInstance().subscribeToTopic("lunch")
                    .addOnCompleteListener(task -> {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        System.out.println(" tokens were subscribed successfully");
                        Toast.makeText(PlaceDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                    });

        } else {
            addSpotLunchInSharedPreferences(null);
//                addSpotLunch.setImageResource(R.drawable.ic_baseline_add_circle_24);
//                RestaurantHelper.incrementCounter(restaurant.getUid(), -1);
            Snackbar.make(placeDetailBinding.getRoot(), "You're not going anymore to " + restaurant.getName() + " for lunch!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            FirebaseMessaging.getInstance().unsubscribeFromTopic("lunch")
                    .addOnCompleteListener(task -> {
                        System.out.println(" tokens were unsubscribed successfully");
                    });
        }
    }
}

