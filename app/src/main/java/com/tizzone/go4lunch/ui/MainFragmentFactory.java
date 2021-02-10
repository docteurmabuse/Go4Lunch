package com.tizzone.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import com.tizzone.go4lunch.ui.list.ListViewFragment;
import com.tizzone.go4lunch.ui.map.MapFragment;
import com.tizzone.go4lunch.ui.workmates.WorkmatesFragment;

import javax.inject.Inject;

public class MainFragmentFactory extends FragmentFactory {
    @Inject
    public MainFragmentFactory() {
        super();
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
        Class<? extends Fragment> fragmentClass = loadFragmentClass(classLoader, className);
        if (fragmentClass == MapFragment.class) {
            return new MapFragment();
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
