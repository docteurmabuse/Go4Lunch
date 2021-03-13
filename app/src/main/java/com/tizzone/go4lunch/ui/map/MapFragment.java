package com.tizzone.go4lunch.ui.map;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.FragmentMapBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.utils.Permissions;
import com.tizzone.go4lunch.viewmodels.LocationViewModel;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.Disposable;

import static android.content.ContentValues.TAG;
import static com.tizzone.go4lunch.utils.Constants.DEFAULT_ZOOM;
import static com.tizzone.go4lunch.utils.Constants.LATITUDE;
import static com.tizzone.go4lunch.utils.Constants.LONGITUDE;
import static com.tizzone.go4lunch.utils.Constants.RESTAURANT_ID;
import static com.tizzone.go4lunch.utils.Constants.SESSION_TOKEN;
import static com.tizzone.go4lunch.utils.Constants.mDefaultLocation;
import static com.tizzone.go4lunch.utils.Utils.getBitmapFromVectorDrawable;

@AndroidEntryPoint
public class MapFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private int radius;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesViewModel placesViewModel;
    private LocationViewModel locationViewModel;
    private Context mContext;
    private LatLng currentLocation;
    private List<User> matesList;
    private UserViewModel userViewModel;
    private ArrayList<String> matesSpotList;
    private Disposable disposable;
    @Inject
    public List<Restaurant> restaurantsList;
    @Inject
    public LiveData<List<Restaurant>> restaurantsListLiveData;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            // Turn on the My Location layer and the related control on the map.
            //updateLocationUI();
            // Get the current location of the device and set the position of the map.
            //  getDeviceLocation();
            getLocationPermission();
            // Set a listener for info window events.
            mMap.setOnInfoWindowClickListener(marker -> viewRestaurantDetail((Restaurant) marker.getTag()));
        }
    };


    public MapFragment(List<Restaurant> restaurantsList) {
        this.restaurantsList = restaurantsList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setupSharedPreferences();
        // Init  ViewModels
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        placesViewModel = new ViewModelProvider(requireActivity()).get(PlacesViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        observeData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentMapBinding mapBinding = FragmentMapBinding.inflate(inflater, container, false);
        //Init users list
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        return mapBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }


    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        radius = Integer.parseInt(sharedPreferences.getString("radius", "1000"));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void observeData() {
        placesViewModel.getRestaurantsList().observe(requireActivity(), this::initRestaurantsList);
        placesViewModel.getFilteredRestaurantsList().observe(requireActivity(), restaurants -> {
            mMap.clear();
            restaurantsList = restaurants;
            initMarkers(restaurants);
        });
        userViewModel.getUsersList().observe(requireActivity(), this::usersSpotList);
        userViewModel.getUserMutableLiveData();
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
                    placesViewModel.setPredictions(newText, currentLocation.latitude + "," + currentLocation.longitude, radius, SESSION_TOKEN);
                    return true;
                }
                return true;
            }
        });
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
        if (mMap != null) {
            initMarkers(restaurantsList);
        }
    }

    private void initMarkers(List<Restaurant> mRestaurants) {
        if (mRestaurants != null) {
            if (mMap != null) {
                mMap.clear();
            }
            for (Restaurant restaurant : mRestaurants) {
                if (!placeIsMatesSpot(restaurant)) {
                    setMarkers(restaurant, R.drawable.ic_restaurant_pin_red);
                } else {
                    setMarkers(restaurant, R.drawable.ic_restaurant_pin_green);
                }
            }
        }
    }

    private void initRestaurantsList(List<Restaurant> mRestaurants) {
        this.restaurantsList.addAll(mRestaurants);
        if (mMap != null) {
            initMarkers(mRestaurants);
        }
    }

    private boolean placeIsMatesSpot(Restaurant restaurant) {
        if (matesSpotList != null)
            return ArrayUtils.contains(matesSpotList.toArray(), restaurant.getUid());
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
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(restaurant.getName())
                .snippet(restaurant.getAddress())
                .icon(mapIcon));
        marker.setTag(restaurant);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap != null) {
            initMarkers(restaurantsList);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(currentLocation.latitude,
                                currentLocation.longitude), DEFAULT_ZOOM));
                locationViewModel.setUserLocation(currentLocation.latitude,
                        currentLocation.longitude);
                build_retrofit_and_get_response(currentLocation.latitude,
                        currentLocation.longitude);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
                            currentLocation = new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude());
                        } else {
                            currentLocation = new LatLng(LATITUDE, LONGITUDE);
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        currentLocation = mDefaultLocation;
                    }
                    updateLocationUI();
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (Permissions.checkLocationPermission(requireActivity())) {
            mLocationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            getDeviceLocation();
        } else {
            Permissions.requestLocationPermission(this.getActivity());
        }
    }

    private void build_retrofit_and_get_response(double latitude, double longitude) {
        placesViewModel.setRestaurants(latitude + "," + longitude, radius);
        locationViewModel.setUserLocation(latitude, longitude);
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
        if (s.equals("radius")) {
            Log.e(TAG, "Preference radius value was updated to: " + sharedPreferences.getString(s, ""));
            radius = Integer.parseInt(sharedPreferences.getString(s, ""));
            mMap.clear();
            placesViewModel.setRestaurants(currentLocation.latitude + "," + currentLocation.longitude, Integer.parseInt(sharedPreferences.getString(s, "")));
        }
    }
}