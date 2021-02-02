package com.tizzone.go4lunch.network;


import com.tizzone.go4lunch.BuildConfig;
import com.tizzone.go4lunch.models.detail.PlaceDetail;
import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.models.prediction.Predictions;

import io.reactivex.rxjava3.core.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApiService {
    String GOOGLE_MAP_API_KEY = BuildConfig.GOOGLE_MAPS_API_KEY;

    @GET("place/nearbysearch/json&key=" + GOOGLE_MAP_API_KEY)
    Flowable<PlacesResults> getNearByPlacesApi(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type
    );


    @GET("place/details/json?fields=name,formatted_address,photos,rating,international_phone_number,website,geometry&key=" + GOOGLE_MAP_API_KEY)
    Flowable<PlaceDetail> getDetailByPlaceId(
            @Query("place_id") String placeId
    );

    @GET("place/autocomplete/json?types=establishment&strictbounds&key=" + GOOGLE_MAP_API_KEY)
    Flowable<Predictions> getPredictionsApi(
            @Query("input") String input,
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("sessiontoken ") int sessiontoken
    );

}
