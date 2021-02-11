package com.tizzone.go4lunch.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.tizzone.go4lunch.R;

public class GalleryFragment extends Fragment {

    private GalleryModel galleryModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryModel =
                new ViewModelProvider(this).get(GalleryModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        return root;
    }
}