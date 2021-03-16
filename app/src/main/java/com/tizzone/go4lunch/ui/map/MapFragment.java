package com.tizzone.go4lunch.ui.map;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.FragmentMapBinding;
import com.tizzone.go4lunch.models.LocationModel;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.utils.Utils;
import com.tizzone.go4lunch.viewmodels.LocationViewModel;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.tizzone.go4lunch.utils.Constants.DEFAULT_ZOOM;
import static com.tizzone.go4lunch.utils.Constants.LATITUDE;
import static com.tizzone.go4lunch.utils.Constants.LONGITUDE;
import static com.tizzone.go4lunch.utils.Constants.RESTAURANT_ID;
import static com.tizzone.go4lunch.utils.Constants.SESSION_TOKEN;
import static com.tizzone.go4lunch.utils.Constants.TAG_MAP_VIEW;
import static com.tizzone.go4lunch.utils.Constants.radius;
import static com.tizzone.go4lunch.utils.Utils.getBitmapFromVectorDrawable;

@AndroidEntryPoint
public class MapFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, OnMapReadyCallback {
    private int mRadius;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private PlacesViewModel placesViewModel;
    private LocationViewModel locationViewModel;
    private Context mContext;
    private LatLng currentLocation;
    private UserViewModel userViewModel;
    private List<String> matesSpotList;
    @Inject
    public List<Restaurant> restaurantsList;
    @Inject
    public LiveData<List<Restaurant>> restaurantsListLiveData;
    private GoogleMap map;
    private SharedPreferences sharedPreferences;

