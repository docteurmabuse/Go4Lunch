package com.tizzone.go4lunch.models.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tizzone.go4lunch.BuildConfig;
import com.tizzone.go4lunch.R;

import java.io.Serializable;
import java.util.List;

import static com.tizzone.go4lunch.utils.Constants.GOOGLE_PLACES_API_KEY;

public class Result implements Serializable {
    private final static long serialVersionUID = 3834918294238241150L;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("international_phone_number")
    @Expose
    private String internationalPhoneNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("obfuscated_type")
    @Expose
    private List<Object> obfuscatedType = null;
    private String photoUrl;

    /**
     * No args constructor for use in serialization
     */
    public Result() {
    }

    /**
     * @param obfuscatedType
     * @param website
     * @param formattedAddress
     * @param name
     * @param rating
     * @param geometry
     * @param internationalPhoneNumber
     * @param photos
     */
    public Result(String formattedAddress, Geometry geometry, String internationalPhoneNumber, String name, List<Object> obfuscatedType, List<Photo> photos, Double rating, String website) {
        super();
        this.formattedAddress = formattedAddress;
        this.geometry = geometry;
        this.internationalPhoneNumber = internationalPhoneNumber;
        this.name = name;
        this.obfuscatedType = obfuscatedType;
        this.photos = photos;
        this.rating = rating;
        this.website = website;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Result withFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
        return this;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Result withGeometry(Geometry geometry) {
        this.geometry = geometry;
        return this;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }

    public Result withInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Result withName(String name) {
        this.name = name;
        return this;
    }

    public List<Object> getObfuscatedType() {
        return obfuscatedType;
    }

    public void setObfuscatedType(List<Object> obfuscatedType) {
        this.obfuscatedType = obfuscatedType;
    }

    public Result withObfuscatedType(List<Object> obfuscatedType) {
        this.obfuscatedType = obfuscatedType;
        return this;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public Result withPhotos(List<Photo> photos) {
        this.photos = photos;
        return this;
    }


    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getPhotoUrl() {
        String GOOGLE_MAP_API_KEY = BuildConfig.GOOGLE_MAPS_API_KEY;
        if (this.photos != null) {
            String staticUrl = "https://maps.googleapis.com/maps/api/place/photo?";
            photoUrl = staticUrl + "maxwidth=400&photoreference=" + getPhotos().get(0).getPhotoReference() + "&key=" + GOOGLE_PLACES_API_KEY;
        } else {
            photoUrl = String.valueOf(R.drawable.ic_logo_go4lunch);
        }
        return photoUrl;
    }

    public Float getRating() {
        if (rating != null) {
            float ratingFiveStarFloat = rating.floatValue();
            return (ratingFiveStarFloat * 3) / 5;
        } else {
            rating = 1.5;
            return rating.floatValue();
        }
    }

    public Result withRating(Double rating) {
        this.rating = rating;
        return this;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Result withWebsite(String website) {
        this.website = website;
        return this;
    }

}
