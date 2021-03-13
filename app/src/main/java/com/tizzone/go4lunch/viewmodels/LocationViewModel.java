package com.tizzone.go4lunch.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.tizzone.go4lunch.models.LocationModel;

public class LocationViewModel extends ViewModel {
    public final MutableLiveData<LocationModel> userLocation = new MutableLiveData<>();

    public void setUserLocation(double latitude, double longitude) {
        userLocation.postValue(new LocationModel(new LatLng(latitude, longitude)));
    }

    public LiveData<LocationModel> getUserLocation() {
        return userLocation;
    }

}
