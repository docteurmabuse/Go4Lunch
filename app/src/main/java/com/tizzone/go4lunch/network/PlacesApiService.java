package com.tizzone.go4lunch.network;


import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.models.prediction.Predictions;

import io.reactivex.rxjava3.core.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApiService {
    @GET("place/nearbysearch/json")
    Flowable<PlacesResults> getNearByPlacesApi(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("key") String key
    );

    @GET("place/autocomplete/json?types=establishment&strictbounds")
    Flowable<Predictions> getPredictionsApi(
            @Query("input") String input,
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("sessiontoken ") int sessiontoken,
            @Query("key") String key
    );
}
