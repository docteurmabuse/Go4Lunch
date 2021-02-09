package com.tizzone.go4lunch.di;

import com.google.android.gms.maps.GoogleMap;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class MapModule {
    public MapModule() {
    }

    @Singleton
    @Provides
    static GoogleMap provideGmap(GoogleMap mGmap) {
        return mGmap;
    }
}
