package com.tizzone.go4lunch.ui.list;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.ActivityPlaceDetailBinding;
import com.tizzone.go4lunch.models.places.Result;

import static com.tizzone.go4lunch.adapters.PlacesListAdapters.DETAIL_PLACE;

public class PlaceDetailActivity extends AppCompatActivity {
    private String mDetailAdress;
    private String mDetailName;
    private String mDetailPhotoUrl;
    private ActivityPlaceDetailBinding placeDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeDetailBinding = ActivityPlaceDetailBinding.inflate(getLayoutInflater());
        View view = placeDetailBinding.getRoot();
        setContentView(view);
        //setContentView(R.layout.activity_place_detail);
        Intent intent = this.getIntent();
        if(intent != null){
            setContentView(R.layout.activity_place_detail);
            Toolbar toolbar = findViewById(R.id.detail_toolbar);
            CollapsingToolbarLayout collapsingToolbar =
                    findViewById(R.id.toolbar_layout);
            AppBarLayout appbar = findViewById (R.id.app_bar_detail);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ImageView DetailImage = findViewById(R.id.mDetailImage);
            TextView placeName =findViewById(R.id.place_name);
            TextView placeAddress =findViewById(R.id.place_address);
            TextView placesDetailsTitle = findViewById(R.id.place_details_title);
            TextView placesDetailsAddress = findViewById(R.id.place_details_address);

            mDetailPhotoUrl = intent.getStringExtra("placePhotoUrl");
            Glide.with(DetailImage.getContext())
                    .load(mDetailPhotoUrl)
                    .into(DetailImage);

            mDetailName= intent.getStringExtra("placeName");
            placeName.setText(mDetailName);

            // Set title of Detail page
            appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                    {
                        // Collapsed
                        collapsingToolbar.setTitle(intent.getStringExtra("placeName"));
                        toolbar.setTitle(intent.getStringExtra("placeAddress"));
                        toolbar.setSubtitle(intent.getStringExtra("placeAddress"));
                        placesDetailsTitle.setText(intent.getStringExtra("placeName"));
                        placesDetailsAddress.setText(intent.getStringExtra("placeAddress"));
                        placeAddress.setVisibility(View.INVISIBLE);
                        placeName.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        // Expanded
                        collapsingToolbar.setTitle("");
                        toolbar.setTitle(intent.getStringExtra("placeAddress"));
                        mDetailAdress= intent.getStringExtra("placeAddress");
                        placeAddress.setText(mDetailAdress);
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