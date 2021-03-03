package com.tizzone.go4lunch.utils;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

import org.hamcrest.core.IsInstanceOf;

import static com.tizzone.go4lunch.utils.Utils.getDistanceFromRestaurant;
import static com.tizzone.go4lunch.utils.Utils.transformFiveStarsIntoThree;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;


public class UtilsTest extends TestCase {

    public void testGetDistanceFromRestaurant() {
        //I am in the restaurant
        LatLng currentLocation = new LatLng(65.850559, 2.377078);
        LatLng restaurantLocation = new LatLng(65.850559, 2.377078);
        int distance = getDistanceFromRestaurant(currentLocation, restaurantLocation);
        assertEquals(0, distance);
        //I am at Home.
        LatLng homeLocation = new LatLng(48.28648, 3.20455);
        int distanceFromNewRestaurant = getDistanceFromRestaurant(homeLocation, restaurantLocation);
        assertNotEquals(0, distanceFromNewRestaurant);
        assertThat(distance, new IsInstanceOf(int.class));
    }

    public void testTransformFiveStarsIntoThree() {
        //We've got 2.5 stars on 5 should be equal 1.5 stars on 3
        assertEquals(transformFiveStarsIntoThree((float) 2.5), (float) 1.5);
        //We've got 5 stars on 5 should be equal 3 stars on 3
        assertEquals(transformFiveStarsIntoThree((float) 5), (float) 3);
    }
}