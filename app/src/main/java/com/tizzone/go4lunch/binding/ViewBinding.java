package com.tizzone.go4lunch.binding;

import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

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
}
