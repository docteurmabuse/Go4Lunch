package com.tizzone.go4lunch.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.PropertyChangeRegistry;

import com.google.android.gms.maps.model.LatLng;
import com.tizzone.go4lunch.BR;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public class Restaurant extends BaseObservable implements Serializable {
    private String uid;
    private int restaurant_counter;
    private String name;
    private String address;

    private final PropertyChangeRegistry registry = new PropertyChangeRegistry();
    @Nullable
    private Double latitude;
    @Nullable
    private Double longitude;
    @Nullable
    private String photoUrl;
    @Nullable
    private Float rating;
    @Nullable
    private Boolean open_now;
    @Nullable
    private String websiteUrl;
    @Nullable
    private String phone;


    public Restaurant(String uid, String name, String address, @Nullable String photoUrl, @Nullable Float rating, int restaurant_counter, @Nullable Boolean open_now, @Nullable
            Double latitude, Double longitude, String websiteUrl, String phone) {
        this.uid = uid;
        this.restaurant_counter = restaurant_counter;
        this.name = name;
        this.address = address;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.open_now = open_now;
        this.latitude = latitude;
        this.longitude = longitude;
        this.websiteUrl = websiteUrl;
        this.phone = phone;
    }


    public Restaurant() {

    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setOpen_now(@Nullable Boolean open_now) {
        this.open_now = open_now;
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        registry.notifyChange(this, BR.address);
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(@Nullable String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public float getRating() {
        float ratingFiveStar;
        if (rating != null) {
            ratingFiveStar = rating;
        } else {
            ratingFiveStar = (float) 1.5;
        }
        rating = ((ratingFiveStar * 3) / 5);
        return rating;
    }

    public void setRating(@Nullable Float rating) {
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

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        registry.notifyChange(this, BR.name);
    }

    @Nullable
    public Boolean getOpen_now() {
        return open_now;
    }

    @Nullable
    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(@Nullable String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }


    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.remove(callback);
    }

}
