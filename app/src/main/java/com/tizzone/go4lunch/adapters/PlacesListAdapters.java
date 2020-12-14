package com.tizzone.go4lunch.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.models.places.Result;

import java.io.InputStream;
import java.util.List;

public class PlacesListAdapters extends RecyclerView.Adapter<PlacesListAdapters.ViewHolder> {

    private final List<Result> mPlaces;

    public PlacesListAdapters(List<Result> results) {

        mPlaces = results;
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
        Result place = mPlaces.get(position);
        holder.textViewName.setText(place.getName());
        holder.textViewAddress.setText(place.getVicinity());
        //Bitmap photo = new ImageRequestAsk().execute(place.getIcon()).get();
        //holder.imageViewPhoto.setImageBitmap(photo);
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    private class ImageRequestAsk extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                InputStream inputStream = new java.net.URL(params[0]).openStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                return null;
            }
        }
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

