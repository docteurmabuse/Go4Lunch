package com.tizzone.go4lunch.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PermissionsManager {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 4348;
    private boolean locationPermissionGranted;


    public Boolean isLocationPermissionGranted(Context context) {
        return isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public boolean isPermissionGranted(Context context, String accessFineLocation) {
        return ContextCompat.checkSelfPermission(context, accessFineLocation) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(Fragment fragment) {
        requestPermission(fragment);
    }

    private void requestPermission(Fragment fragment) {
        fragment.requestPermissions(new String[]{(Manifest.permission.ACCESS_FINE_LOCATION)}, PermissionsManager.LOCATION_PERMISSION_REQUEST_CODE);
    }

    // [START maps_current_place_location_permission]
    private void getLocationPermission(Activity activity, Context context) {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    // [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     */
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
    }

    public void requestAccessFineLocationPermission(AppCompatActivity activity, int requestId) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{(Manifest.permission.ACCESS_FINE_LOCATION)},
                requestId
        );
    }

    /**
     * Function to check if the location permissions are granted or not
     */
    public boolean isAccessFineLocationGranted(Context context) {
        return ContextCompat
                .checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Function to check if location of the device is enabled or not
     */
    public boolean isLocationEnabled(Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
