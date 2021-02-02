package com.tizzone.go4lunch.models.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tizzone.go4lunch.R;

import java.util.List;

public class PlaceResult {

    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("international_phone_number")
    @Expose
    private String internationalPhoneNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("website")
    @Expose
    private String website;
    private String photoUrl;


    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getPhotoUrl() {
        if (this.photos.size() > 0) {
            String staticUrl = "https://maps.googleapis.com/maps/api/place/photo?";
            photoUrl = staticUrl + "maxwidth=400&photoreference=" + getPhotos().get(0).getPhotoReference() + "&key=";
        } else {
            photoUrl = String.valueOf(R.drawable.ic_logo_go4lunch);
        }
        return photoUrl;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
