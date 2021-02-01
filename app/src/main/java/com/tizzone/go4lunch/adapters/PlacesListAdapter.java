package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.PlaceItemBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.utils.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.ViewHolder> {

    private static final String TAG = "ERROR";
    private String mKey;
    private List<Restaurant> mPlaces;
    public static final String DETAIL_PLACE = "detailPlace";
    private final Context mContext;
    private PlaceItemBinding binding;
    private LatLng currentLocation;
    private Context context;


    public PlacesListAdapter(List<Restaurant> mPlaces, LatLng currentLocation, Context mContext) {
        this.mPlaces = mPlaces;
        this.mContext = mContext;
        this.currentLocation = currentLocation;
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
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
        Float rating = place.getRating();

        if (rating != null) {
            holder.ratingBar.setRating(rating);
        }

//        if (place.isOpen_now()=!null) {
//            Boolean isOpen = place.isOpen_now();
//        }
        if (place.getLocation() != null) {
            int mDistance = (int) Math.floor(SphericalUtil.computeDistanceBetween(currentLocation, place.getLocation()));
            holder.distance.setText(resources.getString(R.string.distance, mDistance));
        }


        String imageUrl = place.getPhotoUrl();

        Glide.with(holder.itemView)
                .load(imageUrl)
                .into(holder.imageViewPhoto);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, PlaceDetailActivity.class);
                intent.putExtra("RESTAURANT", place);
                context.startActivity(intent);
            }
        });
    }


    private void getUsersCountFromFirestore(String placeId, ViewHolder holder) {
        UserHelper.getUsersLunchSpot(placeId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listener failed.", error);
                    return;
                }
                assert value != null;
                int usersCount = value.size();
                holder.workmatesCount.setText("(" + usersCount + ")");
            }
        });
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
        notifyDataSetChanged();
    }

    public void setPlaces(List<Restaurant> restaurants, LatLng currentLocation) {
       // this.mPlaces.clear();
        this.mPlaces = restaurants;
        this.currentLocation = currentLocation;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mPlaces == null ? 0 : mPlaces.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView workmatesCount;
        public TextView textViewName;
        public TextView textViewAddress;
        public TextView textViewOpeningHours;
        public TextView distance;
        public ImageView imageViewPhoto;
        public RatingBar ratingBar;
        private final PlaceItemBinding placeItemBinding;

        public ViewHolder(PlaceItemBinding placeItemBinding) {
            super(placeItemBinding.getRoot());
            this.placeItemBinding = placeItemBinding;
            textViewName = placeItemBinding.textViewName;
            textViewAddress = placeItemBinding.textViewAddress;
            textViewOpeningHours = placeItemBinding.textViewOpeningHours;
            imageViewPhoto = placeItemBinding.imageViewPhoto;
            ratingBar = placeItemBinding.rating;
            distance = placeItemBinding.distanceTextView;
            workmatesCount = placeItemBinding.workmatesCount;
        }
    }

}

