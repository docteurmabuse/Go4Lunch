package com.tizzone.go4lunch.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.repositories.PlacesRepository;

public class PlacesViewModel extends ViewModel {
    private PlacesRepository placesRepository;
    private LiveData<PlacesResults> placesResultsLiveData;

    public PlacesViewModel(@NonNull Application application) {
        super();
    }

    public void init() {
        placesRepository = new PlacesRepository();
        placesResultsLiveData = placesRepository.getPlacesResultsLiveData();
    }

    public void getNearByPlaces(String location, int radius, String type, String key) {
        placesRepository.getNearByPlaces(location, radius, type, key);
    }

    public LiveData<PlacesResults> getPlacesResultsLiveData() {
        return placesResultsLiveData;
    }
}
