package com.tizzone.go4lunch.di;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.ui.MainNavHostFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@interface gMap {
}

@Module
@InstallIn(SingletonComponent.class)
public class MapModule {
    public MapModule() {
    }


    @Singleton
    @Provides
    static SupportMapFragment provideSupportMapFragment(MainNavHostFragment fragment) {
        return (SupportMapFragment) fragment.getChildFragmentManager().findFragmentById(R.id.map);
    }

    @Singleton
    @Provides
    static OnMapReadyCallback provideOnMapReadyCallback(OnMapReadyCallback callback) {
        return callback;
    }

}
