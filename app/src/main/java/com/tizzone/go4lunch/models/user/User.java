package com.tizzone.go4lunch.models.user;

import com.google.firebase.database.annotations.Nullable;

import java.lang.reflect.Array;

public class User {

    public String uid;
    public boolean isAuthenticated;
    private String lastName;
    @Nullable private String photoUrl;
    private Array favoriteRestaurants;
    private int lunchSpot;

    public User(String uid, boolean isAuthenticated, String lastName, String photoUrl, Array favoriteRestaurants, int lunchSpot, String firstName) {
        this.uid = uid;
        this.isAuthenticated = isAuthenticated;
        this.lastName = lastName;
        this.photoUrl = photoUrl;
        this.favoriteRestaurants = favoriteRestaurants;
        this.lunchSpot = lunchSpot;
        this.firstName = firstName;
    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }


    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
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
