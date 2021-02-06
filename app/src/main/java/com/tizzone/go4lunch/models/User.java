package com.tizzone.go4lunch.models;


import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private String uid;
    private String userName;
    @Nullable
    private String userEmail;
    @Nullable
    private String photoUrl;
    @Nullable
    private List<String> favoriteRestaurants;
    @Nullable
    private String lunchSpot;

    public User() {

    }

    public User(String uid, String userName, @org.jetbrains.annotations.Nullable String userEmail, @org.jetbrains.annotations.Nullable String photoUrl, @org.jetbrains.annotations.Nullable List<String> favoriteRestaurants, String lunchSpot) {
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

    @org.jetbrains.annotations.Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(@org.jetbrains.annotations.Nullable String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @org.jetbrains.annotations.Nullable
    public List<String> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    public void setFavoriteRestaurants(@org.jetbrains.annotations.Nullable List<String> favoriteRestaurants) {
        this.favoriteRestaurants = favoriteRestaurants;
    }

    @org.jetbrains.annotations.Nullable
    public String getLunchSpot() {
        return lunchSpot;
    }

    public void setLunchSpot(@org.jetbrains.annotations.Nullable String lunchSpot) {
        this.lunchSpot = lunchSpot;
    }


}
