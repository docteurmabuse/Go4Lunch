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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;
import static com.tizzone.go4lunch.utils.Constants.PROXIMITY_RADIUS;
import static com.tizzone.go4lunch.utils.Constants.SESSION_TOKEN;

@AndroidEntryPoint
public class ListViewFragment extends Fragment {

    private PlacesListAdapter placesListAdapter;
    private FragmentListBinding fragmentListBinding;

    private LocationViewModel locationViewModel;

    public PlacesViewModel placesViewModel;

    private List<Restaurant> restaurants;
    private LatLng currentLocation;

    @Inject
    public LiveData<List<Restaurant>> restaurantsList;

    public ListViewFragment(LiveData<List<Restaurant>> restaurantsList) {
        this.restaurantsList = restaurantsList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate view and obtain an instance of the binding class.
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);
        fragmentListBinding.listViewPlaces.setLayoutManager(new LinearLayoutManager(getContext()));
        placesListAdapter = new PlacesListAdapter();
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
            Log.e(TAG, "onChanged: " + restaurantsList.size());
            placesListAdapter.setPlaces(restaurantsList, currentLocation);
            restaurants = new ArrayList<>();
            restaurants.addAll(restaurantsList);
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
}