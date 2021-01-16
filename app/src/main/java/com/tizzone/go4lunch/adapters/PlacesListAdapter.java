package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.api.UserHelper;
import com.tizzone.go4lunch.databinding.PlaceItemBinding;
import com.tizzone.go4lunch.models.places.Result;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.ViewHolder> {

    private static final String TAG = "ERROR";
    private String mKey;
    private List<Result> mPlaces = new ArrayList<>();
    public static final String DETAIL_PLACE = "detailPlace";
    private final Context mContext;
    private PlaceItemBinding binding;
    private LatLng currentLocation;
    private Context context;


    public PlacesListAdapter(List<Result> mPlaces, String key, Context mContext, LatLng currentLocation) {
        this.mPlaces = mPlaces;
        this.mContext = mContext;
        this.mKey = key;
        this.currentLocation = currentLocation;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Result place = mPlaces.get(position);
        binding.setResult(place);
        getUsersCountFromFirestore(place.getPlaceId(), holder);
        //holder.textViewName.setText(place.getName());
        //holder.textViewAddress.setText(place.getVicinity());
        Float ratingFiveStar = place.getRating();
        if (place.getRating() != null) {
            holder.ratingBar.setRating(ratingFiveStar);
        }

        if (mPlaces.get(position).getOpeningHours() != null) {
            boolean isOpen = mPlaces.get(position).getOpeningHours().getOpenNow();
            //   if (isOpen)
            //   holder.textViewOpeningHours.setText("Open Now");
            //  else holder.textViewOpeningHours.setText("Closed");
        }

        LatLng placeLocation = new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());
        int mDistance = (int) Math.floor(SphericalUtil.computeDistanceBetween(currentLocation, placeLocation));
        Resources resources = context.getResources();
        holder.distance.setText(resources.getString(R.string.distance, mDistance));

        String staticUrl = "https://maps.googleapis.com/maps/api/place/photo?";

        //display place thumbnail
        if (mPlaces.get(position).getPhotos().size() != 0) {
            String imageUrl = staticUrl + "maxwidth=400&photoreference=" + mPlaces.get(position).getPhotos().get(0).getPhotoReference() + "&key=" + mKey;

            Glide.with(holder.itemView)
                    .load(imageUrl)
                    .into(holder.imageViewPhoto);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = holder.itemView.getContext();
                Bundle arguments = new Bundle();
                String imageUrl;
                Intent intent = new Intent(context, PlaceDetailActivity.class);
                intent.putExtra("placeName", place.getName());
                intent.putExtra("placeId", place.getPlaceId());
                intent.putExtra("placeAddress", place.getVicinity());
                if (place.getPhotos().size() > 0) {
                    String photoReference = place.getPhotos().get(0).getPhotoReference();
                    imageUrl = staticUrl + "maxwidth=400&photoreference=" + photoReference + "&key=" + mKey;
                } else {
                    imageUrl = null;
                }
                intent.putExtra("placePhotoUrl", imageUrl);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        binding = PlaceItemBinding.inflate(inflater, parent, false);
        context = parent.getContext();
        return new ViewHolder(binding);

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

    public void setPlaces(List<Result> results, String key) {
        this.mPlaces = results;
        this.mKey = key;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView workmatesCount;
        public TextView textViewName;
        public TextView textViewAddress;
        public TextView textViewOpeningHours;
        public TextView distance;
        public ImageView imageViewPhoto;
        public RatingBar ratingBar;

        public ViewHolder(PlaceItemBinding placeItemBinding) {
            super(placeItemBinding.getRoot());
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

