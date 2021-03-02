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
import com.tizzone.go4lunch.models.Restaurant;

import static com.tizzone.go4lunch.utils.Constants.lunchSpotAddress;
import static com.tizzone.go4lunch.utils.Constants.lunchSpotId;
import static com.tizzone.go4lunch.utils.Constants.lunchSpotName;
import static com.tizzone.go4lunch.utils.Constants.lunchSpotPhotoUrl;
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

    public static Restaurant getRestaurantFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(myPreference,
                Context.MODE_PRIVATE);
        Restaurant lunchSpot = new Restaurant();
        if (sharedPreferences.contains(lunchSpotId)) {
            lunchSpot.setUid(sharedPreferences.getString(lunchSpotId, ""));
            lunchSpot.setName(sharedPreferences.getString(lunchSpotName, ""));
            lunchSpot.setAddress(sharedPreferences.getString(lunchSpotAddress, ""));
            lunchSpot.setPhotoUrl(sharedPreferences.getString(lunchSpotPhotoUrl, ""));
        }
        return lunchSpot;
    }

    public static int getDistanceFromRestaurant(LatLng currentLocation, LatLng restaurantLocation) {
        return (int) Math.floor(SphericalUtil.computeDistanceBetween(currentLocation, restaurantLocation));
    }

}
