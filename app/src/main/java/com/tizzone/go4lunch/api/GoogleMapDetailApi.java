package com.tizzone.go4lunch.api;

import com.tizzone.go4lunch.models.places.PlacesResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapDetailApi {
    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("place/details/json?")
    Call<PlacesResults> getDetailByPlaceId(
            @Query("place_id") String placeId,
            @Query("fields") String fields,
            @Query("key") String key
    );

}
