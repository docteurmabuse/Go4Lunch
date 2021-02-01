package com.tizzone.go4lunch.ui.list;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.adapters.PlacesListAdapter;
import com.tizzone.go4lunch.databinding.FragmentListBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.viewmodels.LocationViewModel;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;

@AndroidEntryPoint
public class ListViewFragment extends Fragment {

    private PlacesListAdapter placesListAdapter;
    private FragmentListBinding fragmentListBinding;

    private final int PROXIMITY_RADIUS = 1000;
    private final int SESSION_TOKEN = 54784;

    private LocationViewModel locationViewModel;
    private PlacesViewModel placesViewModel;

    private List<Restaurant> restaurants;
    private String key;
    private LatLng currentLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getText(R.string.google_maps_key).toString();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity());
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);
        return fragmentListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Init ViewModels
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        placesViewModel = new ViewModelProvider(requireActivity()).get(PlacesViewModel.class);
        initRecycleView();
    }

    private void observeData() {
        locationViewModel.getUserLocation().observe(requireActivity(), locationModel -> {
            if (locationModel != null) {
                placesListAdapter.setCurrentLocation(locationModel.getLocation());
                this.currentLocation = locationModel.getLocation();
            }
        });

        placesViewModel.getRestaurantsList().observe(requireActivity(), restaurantsList -> {
            Log.e(TAG, "onChanged: " + restaurantsList.size());
            placesListAdapter.setPlaces(restaurantsList, currentLocation);
            restaurants = new ArrayList<>();
            restaurants.addAll(restaurantsList);
        });

        placesViewModel.getFilteredRestaurantsList().observe(getViewLifecycleOwner(), restaurants -> {
            placesListAdapter.setPlaces(restaurants, currentLocation);
        });
    }

    private void initRecycleView() {
        fragmentListBinding.listViewPlaces.setLayoutManager(new LinearLayoutManager(getContext()));
        placesListAdapter = new PlacesListAdapter(restaurants, currentLocation, getContext());
        fragmentListBinding.listViewPlaces.setAdapter(placesListAdapter);
        observeData();
    }


    public void onResume() {
        super.onResume();
        locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), locationModel -> {
            if (locationModel != null) {
                placesListAdapter.setCurrentLocation(locationModel.getLocation());
                this.currentLocation = locationModel.getLocation();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentListBinding = null;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
//        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        //searchView.setBackgroundColor(Color.WHITE);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        // searchView.setBackgroundColor(Color.WHITE);

        searchView.findViewById(R.id.search_close_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("called", "this is called.");
                        searchView.setIconified(true);
                        placesListAdapter.setPlaces(restaurants, currentLocation);
                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    placesViewModel.setPredictions(newText, currentLocation.latitude + "," + currentLocation.longitude, PROXIMITY_RADIUS, SESSION_TOKEN, key);
                } else {
                    placesListAdapter.setPlaces(restaurants, currentLocation);
                }
                return true;
            }
        });
    }

}