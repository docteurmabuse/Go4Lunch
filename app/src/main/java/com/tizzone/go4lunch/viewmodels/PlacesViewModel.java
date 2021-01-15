package com.tizzone.go4lunch.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.models.places.Result;
import com.tizzone.go4lunch.repositories.PlacesRepository;

public class PlacesViewModel extends ViewModel {
    private PlacesRepository placesRepository;
    private MutableLiveData<PlacesResults> mutableLiveDataPlaces;
    private MutableLiveData<Result> mutableLiveDataUserLocation;


    public void init() {
        placesRepository = new PlacesRepository();

    }

    public void getNearByPlaces(String location, int radius, String type, String key) {
        placesRepository.getNearByPlaces(location, radius, type, key, new PlacesRepository.PlacesResultsInterface() {
            @Override
            public void onResponse(PlacesResults placesResults) {
                mutableLiveDataPlaces.setValue(placesResults);
            }
        });
    }

    public LiveData<PlacesResults> getPlacesResultsLiveData() {
        if (mutableLiveDataPlaces == null) {
            mutableLiveDataPlaces = new MutableLiveData<PlacesResults>();
        }
        return mutableLiveDataPlaces;
    }

    public LiveData<Result> getUserLocation() {
        if (mutableLiveDataUserLocation == null) {
            mutableLiveDataUserLocation = new MutableLiveData<Result>();
        }
        return mutableLiveDataUserLocation;
    }

}
