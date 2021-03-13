package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.PlaceItemBinding;
import com.tizzone.go4lunch.models.Restaurant;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.tizzone.go4lunch.utils.Utils.getDistanceFromRestaurant;

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.ViewHolder> {

    private List<Restaurant> mPlaces;
    private LatLng currentLocation;
    private Context context;
    private final RestaurantItemClickListener mListener;

    public PlacesListAdapter(RestaurantItemClickListener mListener) {
        this.mListener = mListener;
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        com.tizzone.go4lunch.databinding.PlaceItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.place_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Restaurant place = mPlaces.get(position);
        Resources resources = context.getResources();
        holder.placeItemBinding.setRestaurant(place);
        if (place.getLocation() != null) {
            int mDistance = getDistanceFromRestaurant(currentLocation, place.getLocation());
            holder.distance.setText(resources.getString(R.string.distance, mDistance));
        }
        holder.placeItemBinding.setRestaurantItemClick(mListener);
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
        notifyDataSetChanged();
    }

    public void setPlaces(List<Restaurant> restaurants, LatLng currentLocation) {
        this.mPlaces = restaurants;
        this.currentLocation = currentLocation;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mPlaces == null ? 0 : mPlaces.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView distance;
        private final PlaceItemBinding placeItemBinding;

        public ViewHolder(PlaceItemBinding placeItemBinding) {
            super(placeItemBinding.getRoot());
            this.placeItemBinding = placeItemBinding;
            distance = placeItemBinding.distanceTextView;
        }
    }

    public interface RestaurantItemClickListener {
        void onRestaurantClick(String lunchSpotId);
    }
}