    public MapFragment(List<Restaurant> restaurantsList) {
        this.restaurantsList = restaurantsList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        // Init  ViewModels
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        setupSharedPreferences();
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        placesViewModel = new ViewModelProvider(requireActivity()).get(PlacesViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeData();
    }

    private void observeData() {
        locationViewModel.getUserLocation().observe(requireActivity(), this::initPosition);
        userViewModel.getUserMutableLiveData();
        placesViewModel.getFilteredRestaurantsList().observe(requireActivity(), restaurants -> {
            restaurantsList = restaurants;
            initMarkers();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentMapBinding mapBinding = FragmentMapBinding.inflate(inflater, container, false);
        if (map == null) {
            initMap();
        }

        return mapBinding.getRoot();
    }

    private void initMap() {
        //Init map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) requireContext().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.findViewById(R.id.search_close_btn)
                .setOnClickListener(v -> {
                    Log.d("called", "this is called.");
                    initRestaurantsList(restaurantsList);
                    searchView.setIconified(true);
                });
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
                    searchView.setFocusable(false);
                    placesViewModel.setPredictions(newText, currentLocation.latitude + "," + currentLocation.longitude, mRadius, SESSION_TOKEN);
                    return true;
                }
                return true;
            }
        });
    }

    private void setupSharedPreferences() {
        mRadius = Integer.parseInt(sharedPreferences.getString("radius", "1000"));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        currentLocation = Utils.getLocationFromSharedPreferences(requireActivity());
        locationViewModel.setUserLocation(currentLocation.latitude, currentLocation.longitude);
        mLocationPermissionGranted = sharedPreferences.getBoolean("mLocationPermissionGranted", true);
        initCurrentLocation(currentLocation);
    }

    private void initPosition(LocationModel locationModel) {
        currentLocation = new LatLng(locationModel.getLocation().latitude, locationModel.getLocation().longitude);
    }

    private void initCurrentLocation(LatLng location) {
        if (map != null)
            movedCameraToCurrentPosition(location);
    }

    private void usersSpotList(List<User> users) {
        if (users != null) {
            matesSpotList = new ArrayList<>();
            for (User user : users) {
                if (user.getLunchSpotId() != null) {
                    matesSpotList.add(user.getLunchSpotId());
                }
            }
        }
    }

    private void initRestaurantsList(List<Restaurant> mRestaurants) {
        this.restaurantsList.addAll(mRestaurants);
        if (map != null) {
            restaurantsList = mRestaurants;
            initMarkers();
        }
    }

    private void initMarkers() {
        if (restaurantsList != null) {
            for (Restaurant restaurant : restaurantsList) {
                if (!placeIsMatesSpot(restaurant)) {
                    setMarkers(restaurant, R.drawable.ic_restaurant_pin_red);
                } else {
                    setMarkers(restaurant, R.drawable.ic_restaurant_pin_green);
                }
            }
        }
    }

    private boolean placeIsMatesSpot(Restaurant restaurant) {
        if (matesSpotList != null)
            return restaurant.getRestaurant_counter() > 0;
        else {
            return false;
        }
    }

    private void setMarkers(Restaurant restaurant, int iconId) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = restaurant.getLocation();
        // Position of Marker on Map
        markerOptions.position(latLng);
        // Adding Title to the Marker
        markerOptions.title(restaurant.getName() + " : " + restaurant.getAddress());
        Bitmap bitmap = getBitmapFromVectorDrawable(iconId, mContext);
        BitmapDescriptor mapIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        Marker marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(restaurant.getName())
                .snippet(restaurant.getAddress())
                .icon(mapIcon));
        marker.setTag(restaurant);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            map.clear();
            initMarkers();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.setOnMyLocationButtonClickListener(() -> {
                    getDeviceLocation();
                    return true;
                });
                map.setOnMyLocationClickListener(location -> getDeviceLocation());
                Toast.makeText(mContext, "OMG! It works" + currentLocation, Toast.LENGTH_SHORT).show();
                movedCameraToCurrentPosition(currentLocation);
                userViewModel.getUsersList().observe(requireActivity(), this::usersSpotList);
                placesViewModel.getRestaurantsList().observe(requireActivity(), this::initRestaurantsList);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                //mLastKnownLocation = null;
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void build_retrofit_and_get_response(double latitude, double longitude) {
        if (latitude != 0 && longitude != 0) {
            placesViewModel.setRestaurants(latitude + "," + longitude, mRadius);
            locationViewModel.setUserLocation(latitude, longitude);
        }
    }

    private void viewRestaurantDetail(Restaurant lunchSpot) {
        final Context context = getContext();
        Intent intent = new Intent(context, PlaceDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(RESTAURANT_ID, lunchSpot.getUid());
        intent.putExtras(bundle);
        assert context != null;
        context.startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(radius)) {
            Log.e(TAG_MAP_VIEW, "Preference radius value was updated to: " + sharedPreferences.getString(s, ""));
            mRadius = Integer.parseInt(sharedPreferences.getString(s, ""));
            // map.clear();
        }
    }

    @SuppressLint({"PotentialBehaviorOverride"})
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        updateLocationUI();
        map.setOnInfoWindowClickListener(marker -> viewRestaurantDetail((Restaurant) marker.getTag()));
        if (currentLocation != null) {
            movedCameraToCurrentPosition(currentLocation);
        }
    }

    private void movedCameraToCurrentPosition(LatLng currentLocation) {
        CameraPosition cp = CameraPosition.builder().target(currentLocation).zoom(DEFAULT_ZOOM).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 100, null);
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                // Construct a FusedLocationProviderClient.
                FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null && map != null) {
                            currentLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            movedCameraToCurrentPosition(currentLocation);
                            Utils.addSpotLocationInSharedPreferences(requireActivity(), mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            locationViewModel.setUserLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            build_retrofit_and_get_response(currentLocation.latitude, currentLocation.longitude);
                        }
                    } else {
                        Log.d(TAG_MAP_VIEW, "Current location is null. Using defaults.");
                        Log.e(TAG_MAP_VIEW, "Exception: %s", task.getException());
                        if (map != null) {
                            movedCameraToCurrentPosition(currentLocation);
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                            currentLocation = new LatLng(LATITUDE, LONGITUDE);
                            locationViewModel.setUserLocation(LATITUDE, LONGITUDE);
                            Utils.addSpotLocationInSharedPreferences(requireActivity(), LATITUDE, LONGITUDE);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}