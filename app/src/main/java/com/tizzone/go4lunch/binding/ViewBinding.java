package com.tizzone.go4lunch.binding;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;

public class ViewBinding {
    @BindingAdapter(value = {"imageFromUrl", "placeholder"}, requireAll = false)
    public static void bindImageFromUrl(AppCompatImageView imageView, String imageUrl, Drawable placeHolder) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageDrawable(placeHolder);
        } else {
            Glide.with(imageView.getContext())
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }
    }

    @BindingAdapter(value = {"circleImageFromUrl", "circlePlaceholder"}, requireAll = false)
    public static void bindRoundImageFromUrl(AppCompatImageView imageView, String imageUrl, Drawable placeHolder) {
        if (imageUrl == null) {
            imageView.setImageDrawable(placeHolder);
        } else {
            Glide.with(imageView.getContext())
                    .load(imageUrl)
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }
    }

    @BindingAdapter(value = {"userName", "restaurantName"}, requireAll = false)
    public static void bindTextView(AppCompatTextView textView, String userName, String restaurantName) {
        if (textView.getContext() instanceof PlaceDetailActivity) {
            String joiningText = String.format(textView.getResources().getString(R.string.joining_text), userName);
            textView.setText(joiningText);
        } else {
            if (restaurantName == null) {
                String notDecidedYet = String.format(textView.getResources().getString(R.string.not_decided), userName);
                textView.setText(notDecidedYet);
            } else {
                String lunchingText = String.format(textView.getResources().getString(R.string.lunching_text), userName, restaurantName);
                textView.setText(lunchingText);
                textView.setTypeface(null, Typeface.BOLD);
            }
        }

    }
}
