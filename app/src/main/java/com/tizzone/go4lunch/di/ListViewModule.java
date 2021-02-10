package com.tizzone.go4lunch.di;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class ListViewModule {
    public ListViewModule() {
    }
//    @Singleton
//    @Provides
//    static Fragment provideSupportListViewFragment(MainNavHostFragment fragment) {
//        return fragment.getChildFragmentManager().findFragmentById(R.id.fragment_list_relative_layout);
//    }

}
