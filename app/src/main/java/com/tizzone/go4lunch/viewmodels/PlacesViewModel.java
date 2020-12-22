package com.tizzone.go4lunch.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.repositories.PlacesRepository;

public class PlacesViewModel extends ViewModel {
    private PlacesRepository placesRepository = new PlacesRepository();;
    private MutableLiveData<PlacesResults> mutableLiveDataPlaces = new MutableLiveData<>();

    public void getNearByPlaces(String location, int radius, String type, String key) {
        placesRepository.getNearByPlaces(location, radius, type, key, new PlacesRepository.PlacesResultsInterface() {
            @Override
            public void onResponse(PlacesResults placesResults) {
                mutableLiveDataPlaces.setValue(placesResults);
            }
        });
    }

    public LiveData<PlacesResults> getPlacesResultsLiveData() {
        return mutableLiveDataPlaces;
    }
}
