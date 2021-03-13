package com.tizzone.go4lunch.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.tizzone.go4lunch.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class Permissions {
    public static boolean checkLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
}
