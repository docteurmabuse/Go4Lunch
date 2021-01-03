package com.tizzone.go4lunch.models.user;

import java.lang.reflect.Array;

public class User {

    private int id;
    private String lastName;
    private String photoUrl;
    private Array favoriteRestaurants;
    private int lunchSpot;

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
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
