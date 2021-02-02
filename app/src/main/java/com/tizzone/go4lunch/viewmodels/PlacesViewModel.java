package com.tizzone.go4lunch.viewmodels;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.detail.PlaceDetail;
import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.models.places.Result;
import com.tizzone.go4lunch.models.prediction.Prediction;
import com.tizzone.go4lunch.models.prediction.Predictions;
import com.tizzone.go4lunch.repositories.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;


@HiltViewModel
public class PlacesViewModel extends ViewModel {
    private static final String TAG = "RestaurantViewModel";

    private final Repository repository;
    public String location;
    public String key;
    public MutableLiveData<List<Restaurant>> restaurantsList = new MutableLiveData<>();
    private final MutableLiveData<List<Restaurant>> filteredRestaurants = new MutableLiveData<>();
    private final MutableLiveData<Restaurant> restaurantMutableLiveData = new MutableLiveData<>();

    @Inject
    public PlacesViewModel(Repository repository) {
        this.repository = repository;
    }


    public MutableLiveData<List<Restaurant>> getRestaurantsList() {
        if (restaurantsList == null) {
            restaurantsList = new MutableLiveData<List<Restaurant>>();
        }
        return restaurantsList;
    }

    public MutableLiveData<Restaurant> getRestaurant() {
        return restaurantMutableLiveData;
    }

    public MutableLiveData<List<Restaurant>> getFilteredRestaurantsList() {
        return filteredRestaurants;
    }

    public MutableLiveData<List<Restaurant>> setRestaurants(String location, int radius, String type, String key) {
        repository.getNearByPlacesApi(location, radius, type, key)
                .subscribeOn(Schedulers.io())
                .map(new Function<PlacesResults, List<Restaurant>>() {
                    @Override
                    public List<Restaurant> apply(PlacesResults placesResults) throws Throwable {
                        List<Result> placesResultsList = placesResults.getResults();
                        List<Restaurant> restaurants = new ArrayList<>();

                        for (Result result : placesResultsList) {
                            Boolean isOpen = null;
                            if (result.getOpeningHours() != null)
                                isOpen = result.getOpeningHours().getOpenNow();

                            Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(), result.getVicinity(), result.getPhotoUrl(), result.getRating(), 0,
                                    isOpen, new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()), null, null);
                            restaurants.add(restaurant);
                        }
                        Log.e(TAG, "apply: " + placesResultsList.get(0).getName());

                        return restaurants;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            restaurantsList.setValue(result);
                        },
                        error -> Log.e(TAG, "setRestaurants:" + error.getMessage())
                );
        return restaurantsList;
    }

    public MutableLiveData<Restaurant> setRestaurant(String uid, String fields, String key) {
        repository.getDetailByPlaceId(uid, fields, key)
                .subscribeOn(Schedulers.io())
                .map(new Function<PlaceDetail, Restaurant>() {
                    @Override
                    public Restaurant apply(PlaceDetail placeDetail) throws Throwable {
                        Log.e(TAG, "apply: " + placeDetail.getPlaceResult().getName());
                        Float rating = null;
                        if (placeDetail.getPlaceResult().getRating() != null) {
                            rating = placeDetail.getPlaceResult().getRating().floatValue();
                        }

                        Restaurant restaurant = new Restaurant(uid, placeDetail.getPlaceResult().getName(), placeDetail.getPlaceResult().getFormattedAddress(), placeDetail.getPlaceResult().getPhotoUrl(), rating, 0,
                                null, new LatLng(placeDetail.getPlaceResult().getGeometry().getLocation().getLat(), placeDetail.getPlaceResult().getGeometry().getLocation().getLng()), null, null);
                        return restaurant;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantMutableLiveData::setValue);
        return restaurantMutableLiveData;
    }

    public void setPredictions(String input, String location, int radius, int sessiontoken, String key) {
        repository.getPredictionsApi(input, location, radius, sessiontoken, key)
                .subscribeOn(Schedulers.io())
                .map(new Function<Predictions, List<Prediction>>() {
                    @Override
                    public List<Prediction> apply(Predictions apiPredictions) throws Throwable {
                        Log.e(TAG, "apply: " + apiPredictions.getPredictions().get(0).getPlaceId());
                        return apiPredictions.getPredictions();
                    }
                })
                .map(new Function<List<Prediction>, List<Restaurant>>() {
                    @Override
                    public List<Restaurant> apply(List<Prediction> predictions) throws Throwable {
                        List<Restaurant> filtered = new ArrayList<>();
                        List<Restaurant> restaurants = new ArrayList<>(Objects.requireNonNull(restaurantsList.getValue()));
                        if (predictions.size() > 0) {
                            for (Prediction prediction : predictions) {
                                String idPrediction = prediction.getPlaceId();
                                for (Restaurant restaurant : restaurants) {
                                    String idRestaurant = restaurant.getUid();
                                    if (idPrediction.matches(idRestaurant)) {
                                        filtered.add(restaurant);
                                    }
                                }
                            }
                        } else {
                            filtered.addAll(restaurants);
                        }

                        return filtered;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(filteredRestaurants::setValue,
                        error -> Log.e(TAG, "setPredictions:" + error.getMessage())
                );
    }


}
