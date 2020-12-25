package com.tizzone.go4lunch.ui.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.models.places.Result;

import static com.tizzone.go4lunch.adapters.PlacesListAdapters.DETAIL_PLACE;

public class PlaceDetailActivity extends AppCompatActivity {
    private String mDetailAdress;
    private String mDetailName;
    private String mDetailPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_place_detail);
        Intent intent = this.getIntent();
        if(intent != null){
            setContentView(R.layout.activity_place_detail);
            Toolbar toolbar = findViewById(R.id.toolbar);
            AppBarLayout appbar = (AppBarLayout)findViewById(R.id.app_bar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ImageView DetailImage = findViewById(R.id.mDetailImage);
            TextView placeName =findViewById(R.id.place_name);
            TextView placeAddress =findViewById(R.id.place_address);

            mDetailPhotoUrl = intent.getStringExtra("placePhotoUrl");
            Glide.with(DetailImage.getContext())
                    .load(mDetailPhotoUrl)
                    .into(DetailImage);
            //  setSupportActionBar(toolbar);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mDetailName= intent.getStringExtra("placeName");
            placeName.setText(mDetailName);
            CollapsingToolbarLayout collapsingToolbar =
                    findViewById(R.id.toolbar_layout);
            // Set title of Detail page
            appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                    {
                        // Collapsed
                        collapsingToolbar.setTitle(mDetailName);
                    }
                    else
                    {
                        // Expanded
                        collapsingToolbar.setTitle("");
                    }
                }
            });
           // collapsingToolbar.setTitle(mDetailName);
            mDetailAdress= intent.getStringExtra("placeAddress");
            placeAddress.setText(mDetailAdress);
            getPlaceDetail();
            fabOnClickListener();
        }

    }
    private void fabOnClickListener() {

    }


    private void getPlaceDetail() {

    }
}