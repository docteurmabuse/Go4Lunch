package com.tizzone.go4lunch.di;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.tizzone.go4lunch.ui.MainNavHostFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import static com.tizzone.go4lunch.R.id.nav_host_fragment;
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@interface MainActivity {
}

@Module
@InstallIn(SingletonComponent.class)
public class ActivityModule {
    public ActivityModule() {
    }

    @MainActivity
    @Singleton
    @Provides
    static Activity provideAppCompatActivity(Activity activity) {
        return activity;
    }

    @Singleton
    @Provides
    static FragmentManager provideFragmentManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @Singleton
    @Provides
    static NavHostFragment provideMainNavHostFragment(FragmentManager fragmentManager) {
        return (MainNavHostFragment) fragmentManager.findFragmentById(nav_host_fragment);
    }

    @Singleton
    @Provides
    static NavController provideNavController(NavHostFragment navHostFragment) {
        return navHostFragment.getNavController();
    }
}


