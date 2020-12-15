package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.models.places.Result;

import java.util.ArrayList;
import java.util.List;

public class PlacesListAdapters extends RecyclerView.Adapter<PlacesListAdapters.ViewHolder> {

    private final Context mContext;
    private final String mKey;
    private List<Result> mPlaces;

    public PlacesListAdapters(List<Result> results, Context context, String key) {
        mPlaces = new ArrayList<>();
        mPlaces = results;
        mContext = context;
        mKey = key;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mPlace = mPlaces.get(position);
        // Result place = mPlaces.get(position);
        holder.textViewName.setText(mPlaces.get(position).getName());
        holder.textViewAddress.setText(mPlaces.get(position).getVicinity());
        String staticUrl = "https://maps.googleapis.com/maps/api/place/photo?";

        //display place thumbnail
        if (mPlaces.get(position).getPhotos().get(0).getPhotoReference() != null) {
            String imageUrl = staticUrl + "maxwidth=400&photoreference=" + mPlaces.get(position).getPhotos().get(0).getPhotoReference() + "&key=" + mKey;

            Glide.with(holder.itemView)
                    .load(imageUrl)
                    .into(holder.imageViewPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public TextView textViewName;
        public TextView textViewAddress;
        public ImageView imageViewPhoto;
        public Result mPlace;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            textViewName = view.findViewById(R.id.textViewName);
            textViewAddress = view.findViewById(R.id.textViewAddress);
            imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        }
    }
}

