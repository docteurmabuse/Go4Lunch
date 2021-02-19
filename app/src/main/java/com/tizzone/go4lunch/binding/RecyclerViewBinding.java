package com.tizzone.go4lunch.binding;

import android.view.View;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.tizzone.go4lunch.adapters.PlacesListAdapter;
import com.tizzone.go4lunch.models.Restaurant;

import java.util.List;

public class RecyclerViewBinding {

    @BindingAdapter(value = {"adapterRestaurantList", "location"}, requireAll = false)
    public static void bindAdapterRestaurantList(RecyclerView recyclerView, List<Restaurant> restaurantList, LatLng currentLocation) {
        if (restaurantList != null) {
            PlacesListAdapter adapter = new PlacesListAdapter();
            recyclerView.setAdapter(adapter);
            adapter.setPlaces(restaurantList, currentLocation);
        }
    }


    @BindingAdapter("gone")
    public static void bindGone(View view, Boolean isGone) {
        if (isGone) view.setVisibility(View.INVISIBLE);
        else {
            view.setVisibility(View.VISIBLE);
        }
    }
}
