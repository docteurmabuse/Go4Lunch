package com.tizzone.go4lunch.ui.list;

import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.adapters.PlacesListAdapter;
import com.tizzone.go4lunch.databinding.FragmentListBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.viewmodels.LocationViewModel;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;
import com.tizzone.go4lunch.viewmodels.RestaurantViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;

@AndroidEntryPoint
public class ListViewFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 44;
    private PlacesListAdapter placesListAdapter;
    private String key;
    private FragmentListBinding fragmentListBinding;
    private final int PERMISSION_ID = 44;

    private final LatLng mDefaultLocation = new LatLng(48.850559, 2.377078);
    private final int PROXIMITY_RADIUS = 1000;
    private final int SESSION_TOKEN = 54784;

    private boolean mLocationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    private LatLng currentLocation;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText queryText;
    private LocationViewModel locationViewModel;
    private RestaurantViewModel restaurantViewModel;
    private PlacesViewModel placesViewModel;

    private SearchView.SearchAutoComplete searchAutoComplete;
    private PlacesClient placesClient;
    private List<Restaurant> restaurants;


    public static LatLng getCoordinate(double lat0, double lng0, long dy, long dx) {
        double lat = lat0 + (180 / Math.PI) * (dy / 6378137);
        double lng = lng0 + (180 / Math.PI) * (dx / 6378137) / Math.cos(lat0);
        return new LatLng(lat, lng);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getText(R.string.google_maps_key).toString();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity());


        setHasOptionsMenu(true);
        Places.initialize(this.getContext().getApplicationContext(), key);
        placesClient = Places.createClient(this.getContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);
        return fragmentListBinding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Init ViewModels
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        placesViewModel = new ViewModelProvider(requireActivity()).get(PlacesViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);

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

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        //Set current user position in adapter
        //LocationViewModel locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
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
        searchView.setIconifiedByDefault(false);

        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
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

        //  getActivity().setContentView(R.layout.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    searchView.setFocusable(false);
                    return false;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    placesViewModel.setPredictions(newText, currentLocation.latitude + "," + currentLocation.longitude, PROXIMITY_RADIUS, SESSION_TOKEN, key);
                    searchView.setFocusable(false);
                    return true;
                }
                return true;
            }
        });
    }


    /**
     * Called when the hidden state (as returned by {@link #isHidden()} of
     * the fragment has changed.  Fragments start out not hidden; this will
     * be called whenever the fragment changes state from that.
     *
     * @param hidden True if the fragment is now hidden, false otherwise.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    private void onChanged(List<Restaurant> restaurants) {
        placesListAdapter.setPlaces(restaurants, currentLocation);
    }


}