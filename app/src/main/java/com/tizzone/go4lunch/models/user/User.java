package com.tizzone.go4lunch.models.user;

import com.google.firebase.database.annotations.Nullable;

import java.lang.reflect.Array;

public class User {

    public String uid;
    public boolean isAuthenticated;
    private String userName;
    @Nullable private String photoUrl;
    @Nullable private Array favoriteRestaurants;
    @Nullable private int lunchSpot;

    public User(String uid, boolean isAuthenticated, String userName, String photoUrl, Array favoriteRestaurants, int lunchSpot) {
        this.uid = uid;
        this.isAuthenticated = isAuthenticated;
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.favoriteRestaurants = favoriteRestaurants;
        this.lunchSpot = lunchSpot;
    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }




    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public User setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }

    public Array getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    public User setFavoriteRestaurants(Array favoriteRestaurants) {
        this.favoriteRestaurants = favoriteRestaurants;
        return this;
    }

    public int getLunchSpot() {
        return lunchSpot;
    }

    public User setLunchSpot(int lunchSpot) {
        this.lunchSpot = lunchSpot;
        return this;
    }



}
