package com.tizzone.go4lunch.models.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlaceDetail implements Serializable {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    private final static long serialVersionUID = -5906451652049202653L;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private Result result;

    /**
     * No args constructor for use in serialization
     */
    public PlaceDetail() {
    }

    /**
     * @param result
     * @param htmlAttributions
     * @param status
     */
    public PlaceDetail(List<Object> htmlAttributions, Result result, String status) {
        super();
        this.htmlAttributions = htmlAttributions;
        this.result = result;
        this.status = status;
    }

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public PlaceDetail withHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
        return this;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public PlaceDetail withResult(Result result) {
        this.result = result;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PlaceDetail withStatus(String status) {
        this.status = status;
        return this;
    }

}
