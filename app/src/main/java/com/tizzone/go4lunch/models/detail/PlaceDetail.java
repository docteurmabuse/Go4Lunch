package com.tizzone.go4lunch.models.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceDetail {
    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("placeResult")
    @Expose
    private PlaceResult placeResult;

    public PlaceDetail(PlaceResult placeResult) {
        this.placeResult = placeResult;
    }

    @SerializedName("status")
    @Expose
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public PlaceResult getPlaceResult() {
        return placeResult;
    }

    public void setPlaceResult(PlaceResult placeResult) {
        this.placeResult = placeResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
