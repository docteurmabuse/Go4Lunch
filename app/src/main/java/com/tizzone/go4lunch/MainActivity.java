package com.tizzone.go4lunch;

import android.content.Intent;
import android.os.Bundle;

import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.ui.authentification.AuthActivity;

public class MainActivity extends BaseActivity {


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onResume();
        if (this.isCurrentUserLogged()) {
            this.startBottomNavigationActivity();
        } else {
            this.startAuthActivity();
        }
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startBottomNavigationActivity() {
        Intent intent = new Intent(this, BottomNavigationActivity.class);
        startActivity(intent);
    }

    private void startAuthActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }


}