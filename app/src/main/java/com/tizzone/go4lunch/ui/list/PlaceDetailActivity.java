package com.tizzone.go4lunch.ui.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.models.places.Result;

import static com.tizzone.go4lunch.adapters.PlacesListAdapters.DETAIL_PLACE;

public class PlaceDetailActivity extends AppCompatActivity {
    private Result place;
    private String mDetailName;
    private String mDetailPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_place_detail);
        Bundle bundle = this.getIntent().getExtras();
        Result place = null;
        if(bundle != null){
            place= (Result) bundle.getSerializable(DETAIL_PLACE);
        }
        if (place != null) {
            populateViews();
            getPlaceDetail();
            fabOnClickListener();
        }
    }
    private void fabOnClickListener() {

    }

    private void populateViews() {
        setContentView(R.layout.activity_place_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);

        ImageView DetailImage = findViewById(R.id.mDetailImage);
        Glide.with(DetailImage.getContext())
                .load(mDetailPhotoUrl)
                .into(DetailImage);
      //  setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar =
                findViewById(R.id.toolbar_layout);
        // Set title of Detail page
        collapsingToolbar.setTitle(mDetailName);

    }

    private void getPlaceDetail() {
        mDetailName = place.getName();
        String key = getText(R.string.google_maps_key).toString();
        String staticUrl = "https://maps.googleapis.com/maps/api/place/photo?";
        mDetailPhotoUrl = staticUrl + "maxwidth=400&photoreference=" + place.getPhotos().get(0).getPhotoReference() + "&key=" + key;

    }
}