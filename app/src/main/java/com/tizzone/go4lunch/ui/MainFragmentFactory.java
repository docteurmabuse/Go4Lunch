package com.tizzone.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.ui.list.ListViewFragment;
import com.tizzone.go4lunch.ui.map.MapFragment;
import com.tizzone.go4lunch.ui.workmates.WorkmatesFragment;

import java.util.List;

import javax.inject.Inject;

public class MainFragmentFactory extends FragmentFactory {
    public List<Restaurant> restaurantsList;

    @Inject
    public MainFragmentFactory(List<Restaurant> restaurantsList) {
        super();
        this.restaurantsList = restaurantsList;
    }

    @NonNull
    @Override
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
        Class<? extends Fragment> fragmentClass = loadFragmentClass(classLoader, className);
        if (fragmentClass == MapFragment.class) {
            return new MapFragment(restaurantsList);
        }
        if (fragmentClass == ListViewFragment.class) {
            return new ListViewFragment();
        }
        if (fragmentClass == WorkmatesFragment.class) {
            return new WorkmatesFragment();
        } else {
            return super.instantiate(classLoader, className);
        }
    }
}
