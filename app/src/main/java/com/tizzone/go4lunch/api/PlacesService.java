package com.tizzone.go4lunch.api;

import com.tizzone.go4lunch.models.places.PlacesResults;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesService {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("place/nearbysearch/json")
    Call<PlacesResults> getNearByPlaces(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("key") String key
    );


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
