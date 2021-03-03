package com.tizzone.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.PropertyChangeRegistry;

import com.google.android.gms.maps.model.LatLng;
import com.tizzone.go4lunch.BR;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

import static com.tizzone.go4lunch.utils.Utils.transformFiveStarsIntoThree;


public class Restaurant extends BaseObservable implements Serializable, Parcelable {
    private String uid;
    private int restaurant_counter;
    private String name;
    private String address;

    private final PropertyChangeRegistry registry = new PropertyChangeRegistry();
    @Nullable
    private Double latitude;
    @Nullable
    private Double longitude;
    @Nullable
    private String photoUrl;
    @Nullable
    private Float rating;
    @Nullable
    private Boolean open_now;
    @Nullable
    private String websiteUrl;
    @Nullable
    private String phone;


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


    public Restaurant() {

    }

    public Restaurant(String uid, String name, String address, @Nullable String photoUrl, @Nullable Float rating, int restaurant_counter, @Nullable Boolean open_now, @Nullable
            Double latitude, @Nullable Double longitude, @Nullable String websiteUrl, @Nullable String phone) {
        this.uid = uid;
        this.restaurant_counter = restaurant_counter;
        this.name = name;
        this.address = address;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.open_now = open_now;
        this.latitude = latitude;
        this.longitude = longitude;
        this.websiteUrl = websiteUrl;
        this.phone = phone;
    }

    protected Restaurant(Parcel in) {
        uid = in.readString();
        restaurant_counter = in.readInt();
        name = in.readString();
        address = in.readString();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        photoUrl = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readFloat();
        }
        byte tmpOpen_now = in.readByte();
        open_now = tmpOpen_now == 0 ? null : tmpOpen_now == 1;
        websiteUrl = in.readString();
        phone = in.readString();
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setOpen_now(@Nullable Boolean open_now) {
        this.open_now = open_now;
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

    @Bindable
    public float getRating() {
        float ratingFiveStar;
        if (rating != null) {
            ratingFiveStar = rating;
        } else {
            ratingFiveStar = (float) 1.5;
        }
        rating = transformFiveStarsIntoThree(ratingFiveStar);
        return rating;
    }

    public void setRating(@Nullable Float rating) {
        this.rating = rating;
    }

    @Bindable

    public int getRestaurant_counter() {
        return restaurant_counter;
    }

    @Bindable
    public void setRestaurant_counter(int restaurant_counter) {
        this.restaurant_counter = restaurant_counter;
    }

    @Bindable
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

    @Nullable
    public Boolean getOpen_now() {
        return open_now;
    }

    @Nullable
    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(@Nullable String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }


    @Override
    public void addOnPropertyChangedCallback(@NotNull OnPropertyChangedCallback callback) {
        registry.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(@NotNull OnPropertyChangedCallback callback) {
        registry.remove(callback);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeInt(restaurant_counter);
        dest.writeString(name);
        dest.writeString(address);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        dest.writeString(photoUrl);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(rating);
        }
        dest.writeByte((byte) (open_now == null ? 0 : open_now ? 1 : 2));
        dest.writeString(websiteUrl);
        dest.writeString(phone);
    }
}
