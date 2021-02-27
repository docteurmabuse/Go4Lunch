package com.tizzone.go4lunch.utils;

import com.google.android.gms.maps.model.LatLng;
import com.tizzone.go4lunch.BuildConfig;

public class Constants {
    public static final String COLLECTION_NAME = "users";
    public static final String NAME_PROPERTY = "UserName";

    public static final int RC_SIGN_IN = 9903;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 546;
    public static final float DEFAULT_ZOOM = 15;
    public static final int SESSION_TOKEN = 54784;
    public static final int PROXIMITY_RADIUS = 1000;
    public static final LatLng mDefaultLocation = new LatLng(65.850559, 2.377078);
    public static final double LATITUDE = 78.850559;
    public static final double LONGITUDE = 2.377078;
    public static final String TAG = "1543";
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 5873;
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    public static final String CHANNEL_ID = "primary_notification_channel";
    public static final int NOTIFICATION_ID = 0;
    public static final String lunchSpotId = "lunchSpotId";
    public static final String myPreference = "mypref";
    public static final String GOOGLE_MAP_API_KEY = BuildConfig.GOOGLE_MAPS_API_KEY;


}