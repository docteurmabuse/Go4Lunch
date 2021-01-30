package com.tizzone.go4lunch.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.tizzone.go4lunch.models.LocationModel;

public class LocationViewModel extends ViewModel {
    public MutableLiveData<LocationModel> userLocation = new MutableLiveData<LocationModel>();
    public MutableLiveData<String> userId;


    public void setUserLocation(double latitude, double longitude) {
        userLocation.postValue(new LocationModel(new LatLng(latitude, longitude)));
    }

    public LiveData<String> getUserId() {
        return userId;
    }

    public LiveData<LocationModel> getUserLocation() {
        return userLocation;
    }

    public void setUserId(String uid) {
        userId = new MutableLiveData<String>();
        userId.postValue(uid);
    }

}
