package com.tizzone.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.PropertyChangeRegistry;

import com.google.android.gms.maps.model.LatLng;
import com.tizzone.go4lunch.BR;

import org.jetbrains.annotations.Nullable;

public class Restaurant extends BaseObservable implements Parcelable {
    private String uid;
    private int restaurant_counter;
    private String name;
    private String address;

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
    private final PropertyChangeRegistry registry = new PropertyChangeRegistry();
    private LatLng location;
    @Nullable
    private String photoUrl;
    @Nullable
    private Float rating;
    @Nullable
    private Boolean open_now;

    public Restaurant(String uid, String name, String address, @Nullable String photoUrl, @Nullable Float rating, int restaurant_counter, @Nullable Boolean open_now, LatLng location) {
        this.uid = uid;
        this.restaurant_counter = restaurant_counter;
        this.name = name;
        this.address = address;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.open_now = open_now;
        this.location = location;
    }

    protected Restaurant(Parcel in) {
        uid = in.readString();
        restaurant_counter = in.readInt();
        name = in.readString();
        address = in.readString();
        photoUrl = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readFloat();
        }
        location = in.readParcelable(LatLng.class.getClassLoader());
        byte tmpOpen_now = in.readByte();
        open_now = tmpOpen_now == 0 ? null : tmpOpen_now == 1;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Boolean isOpen_now() {
        return open_now;
    }

    public void setOpen_now(@Nullable Boolean open_now) {
        this.open_now = open_now;
    }

    public Restaurant() {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeInt(restaurant_counter);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(photoUrl);
        if (rating == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(rating);
        }
        parcel.writeParcelable(location, i);
        parcel.writeByte((byte) (open_now == null ? 0 : open_now ? 1 : 2));
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
