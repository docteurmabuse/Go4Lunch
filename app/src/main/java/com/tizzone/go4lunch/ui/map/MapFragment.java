package com.tizzone.go4lunch.ui.map;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;

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
import com.tizzone.go4lunch.ui.MainNavHostFragment;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.viewmodels.LocationViewModel;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;
import com.tizzone.go4lunch.viewmodels.SharedViewModel;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;
import static com.tizzone.go4lunch.utils.Utils.getBitmapFromVectorDrawable;

@AndroidEntryPoint
public class MapFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 546;
    private static final float DEFAULT_ZOOM = 15;
    private final int PROXIMITY_RADIUS = 1000;
    private final int SESSION_TOKEN = 54784;
    private final LatLng mDefaultLocation = new LatLng(65.850559, 2.377078);

    @Inject
    public String randomString;
    @Inject
    public MutableLiveData<List<Restaurant>> restaurantsList;

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String key;
    private PlacesViewModel placesViewModel;
    private LocationViewModel locationViewModel;
    private UserViewModel userViewModel;
    private SharedViewModel sharedViewModel;
    private Marker workmatesRestaurant;
    private Marker emptyRestaurant;
    private Context mContext;
    private LatLng currentLocation;
    private List<Restaurant> restaurantsList2;
    private List<Restaurant> restaurantsMatesList;

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


            // Set a listener for info window events.
            mMap.setOnInfoWindowClickListener(marker -> viewRestaurantDetail((Restaurant) marker.getTag()));
        }
    };
    private List<Restaurant> restaurants;
    private MapViewModel mapViewModel;


    public MapFragment(String randomString, MutableLiveData<List<Restaurant>> restaurantsList) {
        this.randomString = randomString;
        this.restaurantsList = restaurantsList;
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Init  ViewModels
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        MainNavHostFragment navHostFragment =
                (MainNavHostFragment) requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.my_graph);
//        placesViewModel = new ViewModelProvider(backStackEntry,
//                HiltViewModelFactory.create(getActivity(), backStackEntry)).get(PlacesViewModel.class);

        locationViewModel = new ViewModelProvider(backStackEntry,
                HiltViewModelFactory.create(requireActivity(), backStackEntry)).get(LocationViewModel.class);
        //RestaurantViewModel restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        System.out.println("MapFragment test" + sharedViewModel);
        setHasOptionsMenu(true);
