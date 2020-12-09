package com.tizzone.go4lunch.api;

import com.tizzone.go4lunch.models.places.GMapPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapAPI {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyBK_IN5GbLg77wSfRKVx1qrJHOVc2Tdv5g")
    Call<GMapPlaces> getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);
}
