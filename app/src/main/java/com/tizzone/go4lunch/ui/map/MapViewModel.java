package com.tizzone.go4lunch.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.places.PlacesResults;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapViewModel extends ViewModel {
    private final SavedStateHandle savedStateHandle;

    private final MutableLiveData<PlacesResults> mutableLiveDataPlaces = new MutableLiveData<>();

    @Inject
    public MapViewModel(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
    }

    public void setMutableLiveDataPlaces(PlacesResults placesResults) {
        mutableLiveDataPlaces.setValue(placesResults);
    }

    public LiveData<PlacesResults> getDataPlaces() {
        return mutableLiveDataPlaces;
    }
}