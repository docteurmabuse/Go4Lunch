package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tizzone.go4lunch.databinding.PlaceItemBinding;
import com.tizzone.go4lunch.models.places.Result;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.ViewHolder> {

    private String mKey;
    private List<Result> mPlaces = new ArrayList<>();
    public static final String DETAIL_PLACE = "detailPlace";
    private final Context mContext;
    private PlaceItemBinding binding;

    public PlacesListAdapter(List<Result> mPlaces, String key, Context mContext) {
        this.mPlaces = mPlaces;
        this.mContext = mContext;
        this.mKey = key;
        notifyDataSetChanged();
    }

//    public PlacesListAdapter(List<Result> results, Context context, String key) {
//        mPlaces = new ArrayList<>();
//        mPlaces = results;
//        mContext = context;
//        mKey = key;
//    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        binding = PlaceItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);

//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.place_item, parent, false);
//        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mPlace = mPlaces.get(position);
        Result place = mPlaces.get(position);
        holder.textViewName.setText(place.getName());
        holder.textViewAddress.setText(place.getVicinity());
        Double ratingFiveStar = place.getRating();
        if (place.getRating() != null) {
            float ratingFiveStarFloat = ratingFiveStar.floatValue();
            float ratingThreeStars = (ratingFiveStarFloat * 3) / 5;
            holder.ratingBar.setRating(ratingThreeStars);
        }

        if (mPlaces.get(position).getOpeningHours() != null) {
            boolean isOpen = mPlaces.get(position).getOpeningHours().getOpenNow();
            if (isOpen)
                holder.textViewOpeningHours.setText("Open Now");
            else holder.textViewOpeningHours.setText("Closed");
        }

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
                Intent intent = new Intent(context, PlaceDetailActivity.class);
                intent.putExtra("placeName", place.getName());
                intent.putExtra("placeId", place.getPlaceId());
                intent.putExtra("placeAddress", place.getVicinity());
                String placePhotoUrl = staticUrl + "maxwidth=400&photoreference=" + place.getPhotos().get(0).getPhotoReference() + "&key=" + mKey;
                intent.putExtra("placePhotoUrl", placePhotoUrl);
                context.startActivity(intent);
            }
        });
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
        public TextView textViewName;
        public TextView textViewAddress;
        public TextView textViewOpeningHours;
        public TextView distance;
        public ImageView imageViewPhoto;
        public Result mPlace;
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
        }
    }
}

