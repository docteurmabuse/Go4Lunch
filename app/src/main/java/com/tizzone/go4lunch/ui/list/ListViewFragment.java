package com.tizzone.go4lunch.ui.list;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import static com.tizzone.go4lunch.utils.Constants.PROXIMITY_RADIUS;
import static com.tizzone.go4lunch.utils.Constants.RESTAURANT;
import static com.tizzone.go4lunch.utils.Constants.SESSION_TOKEN;

@AndroidEntryPoint
public class ListViewFragment extends Fragment implements PlacesListAdapter.RestaurantItemClickListener {

    private PlacesListAdapter placesListAdapter;
    private FragmentListBinding fragmentListBinding;
    private LocationViewModel locationViewModel;
    public PlacesViewModel placesViewModel;
    private List<Restaurant> restaurants;
    private LatLng currentLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate view and obtain an instance of the binding class.
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);
        fragmentListBinding.listViewPlaces.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentListBinding.listViewPlaces.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));
        placesListAdapter = new PlacesListAdapter(this);
        fragmentListBinding.listViewPlaces.setAdapter(placesListAdapter);
        setHasOptionsMenu(true);
        return fragmentListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Init ViewModels
        placesViewModel = new ViewModelProvider(requireActivity()).get(PlacesViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        observeData();
    }

    private void observeData() {
        placesViewModel.getRestaurantsList().observe(requireActivity(), restaurantsList -> {
            restaurants = new ArrayList<>(restaurantsList);
            placesListAdapter.setPlaces(restaurants, currentLocation);
            Log.e(TAG, "onChanged: " + restaurants.size());
        });
        locationViewModel.getUserLocation().observe(requireActivity(), locationModel -> {
            if (locationModel != null) {
                placesListAdapter.setCurrentLocation(locationModel.getLocation());
                this.currentLocation = locationModel.getLocation();
            }
        });
        placesViewModel.getFilteredRestaurantsList().observe(getViewLifecycleOwner(), restaurants -> placesListAdapter.setPlaces(restaurants, currentLocation));
    }

    public void onResume() {
        super.onResume();
        placesListAdapter.setPlaces(restaurants, currentLocation);
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
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.findViewById(R.id.search_close_btn)
                .setOnClickListener(v -> {
                    Log.d("called", "this is called.");
                    searchView.setIconified(true);
                    placesListAdapter.setPlaces(restaurants, currentLocation);
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    placesViewModel.setPredictions(newText, currentLocation.latitude + "," + currentLocation.longitude, PROXIMITY_RADIUS, SESSION_TOKEN);
                } else {
                    placesListAdapter.setPlaces(restaurants, currentLocation);
                }
                return true;
            }
        });
    }

    @Override
    public void onRestaurantClick(Restaurant restaurant) {
        Intent intent = new Intent(getContext(), PlaceDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESTAURANT, restaurant);
        intent.putExtras(bundle);
        startActivity(intent);
        Log.e(TAG, RESTAURANT + ": " + (restaurant.getName()));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentListBinding = null;
    }
}