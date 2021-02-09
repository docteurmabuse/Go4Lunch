package com.tizzone.go4lunch.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentFactory;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainNavHostFragment extends androidx.navigation.fragment.NavHostFragment {
    @Inject
    public MainFragmentFactory fragmentFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        FragmentFactory fragmentFactory = this.getChildFragmentManager().getFragmentFactory();
        this.fragmentFactory = (MainFragmentFactory) fragmentFactory;
    }
}
