package com.tizzone.go4lunch.models;


import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;
import java.util.List;

public class User extends BaseObservable implements Serializable {

    private String uid;
    private String userName;
    private String userEmail;
    @Nullable
    private String photoUrl;
    @Nullable
    private List<String> favoriteRestaurants;
    @Nullable
    private String lunchSpot;

    public User() {

    }

    public User(String uid, String userEmail, String userName, @Nullable String photoUrl, @Nullable List<String> favoriteRestaurants, @Nullable String lunchSpot) {
        this.uid = uid;
        this.userEmail = userEmail;
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.favoriteRestaurants = favoriteRestaurants;
        this.lunchSpot = lunchSpot;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Nullable
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(@Nullable String userEmail) {
        this.userEmail = userEmail;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(@org.jetbrains.annotations.Nullable String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Bindable
    @Nullable
    public List<String> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    public void setFavoriteRestaurants(@Nullable List<String> favoriteRestaurants) {
        this.favoriteRestaurants = favoriteRestaurants;
    }

    @Bindable
    @Nullable
    public String getLunchSpot() {
        return lunchSpot;
    }

    public void setLunchSpot(@Nullable String lunchSpot) {
        this.lunchSpot = lunchSpot;
    }


}
