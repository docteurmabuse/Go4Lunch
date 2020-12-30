package com.tizzone.go4lunch.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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
import com.google.android.libraries.places.api.net.PlacesClient;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 546;
    private static final float DEFAULT_ZOOM = 15;
    private final int PROXIMITY_RADIUS = 1000;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(48.850559, 2.377078);
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final double latitude = 48.850167;
    private final double longitude = 2.390770;
    private String key;
    private PlacesViewModel placesViewModel;

    private void setupMap() {
        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        else {
            // Show rationale and request permission.
        }
    }
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        private Location mLastLocation;
        private Marker mCurrLocationMarker;
        private LocationRequest mLocationRequest;
        private PlacesClient mPlaceDetectionClient;

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;


//                // Do other setup activities here too, as described elsewhere in this tutorial.@Override
//                LatLng paris = new LatLng(48.850167, 2.390770);
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
//
//                CameraPosition cameraPosition = new CameraPosition.Builder()
//                        .target(paris)      // Sets the center of the map to Mountain View
//                        .zoom(17)                   // Sets the zoom
//                        .bearing(90)                // Sets the orientation of the camera to east
//                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
//                        .build();                   // Creates a CameraPosition from the builder
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.clear();
            }
        }


    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        placesViewModel =
                new ViewModelProvider(requireActivity()).get(PlacesViewModel.class);
        placesViewModel.init();
        return root;
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
    }

    private void build_retrofit_and_get_response(double latitude, double longitude) {
        placesViewModel.getNearByPlaces(latitude + "," + longitude, PROXIMITY_RADIUS, "restaurant", key);
        placesViewModel.getPlacesResultsLiveData().observe(this, new Observer<PlacesResults>() {
            @Override
            public void onChanged(PlacesResults placesResults) {
                if (placesResults != null) {
                    try {
                        // This loop will go through all the results and add marker on each location.
                        for (int i = 0; i < placesResults.getResults().size(); i++) {
                            Double lat = placesResults.getResults().get(i).getGeometry().getLocation().getLat();
                            Double lng = placesResults.getResults().get(i).getGeometry().getLocation().getLng();
                            String placeName = placesResults.getResults().get(i).getName();
                            String vicinity = placesResults.getResults().get(i).getVicinity();
                            MarkerOptions markerOptions = new MarkerOptions();
                            LatLng latLng = new LatLng(lat, lng);
                            // Position of Marker on Map
                            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_restaurant_24));
                           // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_foreground));
                            markerOptions.position(latLng);
                            // Adding Title to the Marker
                            markerOptions.title(placeName + " : " + vicinity);
                            Bitmap bitmap = getBitmapFromVectorDrawable(getContext(),R.drawable.ic_restaurant_pin);
                            BitmapDescriptor mapIcon =BitmapDescriptorFactory.fromBitmap(bitmap);
                            // Adding Marker to the Camera.
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(placeName)
                                    .snippet(vicinity)
                                    .icon(mapIcon)

                                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

                            );
                            // Adding colour to the marker
                            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            // move map camera
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
                        }
                    } catch (Exception e) {
                        Log.d("onResponse", "There is an error");
                        e.printStackTrace();
                    }
                }
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
                            build_retrofit_and_get_response(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        } else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(latitude, longitude), DEFAULT_ZOOM));
                            build_retrofit_and_get_response(latitude, longitude);
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        build_retrofit_and_get_response(mDefaultLocation.latitude, mDefaultLocation.longitude);
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);

                    }
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

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable =  AppCompatResources.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}