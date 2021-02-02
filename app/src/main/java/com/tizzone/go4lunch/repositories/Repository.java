package com.tizzone.go4lunch.repositories;

import com.tizzone.go4lunch.models.detail.PlaceDetail;
import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.models.prediction.Predictions;
import com.tizzone.go4lunch.network.PlacesApiService;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;


public class Repository {

    private final PlacesApiService placesApiService;

    @Inject
    public Repository(PlacesApiService placesApiService) {

        this.placesApiService = placesApiService;

    }

    public Flowable<PlacesResults> getNearByPlacesApi(String location, int radius, String type) {
        return placesApiService.getNearByPlacesApi(location, radius, type);
    }

    public Flowable<PlaceDetail> getDetailByPlaceId(String uid) {
        return placesApiService.getDetailByPlaceId(uid);
    }

    public Flowable<Predictions> getPredictionsApi(String input, String location, int radius, int sessiontoken) {
        return placesApiService.getPredictionsApi(input, location, radius, sessiontoken);
    }
}

