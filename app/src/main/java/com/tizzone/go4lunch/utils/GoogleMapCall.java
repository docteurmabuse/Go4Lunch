package com.tizzone.go4lunch.utils;

import com.tizzone.go4lunch.api.PlacesService;
import com.tizzone.go4lunch.models.places.PlacesResults;

import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleMapCall {

    //Start fetching places from Google Map NearBy
    public static void fetchPlacesNearBy(Callbacks callbacks, String location, int radius, String type, String key) {

        //Create Weak Reference to callback (avoid memory leak)
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<Callbacks>(callbacks);

        //Get a Retrofit instance and the related endpoints
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        //Create call on Google Map Api
        Call<PlacesResults> call = placesService.getNearByPlaces(location, radius, "restaurant", key);

        //Start the call
        call.enqueue(new Callback<PlacesResults>() {
            @Override
            public void onResponse(Call<PlacesResults> call, Response<PlacesResults> response) {
                //Call the proper callback using the controller
                if (callbacksWeakReference.get() != null)
                    callbacksWeakReference.get().onResponse(response.body());
            }

            @Override
            public void onFailure(Call<PlacesResults> call, Throwable t) {
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });

    }

    //Create callback
    public interface Callbacks {
        void onResponse(@Nullable PlacesResults results);

        void onFailure();
    }
}
