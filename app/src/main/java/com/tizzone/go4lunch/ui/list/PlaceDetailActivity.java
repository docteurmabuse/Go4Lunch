package com.tizzone.go4lunch.ui.list;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.ActivityPlaceDetailBinding;

import java.util.Arrays;
import java.util.List;

public class PlaceDetailActivity extends AppCompatActivity {
    private static final String TAG = "1543";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 5873;
    private String mDetailAddress;
    private String mDetailName;
    private String mDetailPhotoUrl;
    private ActivityPlaceDetailBinding placeDetailBinding;
    private String placePhone;
    private Uri placeWebsite;

    // Define a Place ID.
    private String placeId;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeDetailBinding = ActivityPlaceDetailBinding.inflate(getLayoutInflater());
        View view = placeDetailBinding.getRoot();
        setContentView(view);
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(this);
        //setContentView(R.layout.activity_place_detail);

        setContentView(R.layout.activity_place_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);
        AppBarLayout appbar = findViewById(R.id.app_bar_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView DetailImage = findViewById(R.id.mDetailImage);
        TextView placeName = findViewById(R.id.detail_place_name);
        TextView placeAddress = findViewById(R.id.detail_place_address);
        TextView placesDetailsTitle = findViewById(R.id.place_details_title);
        TextView placesDetailsAddress = findViewById(R.id.place_details_address);


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
}