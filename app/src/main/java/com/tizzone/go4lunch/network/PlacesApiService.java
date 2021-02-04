package com.tizzone.go4lunch.network;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tizzone.go4lunch.BuildConfig;
import com.tizzone.go4lunch.models.detail.PlaceDetail;
import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.models.prediction.Predictions;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApiService {
    String GOOGLE_MAP_API_KEY = BuildConfig.GOOGLE_MAPS_API_KEY;

    @GET("place/nearbysearch/json?type=restaurant&key=" + GOOGLE_MAP_API_KEY)
    Flowable<PlacesResults> getNearByPlacesApi(
            @Query("location") String location,
            @Query("radius") int radius
    );


    @GET("place/details/json?fields=name,formatted_address,photos,rating,international_phone_number,website,geometry&key=" + GOOGLE_MAP_API_KEY)
    Observable<PlaceDetail> getDetailByPlaceId(
            @Query("place_id") String placeId
    );

    @GET("place/autocomplete/json?types=establishment&strictbounds&key=" + GOOGLE_MAP_API_KEY)
    Flowable<Predictions> getPredictionsApi(
            @Query("input") String input,
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("sessiontoken ") int sessiontoken
    );

    String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

}
