package com.tizzone.go4lunch.binding;

import android.view.View;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.tizzone.go4lunch.adapters.PlacesListAdapter;
import com.tizzone.go4lunch.models.Restaurant;

import java.util.List;

public class RecyclerViewBinding {

    @BindingAdapter("adapterRestaurantList")
    public static void bindAdapterRestaurantList(RecyclerView recyclerView, List<Restaurant> restaurantList) {
        if (restaurantList != null) {
            recyclerView.setAdapter(new PlacesListAdapter(restaurantList));
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
