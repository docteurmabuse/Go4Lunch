package com.tizzone.go4lunch.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.setContentView(this.getFragmentLayout());

        super.onCreate(savedInstanceState);
    }

    public abstract int getFragmentLayout();
}
