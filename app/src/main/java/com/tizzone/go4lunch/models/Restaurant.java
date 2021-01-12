package com.tizzone.go4lunch.models;

public class Restaurant {
    private String uid;
    private String name;

    public Restaurant() {
    }

    public Restaurant(String uid, String name) {
        this.uid = uid;
        this.name = name;
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
