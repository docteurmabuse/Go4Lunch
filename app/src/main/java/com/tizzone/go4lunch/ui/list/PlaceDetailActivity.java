package com.tizzone.go4lunch.ui.list;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.api.UserHelper;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityPlaceDetailBinding;
import com.tizzone.go4lunch.databinding.ContentLayoutPlaceDetailActivityBinding;
import com.tizzone.go4lunch.models.user.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlaceDetailActivity extends BaseActivity {
    private static final String TAG = "1543";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 5873;
    private String mDetailAddress;
    private String mDetailName;
    private int lunchSpot;
    private ActivityPlaceDetailBinding placeDetailBinding;
    private ContentLayoutPlaceDetailActivityBinding contentLayoutBinding;
    private String placePhone;
    private Uri placeWebsite;
    private FloatingActionButton addSpotLunch;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appbar;
    // Define a Place ID.
    private String placeId;
    private PlacesClient placesClient;
    private boolean isLunchSpot;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placesDetailsTitle;
    private TextView placesDetailsAddress;
    private String uid;

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

        if (this.getCurrentUser() != null) {
            uid = this.getCurrentUser().getUid();
        }

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(this);

        //setContentView(R.layout.activity_place_detail);
        toolbar = placeDetailBinding.detailToolbar;
        collapsingToolbar = placeDetailBinding.toolbarLayout;
        appbar = placeDetailBinding.appBarDetail;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        placeName = contentLayoutBinding.detailPlaceName;
        placeAddress = contentLayoutBinding.detailPlaceAddress;

        placesDetailsTitle = placeDetailBinding.placeDetailsTitle;
        placesDetailsAddress = placeDetailBinding.placeDetailsAddress;
        ImageView DetailImage = placeDetailBinding.mDetailImage;

        Intent intent = this.getIntent();
        if (intent != null) {
            // Specify the fields to return.
            final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.WEBSITE_URI, Place.Field.PHONE_NUMBER);

            // Construct a request object, passing the place ID and fields array.
            placeId = intent.getStringExtra("placeId");
            getUserDataFromFirebase(this.getCurrentUser().getUid());
            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                placePhone = place.getPhoneNumber();
                placeWebsite = place.getWebsiteUri();
                mDetailName = place.getName();
                mDetailAddress = place.getAddress();
                placeAddress.setText(mDetailAddress);
                placeName.setText(mDetailName);
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

            AppCompatImageButton call = findViewById(R.id.call_button);
            call.setOnClickListener(view1 -> dialPhoneNumber(placePhone));

            AppCompatImageButton website = findViewById(R.id.website_button);
            website.setOnClickListener(view12 -> openWebPage(placeWebsite));

            String mDetailPhotoUrl = intent.getStringExtra("placePhotoUrl");
            Glide.with(DetailImage.getContext())
                    .load(mDetailPhotoUrl)
                    .into(DetailImage);


            getPlaceDetail();
            fabOnClickListener();
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
            getUserDataFromFirebase(uid);
        }

        addSpotLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isLunchSpot) {
                    addLunchSpotInFirebase(placeId, uid);
                    addSpotLunch.setImageResource(R.drawable.ic_baseline_check_circle_24);
                    isLunchSpot = true;
                    Snackbar.make(view, "You're going to " + mDetailName + " for lunch!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    addLunchSpotInFirebase(null, uid);
                    addSpotLunch.setImageResource(R.drawable.ic_baseline_add_circle_24);
                    isLunchSpot = false;
                    Snackbar.make(view, "You're not going anymore to " + mDetailName + " for lunch!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }


    private void getPlaceDetail() {

    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void openWebPage(Uri url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //Add Lunch Spot
    private void addLunchSpotInFirebase(String lunchSpot, String uid) {
        UserHelper.updateLunchSpot(lunchSpot, uid).addOnFailureListener(this.onFailureListener());
    }


    private void getUserDataFromFirebase(String uid) {
        // 5 - Get additional data from Firestore
        UserHelper.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                assert currentUser != null;
                if (currentUser.getLunchSpot() != null) {
                    isLunchSpot = currentUser.getLunchSpot().equals(placeId);
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

}