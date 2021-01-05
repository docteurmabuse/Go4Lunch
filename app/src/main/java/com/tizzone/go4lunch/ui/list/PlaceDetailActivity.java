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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.api.UserHelper;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityPlaceDetailBinding;

import java.util.Arrays;
import java.util.List;

public class PlaceDetailActivity extends BaseActivity {
    private static final String TAG = "1543";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 5873;
    private String mDetailAddress;
    private String mDetailName;
    private String mDetailPhotoUrl;
    private int lunchSpot;
    private ActivityPlaceDetailBinding placeDetailBinding;
    private String placePhone;
    private Uri placeWebsite;

    // Define a Place ID.
    private String placeId;
    private PlacesClient placesClient;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        placeDetailBinding = ActivityPlaceDetailBinding.inflate(getLayoutInflater());
        View view = placeDetailBinding.getRoot();
        setContentView(view);
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(this);

        //setContentView(R.layout.activity_place_detail);
        Toolbar toolbar = placeDetailBinding.detailToolbar;
        CollapsingToolbarLayout collapsingToolbar = placeDetailBinding.toolbarLayout;
        AppBarLayout appbar = placeDetailBinding.appBarDetail;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ImageView DetailImage = placeDetailBinding.mDetailImage;
        TextView placeName = findViewById(R.id.detail_place_name);
        TextView placeAddress = placeDetailBinding.placeDetailsAddress;
        TextView placesDetailsTitle = placeDetailBinding.placeDetailsTitle;
        TextView placesDetailsAddress = placeDetailBinding.placeDetailsAddress;


        Intent intent = this.getIntent();
        if (intent != null) {
            // Specify the fields to return.
            final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.WEBSITE_URI, Place.Field.PHONE_NUMBER);

            // Construct a request object, passing the place ID and fields array.
            placeId = intent.getStringExtra("placeId");

            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                placePhone = place.getPhoneNumber();
                placeWebsite = place.getWebsiteUri();
                mDetailName = place.getName();
                mDetailAddress = place.getAddress();
                Log.i(TAG, "Place found: " + place.getName());
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });


            AppCompatImageButton call = findViewById(R.id.call_button);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialPhoneNumber(placePhone);
                }
            });

            AppCompatImageButton website = findViewById(R.id.website_button);
            website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWebPage(placeWebsite);
                }
            });

            getPlaceDetail();
            mDetailPhotoUrl = intent.getStringExtra("placePhotoUrl");
            Glide.with(DetailImage.getContext())
                    .load(mDetailPhotoUrl)
                    .into(DetailImage);

            // Set title of Detail page
            appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                        // Collapsed
                        // collapsingToolbar.setCollapsedTitleTextColor(0xffffff);
                        collapsingToolbar.setTitle("hjello");
                        getSupportActionBar().setSubtitle("sairam");

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
                        toolbar.setTitle(intent.getStringExtra("placeAddress"));
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

            getPlaceDetail();
            fabOnClickListener();
        }

    }

    private void fabOnClickListener() {
        FloatingActionButton addSpotLunch = placeDetailBinding.addSpotLunchButton;
        addSpotLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a click", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                addLunchSpotInFirebase(placeId);
            }
        });
    }


    private void getPlaceDetail() {

    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
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
    private void addLunchSpotInFirebase(String lunchSpot) {
        if (this.getCurrentUser() != null) {
            String uid = this.getCurrentUser().getUid();
            UserHelper.updateLunchSpot(lunchSpot, uid).addOnFailureListener(this.onFailureListener());
        }
    }

    private void addLunchSpotInFirebaseInFirestore() {

        if (this.getCurrentUser() != null) {
            String uid = this.getCurrentUser().getUid();
            UserHelper.updateIsAuthenticated(true, uid).addOnFailureListener(this.onFailureListener());
        }
    }


}