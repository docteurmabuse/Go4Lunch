package com.tizzone.go4lunch.repositories;

import com.tizzone.go4lunch.models.detail.PlaceDetail;
import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.models.prediction.Predictions;
import com.tizzone.go4lunch.network.PlacesApiService;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;


public class PlaceRepository {

    private final PlacesApiService placesApiService;

    @Inject
    public PlaceRepository(PlacesApiService placesApiService) {

        this.placesApiService = placesApiService;

    }

    public Flowable<PlacesResults> getNearByPlacesApi(String location, int radius) {
        return placesApiService.getNearByPlacesApi(location, radius);
    }

    public Observable<PlaceDetail> getDetailByPlaceId(String uid) {
        return placesApiService.getDetailByPlaceId(uid);
    }

    public Flowable<Predictions> getPredictionsApi(String input, String location, int radius, int sessiontoken) {
        return placesApiService.getPredictionsApi(input, location, radius, sessiontoken);
    }
}