//        if (permissionsManager.isAccessFineLocationGranted(getActivity())){
//            setUpLocationListener();
//        }
//        else {
//            permissionsManager.requestLocationPermission(this);
//        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        com.tizzone.go4lunch.databinding.FragmentMapBinding mapBinding = FragmentMapBinding.inflate(inflater, container, false);
        return mapBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        key = getText(R.string.google_maps_key).toString();
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        observeData();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void observeData() {

//        placesViewModel.getRestaurantsList().observe(getActivity(), restaurants -> {
//            restaurants.addAll(restaurants);
//            initRestaurantsList(restaurants);
//        });

        locationViewModel.getUserLocation().observe(requireActivity(), locationModel -> {
            if (locationModel != null) {
                // build_retrofit_and_get_response(locationModel.getLocation().latitude, locationModel.getLocation().longitude);
                this.currentLocation = locationModel.getLocation();
            }
        });

//        placesViewModel.getFilteredRestaurantsList().observe(getActivity(), restaurants -> {
//            mMap.clear();
//            initRestaurantsList(restaurants);
        //});
//
//        sharedViewModel.getRestaurantsList().observe(requireActivity(), restaurants1 -> {
//            initRestaurantsList(restaurants);
//        });

    }


    private void initRestaurantsList(List<Restaurant> mRestaurants) {
        restaurantsList2 = new ArrayList<>();
        for (Restaurant restaurant : mRestaurants) {
            userViewModel.addUserToLiveData(restaurant.getUid()).observe(requireActivity(), users -> {
                if (users.size() > 0) {
                    // restaurantsMatesList.add(restaurant);
                } else {
                    restaurantsList2.add(restaurant);
                }
            });
        }
        setMarkers(mRestaurants, R.drawable.ic_restaurant_pin_red);
        setMarkers(restaurantsMatesList, R.drawable.ic_restaurant_pin_green);
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

             /*   UserHelper.getUsersLunchSpot(restaurant.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listener failed.", error);
                            Bitmap bitmap = getBitmapFromVectorDrawable(R.drawable.ic_restaurant_pin_red, mContext);
                            BitmapDescriptor mapIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
                            // Adding No Workmates Restaurant Marker to the Camera.
                            emptyRestaurant = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(restaurant.getName())
                                    .snippet(restaurant.getAddress())
                                    .icon(mapIcon));
                            emptyRestaurant.setTag(restaurant);
                        } else {
                            int usersCount = value.size();
                            if (usersCount > 0) {
                                Bitmap bitmap = getBitmapFromVectorDrawable(R.drawable.ic_restaurant_pin_green, mContext);
                                BitmapDescriptor mapIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
                                // Adding Workmates Restaurant's Marker to the Camera.

                                workmatesRestaurant = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(restaurant.getName())
                                        .snippet(restaurant.getAddress())
                                        .icon(mapIcon));
                                workmatesRestaurant.setTag(restaurant);
                            } else {
                                Bitmap bitmap = getBitmapFromVectorDrawable(R.drawable.ic_restaurant_pin_red, mContext);
                                BitmapDescriptor mapIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
                                // Adding No Workmates Restaurant Marker to the Camera.
                                emptyRestaurant = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(restaurant.getName())
                                        .snippet(restaurant.getAddress())
                                        .icon(mapIcon));
                                emptyRestaurant.setTag(restaurant);
                            }
                        }

                    }
                });*/
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
                .setOnClickListener(v -> {
                    Log.d("called", "this is called.");
                    initRestaurantsList(restaurants);
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
                    placesViewModel.setPredictions(newText, currentLocation.latitude + "," + currentLocation.longitude, PROXIMITY_RADIUS, SESSION_TOKEN, key);
                    return true;
                }
                return true;
            }
        });
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
                            // locationViewModel.setUserLocation(mLastKnownLocation.getLatitude(),
                            // mLastKnownLocation.getLongitude());
                            build_retrofit_and_get_response(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        } else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mDefaultLocation.latitude, mDefaultLocation.longitude), DEFAULT_ZOOM));
                            //  locationViewModel.setUserLocation(mDefaultLocation.latitude, mDefaultLocation.longitude
                            //   );
                            build_retrofit_and_get_response(mDefaultLocation.latitude, mDefaultLocation.longitude);
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        build_retrofit_and_get_response(mDefaultLocation.latitude, mDefaultLocation.longitude);
                        //locationViewModel.setUserLocation(mDefaultLocation.latitude,
                        //  mDefaultLocation.longitude);
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
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    private void build_retrofit_and_get_response(double latitude, double longitude) {
        sharedViewModel.setRestaurants(latitude + "," + longitude, PROXIMITY_RADIUS).observe(requireActivity(), this::initRestaurantsList);
        locationViewModel.setUserLocation(latitude, longitude);
    }

    private void viewRestaurantDetail(Restaurant restaurant) {
        final Context context = getContext();
        Intent intent = new Intent(context, PlaceDetailActivity.class);
        intent.putExtra("RESTAURANT", restaurant.getUid());
        context.startActivity(intent);
    }

//    private void setUpLocationListener() {
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
//        LocationRequest locationRequest = new LocationRequest().setInterval(2000).setFastestInterval(2000)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        LocationCallback locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                for (Location location : locationResult.getLocations()) {
//                    if (currentLocation != null) {
//                        if (location.getLatitude() != currentLocation.latitude && location.getLongitude() != currentLocation.longitude)
//                            locationViewModel.setUserLocation(mLastKnownLocation.getLatitude(),
//                                    mLastKnownLocation.getLongitude());
//                    }
//                }
//
//            }
//        };
//
//        mFusedLocationProviderClient.requestLocationUpdates(
//                locationRequest, locationCallback,
//                Looper.getMainLooper());
//    }

    @Override
    public void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        // mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}