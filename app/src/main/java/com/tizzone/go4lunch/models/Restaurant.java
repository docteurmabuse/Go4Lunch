package com.tizzone.go4lunch.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String uid;
    private int restaurant_counter;
    private String name;
    private String address;
    private String photoUrl;
    private float rating;

    private LatLng location;
    private boolean open_now;

    public Restaurant(String uid, String name, String address, String photoUrl, float rating, int restaurant_counter, boolean open_now, LatLng location) {
        this.uid = uid;
        this.restaurant_counter = restaurant_counter;
        this.name = name;
        this.address = address;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.open_now = open_now;
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public boolean isOpen_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    public Restaurant() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRestaurant_counter() {
        return restaurant_counter;
    }

    public void setRestaurant_counter(int restaurant_counter) {
        this.restaurant_counter = restaurant_counter;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
