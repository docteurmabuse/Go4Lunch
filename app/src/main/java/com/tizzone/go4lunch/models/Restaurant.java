package com.tizzone.go4lunch.models;

public class Restaurant {
    private String uid;
    private int restaurant_counter;
    private String name;

    public Restaurant(String uid, String name, int restaurant_counter) {
        this.uid = uid;
        this.name = name;
        this.restaurant_counter = restaurant_counter;
    }

    public Restaurant() {
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
