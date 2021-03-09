package com.tizzone.go4lunch.di;

import com.tizzone.go4lunch.network.PlacesApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    @Provides
    @Singleton
    public PlacesApiService provideGooglePlacesApiService() {
        HttpUrl baseUrl = HttpUrl.get("https://maps.googleapis.com/maps/api/");
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(PlacesApiService.class);
    }

    @Provides
    public OkHttpClient provideHttpClient() {
        return new OkHttpClient.Builder()
                .build();
    }
}

