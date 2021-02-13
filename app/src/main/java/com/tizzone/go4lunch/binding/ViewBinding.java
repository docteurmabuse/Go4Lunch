package com.tizzone.go4lunch.binding;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class ViewBinding {
    @BindingAdapter(value = {"imageFromUrl", "placeholder"}, requireAll = false)
    public static void bindImageFromUrl(ImageView imageView, String imageUrl, Drawable placeHolder) {
        if (imageUrl == null) {
            imageView.setImageDrawable(placeHolder);
        } else {
            Glide.with(imageView.getContext())
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }
    }
}
