package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.AutocompletePrediction;

public class PlaceAutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {
    public PlaceAutoCompleteAdapter(@NonNull Context context, int resource, @NonNull AutocompletePrediction[] objects) {
        super(context, resource, objects);
    }
//    private static final String TAG = "PlaceArrayAdapter";
//    private final PlacesClient placesClient;
//    private RectangularBounds mBounds;
//    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();
//    public Context context;
}
