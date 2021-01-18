package com.tizzone.go4lunch.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.places.PlacesResults;

public class MapViewModel extends ViewModel {

    private final MutableLiveData<PlacesResults> mutableLiveDataPlaces = new MutableLiveData<>();
    public void setMutableLiveDataPlaces(PlacesResults placesResults){
        mutableLiveDataPlaces.setValue(placesResults);
    }
    public LiveData<PlacesResults> getDataPlaces(){
        return mutableLiveDataPlaces;
    }
}