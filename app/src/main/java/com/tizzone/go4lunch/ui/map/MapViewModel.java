package com.tizzone.go4lunch.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.places.Result;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    private final String TAG = MapViewModel.class.getSimpleName();
    private MutableLiveData<List<Result>> mPlaces;
    private List<Result> placesList;

    LiveData<List<Result>> getPlacesList() {
        if (mPlaces == null) {
            mPlaces = new MutableLiveData<>();
            placesList = new ArrayList<>();
        }
        return mPlaces;
    }

    public MapViewModel() {
    }
}