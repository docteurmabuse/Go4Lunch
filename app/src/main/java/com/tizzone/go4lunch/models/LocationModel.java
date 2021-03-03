package com.tizzone.go4lunch.models;

import com.google.android.gms.maps.model.LatLng;

public class LocationModel {
    private LatLng location;

    public LocationModel() {
    }

    public LocationModel(LatLng location) {
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        if (location != null) {
            this.location = location;
        }
    }
}
