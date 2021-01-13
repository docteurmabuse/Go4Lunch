package com.tizzone.go4lunch.models;

public class Restaurant {
    private String uid;

    private int restaurant_counter;
    private String name;

    public Restaurant(String uid, String name) {
        this.uid = uid;
        this.name = name;
        this.restaurant_counter = restaurant_counter;
    }

    public Restaurant() {
    }

    public int getRestaurant_counter() {
        return restaurant_counter;
    }

    public Restaurant setRestaurant_counter(int restaurant_counter) {
        this.restaurant_counter = restaurant_counter;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public Restaurant setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getName() {
        return name;
    }

    public Restaurant setName(String name) {
        this.name = name;
        return this;
    }
}
