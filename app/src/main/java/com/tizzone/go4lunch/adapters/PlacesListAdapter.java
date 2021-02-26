package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.PlaceItemBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.utils.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.ViewHolder> {

    private static final String TAG = "ERROR";
    private List<Restaurant> mPlaces;
    private PlaceItemBinding binding;
    private LatLng currentLocation;
    private Context context;
    private UserHelper userHelper;

    @Inject
    public PlacesListAdapter(UserHelper userHelper) {
        this.userHelper = userHelper;
    }

    public PlacesListAdapter() {
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.place_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Restaurant place = mPlaces.get(position);
        Resources resources = context.getResources();
        holder.placeItemBinding.setRestaurant(place);
        getUsersCountFromFirestore(place.getUid(), holder);

        if (place.getLocation() != null) {
            int mDistance = (int) Math.floor(SphericalUtil.computeDistanceBetween(currentLocation, place.getLocation()));
            holder.distance.setText(resources.getString(R.string.distance, mDistance));
        }


        holder.itemView.setOnClickListener(view -> {
            final Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, PlaceDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("RESTAURANT", place);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }


    private void getUsersCountFromFirestore(String placeId, ViewHolder holder) {
        UserHelper.getUsersLunchSpot(placeId).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listener failed.", error);
                return;
            }
            assert value != null;
            int usersCount = value.size();
            holder.workmatesCount.setText("(" + usersCount + ")");
        });
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
        public TextView workmatesCount;
        public TextView distance;
        private final PlaceItemBinding placeItemBinding;

        public ViewHolder(PlaceItemBinding placeItemBinding) {
            super(placeItemBinding.getRoot());
            this.placeItemBinding = placeItemBinding;
            distance = placeItemBinding.distanceTextView;
            workmatesCount = placeItemBinding.workmatesCount;
        }
    }

}

