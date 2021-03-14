package com.tizzone.go4lunch.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import static com.tizzone.go4lunch.utils.Constants.latitude;
import static com.tizzone.go4lunch.utils.Constants.longitude;
import static com.tizzone.go4lunch.utils.Constants.lunchSpotId;
import static com.tizzone.go4lunch.utils.Constants.myPreference;


public class Utils {

    public static Bitmap getBitmapFromVectorDrawable(int drawableId, Context context) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
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

    public static String getRestaurantIdFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(myPreference,
                Context.MODE_PRIVATE);
        String restaurantId = "";
        if (sharedPreferences.contains(lunchSpotId)) {
            restaurantId = sharedPreferences.getString(lunchSpotId, "");
        }
        return restaurantId;
    }

    public static LatLng getLocationFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(myPreference,
                Context.MODE_PRIVATE);
        LatLng currentLocation;
        double currentLocationLatitude = 0;
        double currentLocationLongitude = 0;

        if (sharedPreferences.contains(latitude)) {
            currentLocationLatitude = sharedPreferences.getFloat("latitude", (float) 0.2867811);
        }
        if (sharedPreferences.contains(longitude)) {
            currentLocationLongitude = sharedPreferences.getFloat("longitude", (float) 0.204504);
        }
        currentLocation = new LatLng(currentLocationLatitude, currentLocationLongitude);
        return currentLocation;
    }

    public static int getDistanceFromRestaurant(LatLng currentLocation, LatLng restaurantLocation) {
        return (int) Math.floor(SphericalUtil.computeDistanceBetween(currentLocation, restaurantLocation));
    }

    public static float transformFiveStarsIntoThree(float rating) {
        return ((rating * 3) / 5);
    }
}
