package com.tizzone.go4lunch.viewmodels;


import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.detail.Result;
import com.tizzone.go4lunch.models.prediction.Prediction;
import com.tizzone.go4lunch.repositories.PlaceRepository;
import com.tizzone.go4lunch.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


@HiltViewModel
public class PlacesViewModel extends ViewModel {
    private static final String TAG = "PlacesViewModel";
    public static int userCount = 0;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    public MutableLiveData<List<Restaurant>> restaurantsList = new MutableLiveData<>();
    private final MutableLiveData<List<Restaurant>> filteredRestaurants = new MutableLiveData<>();
    private final MutableLiveData<Restaurant> restaurantMutableLiveData = new MutableLiveData<>();

    public ObservableBoolean isLoading = new ObservableBoolean(false);

    @Inject
    public PlacesViewModel(PlaceRepository placeRepository, UserRepository userRepository) {
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
    }

    public LiveData<List<Restaurant>> getRestaurantsList() {
        return restaurantsList;
    }

    public MutableLiveData<Restaurant> getRestaurant() {
        return restaurantMutableLiveData;
    }

    public MutableLiveData<List<Restaurant>> getFilteredRestaurantsList() {
        return filteredRestaurants;
    }

    public void setRestaurants(String location, int radius) {
        placeRepository.getNearByPlacesApi(location, radius)
                .subscribeOn(Schedulers.io())
                .map(placesResults -> {
                    isLoading.set(true);
                    List<com.tizzone.go4lunch.models.places.Result> placesResultsList = placesResults.getResults();
                    List<Restaurant> restaurants = new ArrayList<>();
                    for (com.tizzone.go4lunch.models.places.Result result : placesResultsList) {
                        Boolean isOpen = null;
                        if (result.getOpeningHours() != null) {
                            isOpen = result.getOpeningHours().getOpenNow();
                        }
                        Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(), result.getVicinity(), result.getPhotoUrl(), result.getRating(), userCount,
                                isOpen, result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), null, null);
                        restaurants.add(restaurant);
                    }
                    if (placesResultsList.size() > 0)
                        Log.e(TAG, "apply: " + placesResultsList.get(0).getName());
                    return restaurants;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            restaurantsList.setValue(result);
                            isLoading.set(false);
                        },
                        error -> Log.e(TAG, "setRestaurants:" + error.getMessage())
                );
    }

    public void setRestaurant(String uid) {
        placeRepository.getDetailByPlaceId(uid)
                .subscribeOn(Schedulers.io())
                .map(placeDetail -> {
                    Result result = placeDetail.getResult();
                    Restaurant restaurant = new Restaurant();
                    Float rating;
                    if (placeDetail.getResult() != null) {
                        Log.e(TAG, "apply: " + result.getName());
                        rating = result.getRating();
                        restaurant = new Restaurant(uid, result.getName(), result.getFormattedAddress(), result.getPhotoUrl(), rating, 0,
                                null, result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), result.getWebsite(), result.getInternationalPhoneNumber());

                    }
                    return restaurant;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantMutableLiveData::setValue,
                        error -> Log.e(TAG, "setRestaurants Filtered:" + error.getMessage())
                );
    }

    public void setPredictions(String input, String location, int radius, int sessionToken) {
        placeRepository.getPredictionsApi(input, location, radius, sessionToken)
                .subscribeOn(Schedulers.io())
                .map(apiPredictions -> {
                    Log.e(TAG, "apply: " + apiPredictions.getPredictions().get(0).getPlaceId());
                    return apiPredictions.getPredictions();
                })
                .map(predictions -> {
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
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(filteredRestaurants::setValue,
                        error -> Log.e(TAG, "setPredictions:" + error.getMessage())
                );
    }

    public void setFakeRestaurantList() {
        List<Restaurant> fakeList = new ArrayList<Restaurant>();
        fakeList.add(new Restaurant("ChIJX-06UAMQ70cRozwHabV2sJQ", "Le Clos des Jacobins", "49 Grande Rue, Sens", "ATtYBwKAnA2YnkqkKZg09qD9Czt6J1jbUvPkoskwUyD68ywl0Bjpo2ZME_WwEKqUy9GJL8E6XPVbkdQ8WC7ZweWmxsSK_uNIHtZA1KrMnMgb2sYy_UX8T1QSIYWXeMzi73WNlACl5x6PMUGamr7qKwLxt9N4QiVX8pJfHHgwJKfXAKg1O0bc", (float) 4.2, 0,
                null, 48.19714099999999, 3.2792574, "https://monsite.com", "0142546545"));
        fakeList.add(new Restaurant("ChIJu9WnDAQQ70cRqXiMV_657gQ", "Restaurant de la Cathédrale", "13 Place de la République, Sens", "ATtYBwKAnA2YnkqkKZg09qD9Czt6J1jbUvPkoskwUyD68ywl0Bjpo2ZME_WwEKqUy9GJL8E6XPVbkdQ8WC7ZweWmxsSK_uNIHtZA1KrMnMgb2sYy_UX8T1QSIYWXeMzi73WNlACl5x6PMUGamr7qKwLxt9N4QiVX8pJfHHgwJKfXAKg1O0bc", (float) 3.5, 0,
                null, 48.1980721, 3.2831136, "https://monsite.com", "0142546545"));
        restaurantsList.setValue(fakeList);
    }

}