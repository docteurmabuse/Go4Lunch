package com.tizzone.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import com.google.android.gms.maps.GoogleMap;
import com.tizzone.go4lunch.ui.map.MapFragment;

import javax.inject.Inject;

public class MainFragmentFactory extends FragmentFactory {
    private final GoogleMap mMap;

    @Inject
    public MainFragmentFactory(GoogleMap mMap) {
        this.mMap = mMap;
    }

    /**
     * Create a new instance of a Fragment with the given class name. This uses
     * {@link #loadFragmentClass(ClassLoader, String)} and the empty
     * constructor of the resulting Class by default.
     *
     * @param classLoader The default classloader to use for instantiation
     * @param className   The class name of the fragment to instantiate.
     * @return Returns a new fragment instance.
     * @throws Fragment.InstantiationException If there is a failure in instantiating
     *                                         the given fragment class.  This is a runtime exception; it is not
     *                                         normally expected to happen.
     */
    @NonNull
    @Override
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
        if (MapFragment.class.getName().equals(className)) {
            return new MapFragment(mMap);
        } else {
            return super.instantiate(classLoader, className);
        }
    }
}
