package com.tizzone.go4lunch.models;


import androidx.annotation.Nullable;

import java.util.List;

public class User {

    public String uid;
    public boolean isAuthenticated;
    private String userName;
    @Nullable
    private String userEmail;
    @Nullable
    private String photoUrl;
    @Nullable
    private List favoriteRestaurants;
    @Nullable
    private String lunchSpot;


    public User() {
    }

    public User(String uid, String userName, String userEmail, String photoUrl, List<String> favoriteRestaurants, String lunchSpot) {
        this.uid = uid;
        this.userName = userName;
        this.userEmail = userEmail;
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

    public User setUserEmail(@Nullable String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    public void setFavoriteRestaurants(List<String> favoriteRestaurants) {
        this.favoriteRestaurants = favoriteRestaurants;
    }

    public String getLunchSpot() {
        return lunchSpot;
    }

    public void setLunchSpot(String lunchSpot) {
        this.lunchSpot = lunchSpot;
    }


}
