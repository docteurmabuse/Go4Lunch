package com.tizzone.go4lunch.utils;

import com.tizzone.go4lunch.api.PlacesService;
import com.tizzone.go4lunch.models.places.PlacesResults;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GoogleMapStreams {

    public static Observable<PlacesResults> getNearByPlaces(String location, int radius, String type, String key) {
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);
        return placesService.getNearByPlaces(location, radius, type, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
