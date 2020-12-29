package com.tizzone.go4lunch.ui.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    private String mDetailAdress;
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
        TextView placeName = findViewById(R.id.detail_place_name);

        //setContentView(R.layout.activity_place_detail);
        Intent intent = this.getIntent();
        if (intent != null) {
            // Specify the fields to return.
            final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

            // Construct a request object, passing the place ID and fields array.
            placeId = intent.getStringExtra("placeId");
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
            PlacesClient placesClient = Places.createClient(this);
            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                placePhone= place.getPhoneNumber();
                placeWebsite = place.getWebsiteUri();
                mDetailName = place.getName();
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

            setContentView(R.layout.activity_place_detail);
            Toolbar toolbar = findViewById(R.id.detail_toolbar);
            CollapsingToolbarLayout collapsingToolbar =
                    findViewById(R.id.toolbar_layout);
            AppBarLayout appbar = findViewById(R.id.app_bar_detail);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ImageView DetailImage = findViewById(R.id.mDetailImage);
            TextView placeAddress = findViewById(R.id.place_address);
            TextView placesDetailsTitle = findViewById(R.id.place_details_title);
            TextView placesDetailsAddress = findViewById(R.id.place_details_address);

            AppCompatImageButton call = findViewById(R.id.call_button);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + placePhone));
                    startActivity(callIntent);
                }
            });

            getPlaceDetail();
            mDetailPhotoUrl = intent.getStringExtra("placePhotoUrl");
            Glide.with(DetailImage.getContext())
                    .load(mDetailPhotoUrl)
                    .into(DetailImage);

          //  mDetailName = intent.getStringExtra("placeName");
            placeName.setText(mDetailName);

            // Set title of Detail page
            appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                        // Collapsed
                        // collapsingToolbar.setCollapsedTitleTextColor(0xffffff);
                        collapsingToolbar.setTitle(intent.getStringExtra("placeName"));
                        toolbar.setTitle(intent.getStringExtra("placeName"));
                        toolbar.setSubtitle(intent.getStringExtra("placeAddress"));
                        placesDetailsTitle.setText(intent.getStringExtra("placeName"));
                        placesDetailsAddress.setText(intent.getStringExtra("placeAddress"));
                        placeAddress.setVisibility(View.INVISIBLE);
                        placeName.setVisibility(View.INVISIBLE);
                        findViewById(R.id.detail_title_layout).setVisibility(View.GONE);
                    } else {
                        // Expanded
                        collapsingToolbar.setTitle("");
                        toolbar.setTitle(intent.getStringExtra("placeAddress"));
                        mDetailAdress = intent.getStringExtra("placeAddress");
                        placeAddress.setText(mDetailAdress);
                        placeAddress.setVisibility(View.VISIBLE);
                        placeName.setVisibility(View.VISIBLE);
                        findViewById(R.id.detail_title_layout).setVisibility(View.VISIBLE);
                        placesDetailsTitle.setVisibility(View.INVISIBLE);
                        placesDetailsAddress.setVisibility(View.INVISIBLE);

                    }
                }
            });
            // collapsingToolbar.setTitle(mDetailName);

            getPlaceDetail();
            fabOnClickListener();
        }

    }

    private void fabOnClickListener() {

    }


    private void getPlaceDetail() {

    }
}