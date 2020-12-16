package com.tizzone.go4lunch.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tizzone.go4lunch.api.GoogleMapAPI;
import com.tizzone.go4lunch.api.PlacesApi;
import com.tizzone.go4lunch.models.places.PlacesResults;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesRepository {
    private static final String PLACE_SEARCH_SERVICE_BASE_URL = "https://maps.googleapis.com/";
    private static PlacesRepository placesRepository;
    private  GoogleMapAPI googleMapAPI;
    private  MutableLiveData<PlacesResults> placesResultsLiveData;
    private PlacesApi placesApi;

    public PlacesRepository() {
        placesResultsLiveData = new MutableLiveData<>();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        googleMapAPI = new Retrofit.Builder()
                .baseUrl(PLACE_SEARCH_SERVICE_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleMapAPI.class);
    }

    public static PlacesRepository getInstance() {
        if (placesRepository == null)
            placesRepository = new PlacesRepository();
        return placesRepository;
    }

    public void getNearByPlaces(String location, int radius, String type, String key, PlacesResultsInterface mPlacesResultsInterface) {
        googleMapAPI.getNearByPlaces(location, radius, type, key)
                .enqueue(new Callback<PlacesResults>() {
                    @Override
                    public void onResponse(Call<PlacesResults> call, Response<PlacesResults> response) {
                        if (response.body() != null) {
                            placesResultsLiveData.postValue(response.body());
                            mPlacesResultsInterface.onResponse(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<PlacesResults> call, Throwable t) {
                        placesResultsLiveData.postValue(null);
                    }
                });
    }

    public LiveData<PlacesResults> getPlacesResultsLiveData() {
        return placesResultsLiveData;
    }

    public interface PlacesResultsInterface{
        void onResponse(PlacesResults placesResults);
    }
}
