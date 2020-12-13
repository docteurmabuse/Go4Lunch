package com.tizzone.go4lunch.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.models.places.Result;

import java.io.InputStream;
import java.util.List;

public class PlacesListAdapters extends ArrayAdapter<Result> {


    private Context context;
    private List<Result> results;

    public PlacesListAdapters(@NonNull Context context, int resource, @NonNull List<Result> results) {
        super(context, R.layout.place_item, results);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        try {
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.place_item, null);
                viewHolder.textViewName = view.findViewById(R.id.textViewName);
                viewHolder.textViewAddress = view.findViewById(R.id.textViewAddress);
                viewHolder.imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            Result place = results.get(position);
            viewHolder.textViewName.setText(place.getName());
            viewHolder.textViewAddress.setText(place.getVicinity());
            Bitmap photo = new ImageRequestAsk().execute(place.getIcon()).get();

            viewHolder.imageViewPhoto.setImageBitmap(photo);
            return view;
        } catch (Exception e) {
            return null;
        }
    }

    public static class ViewHolder {
        public TextView textViewName;
        public TextView textViewAddress;
        public ImageView imageViewPhoto;
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
}

