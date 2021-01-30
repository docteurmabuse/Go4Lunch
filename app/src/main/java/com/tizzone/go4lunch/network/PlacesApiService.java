package com.tizzone.go4lunch.network;

import com.tizzone.go4lunch.models.places.PlacesResults;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApiService {
    @GET("place/nearbysearch/json")
    Observable<PlacesResults> getNearByPlacesApi(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("key") String key
    );
}