package com.tizzone.go4lunch.ui.map;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
import com.tizzone.go4lunch.viewmodels.LocationViewModel;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;
import com.tizzone.go4lunch.viewmodels.RestaurantViewModel;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;
import static com.tizzone.go4lunch.utils.Utils.getBitmapFromVectorDrawable;

@AndroidEntryPoint
public class MapFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 546;
    private static final float DEFAULT_ZOOM = 15;
    private int radius;
    private final int SESSION_TOKEN = 54784;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(65.850559, 2.377078);
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final double latitude = 78.850559;
    private final double longitude = 2.377078;
    private String key;
    private PlacesViewModel placesViewModel;
    private LocationViewModel locationViewModel;
    private Marker workmatesRestaurant;
    private Marker emptyRestaurant;
    private Context mContext;
    private LatLng currentLocation;
    @Inject
    public String randomString;
    @Inject
    public LiveData<List<Restaurant>> restaurantsListLiveData;
    private RestaurantViewModel restaurantViewModel;
    private List<Restaurant> restaurantsList;
    private FragmentMapBinding mapBinding;
    private UserViewModel userViewModel;
    private List<Restaurant> restaurantsMapList;
    private List<User> workmatesList;
    private List<Restaurant> restaurants;
    private MapViewModel mapViewModel;
    private List<Restaurant> matesRestaurantList;
    private ArrayList matesUidList;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            // Prompt the user for permission.
            getLocationPermission();
            // [END_EXCLUDE]

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
            observeData();

            // Set a listener for info window events.
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    viewRestaurantDetail((Restaurant) marker.getTag());
                }
            });
        }
    };


    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public MapFragment(String randomString, LiveData<List<Restaurant>> restaurantsList) {
        this.randomString = randomString;
        this.restaurantsListLiveData = restaurantsList;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setupSharedPreferences();
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        radius = Integer.parseInt(sharedPreferences.getString("radius", ""));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mapBinding = FragmentMapBinding.inflate(inflater, container, false);
        View root = mapBinding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init  ViewModels
        placesViewModel = new ViewModelProvider(requireActivity()).get(PlacesViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        key = getText(R.string.google_maps_key).toString();
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }


    private void observeData() {
//        placesViewModel.getRestaurantsList().observe(getActivity(), new Observer<List<Restaurant>>() {
//            @Override
//            public void onChanged(List<Restaurant> restaurants) {
//                setMarkers(restaurants);
//                restaurantsList = new ArrayList<>();
//                restaurantsList.addAll(restaurants);
//            }
//        });

        placesViewModel.getRestaurantsList().observe(requireActivity(), this::initRestaurantsList);

        locationViewModel.getUserLocation().observe(getActivity(), locationModel -> {
            if (locationModel != null) {
                this.currentLocation = locationModel.getLocation();
            }
        });

        placesViewModel.getFilteredRestaurantsList().observe(requireActivity(), this::initRestaurantsList);

        userViewModel.getFirebaseUsers().observe(requireActivity(), this::initWorkmatesList);
        placesViewModel.getFilteredRestaurantsList().observe(getActivity(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                mMap.clear();
                initRestaurantsList(restaurants);
            }
        });
    }

    private void initWorkmatesList(List<User> users) {
        workmatesList = new ArrayList<User>(users);
        if (workmatesList != null) {
            matesUidList = new ArrayList();
            for (User user : workmatesList) {
                if (user.getLunchSpot() != null) {
                    matesUidList.add(user.getLunchSpot());
                }
            }
        }
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

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setIconifiedByDefault(false);

        searchView.findViewById(R.id.search_close_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("called", "this is called.");
                        initRestaurantsList(restaurantsList);
                        searchView.setIconified(true);
                    }
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

    private void initRestaurantsList(List<Restaurant> mRestaurants) {
        restaurantsMapList = new ArrayList<>();
        matesRestaurantList = new ArrayList<>();


        for (Restaurant restaurant : mRestaurants) {
            boolean isMatesSpot = placeIsMatesSpot(restaurant);
            if (isMatesSpot) {
                matesRestaurantList.add(restaurant);
            } else {
                restaurantsMapList.add(restaurant);
            }

        }
//        Log.e(TAG, "apply restaurant list live data: " + workmatesList.size());
        setMarkers(restaurantsMapList, R.drawable.ic_restaurant_pin_red);
        setMarkers(matesRestaurantList, R.drawable.ic_restaurant_pin_green);
    }

    private boolean placeIsMatesSpot(Restaurant restaurant) {
        if (matesUidList != null)
            return ArrayUtils.contains(matesUidList.toArray(), restaurant.getUid());
        else {
            return false;
        }
    }

    private void setMarkers(List<Restaurant> restaurants, int iconId) {
        if (restaurants.size() > 0) {
            for (Restaurant restaurant : restaurants) {
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
        }
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
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
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            locationViewModel.setUserLocation(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude());
                            build_retrofit_and_get_response(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        } else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(latitude, longitude), DEFAULT_ZOOM));
                            locationViewModel.setUserLocation(latitude,
                                    longitude);
                            build_retrofit_and_get_response(latitude, longitude);
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        build_retrofit_and_get_response(mDefaultLocation.latitude, mDefaultLocation.longitude);
                        locationViewModel.setUserLocation(mDefaultLocation.latitude,
                                mDefaultLocation.longitude);
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireActivity(),
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.clear();
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void build_retrofit_and_get_response(double latitude, double longitude) {
        placesViewModel.setRestaurants(latitude + "," + longitude, radius);
        locationViewModel.setUserLocation(latitude, longitude);
    }

    private void viewRestaurantDetail(Restaurant restaurant) {
        final Context context = getContext();
        Intent intent = new Intent(context, PlaceDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("RESTAURANT", restaurant);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("radius")) {
            Log.e(TAG, "Preference55 value was updated to: " + sharedPreferences.getString(s, ""));
            //placesViewModel.setRadius(Integer.parseInt(sharedPreferences.getString(s, "")));
            radius = Integer.parseInt(sharedPreferences.getString(s, ""));
            mMap.clear();
            placesViewModel.setRestaurants(currentLocation.latitude + "," + currentLocation.longitude, Integer.parseInt(sharedPreferences.getString(s, "")));
        }
    }
}