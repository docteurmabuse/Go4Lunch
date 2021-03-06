package com.tizzone.go4lunch.binding;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;

public class ViewBinding {
    @BindingAdapter(value = {"imageFromUrl", "placeHolder", "progressBar"}, requireAll = false)
    public static void bindImageFromUrl(AppCompatImageView imageView, String imageFromUrl, Drawable placeHolder, ProgressBar progressBar) {
        if (imageFromUrl != null && imageFromUrl.startsWith("http")) {
            Picasso.with(imageView.getContext()).load(imageFromUrl).fit().centerCrop()
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            imageView.setImageDrawable(placeHolder);
            progressBar.setVisibility(View.GONE);
        }
    }

    @BindingAdapter(value = {"circleImageFromUrl", "circlePlaceholder", "progressBar"}, requireAll = false)
    public static void bindRoundImageFromUrl(AppCompatImageView imageView, String imageFromUrl, Drawable placeHolder, ProgressBar progressBar) {
        if (imageFromUrl != null) {
            Glide.with(imageView.getContext())
                    .load(imageFromUrl)
                    .circleCrop()
                    // .placeholder(placeHolder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            progressBar.setVisibility(View.GONE);
            imageView.setImageDrawable(placeHolder);
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

    @BindingAdapter(value = {"isTrue", "placeHolderTrue", "placeHolderFalse"})
    public static void bindFloatingActionButton(FloatingActionButton floatingActionButton, Boolean isTrue, Drawable placeHolderTrue, Drawable placeHolderFalse) {
        if (isTrue != null && !isTrue) {
            floatingActionButton.setImageDrawable(placeHolderFalse);
        } else {
            floatingActionButton.setImageDrawable(placeHolderTrue);
        }
    }

    @BindingAdapter(value = {"isTrue", "placeHolderTrue", "placeHolderFalse"})
    public static void bindAppCompatImageButton(AppCompatImageButton appCompatImageButton, Boolean isTrue, Drawable placeHolderTrue, Drawable placeHolderFalse) {
        if (isTrue != null && !isTrue) {
            appCompatImageButton.setBackgroundDrawable(placeHolderFalse);
        } else {
            appCompatImageButton.setBackgroundDrawable(placeHolderTrue);
        }
    }

    @BindingAdapter("gone")
    public static void bindGone(View view, Boolean isGone) {
        if (isGone) view.setVisibility(View.INVISIBLE);
        else {
            view.setVisibility(View.VISIBLE);
        }
    }
}
