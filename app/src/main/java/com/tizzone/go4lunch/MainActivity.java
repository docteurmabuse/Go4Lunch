package com.tizzone.go4lunch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityMainBinding;
import com.tizzone.go4lunch.databinding.NavHeaderMainBinding;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.ui.MainFragmentFactory;
import com.tizzone.go4lunch.ui.MainNavHostFragment;
import com.tizzone.go4lunch.ui.auth.AuthActivity;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.ui.settings.SettingsActivity;
import com.tizzone.go4lunch.utils.UserHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.tizzone.go4lunch.utils.Constants.lunchSpotId;
import static com.tizzone.go4lunch.utils.Constants.myPreference;

@AndroidEntryPoint
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;
    private AppBarConfiguration mAppBarConfiguration;
    private NavHeaderMainBinding navHeaderMainBinding;
    private SharedPreferences sharedPreferences;

    @Inject
    public MainFragmentFactory fragmentFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initNotifications();
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mBinding.setNavigationItemSelectedListener(this);
        View view = mBinding.getRoot();
        setContentView(view);

        MainNavHostFragment navHostFragment =
                (MainNavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        sharedPreferences = getSharedPreferences(myPreference,
                Context.MODE_PRIVATE);

        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
        DrawerLayout drawer = mBinding.drawerLayout;
        NavigationView navigationView = mBinding.drawerNavView;
        BottomNavigationView bottomNavigationView = mBinding.bottomNavView;

        String uid = this.getCurrentUser().getUid();

        mAppBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_map) {
                navController.navigate(id);
            } else if (id == R.id.navigation_list) {
                navController.navigate(id);
            } else if (id == R.id.navigation_workmates) {
                Bundle bundle = new Bundle();
                bundle.putString("userId", uid);
                navController.navigate(id, bundle);
            }
            return true;
        });
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_logout) {
                signOutUserFromFirebase();
            }
            if (id == R.id.nav_lunch) {
                if (sharedPreferences.contains(lunchSpotId)) {
                    String spotId = (sharedPreferences.getString(lunchSpotId, ""));
                    if (spotId != null) {
                        viewRestaurantDetail(spotId);
                    } else {
                        Snackbar.make(mBinding.getRoot(), "You didn't choose any lunch spot yet!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
            if (id == R.id.nav_settings) {
                launchSettingsActivity();
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        View headerView = mBinding.drawerNavView.getHeaderView(0);

        navHeaderMainBinding = NavHeaderMainBinding.bind(headerView);
        updateProfileWhenCreating();
    }

    private void initNotifications() {
        // Handle possible data accompanying notification message.
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                });
    }

    private void launchSettingsActivity() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    private void viewRestaurantDetail(String restaurantId) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        intent.putExtra("RESTAURANT", restaurantId);
        startActivity(intent);
    }

    private void updateProfileWhenCreating() {
        if (this.getCurrentUser() != null) {
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    User user = documentSnapshot.toObject(User.class);
                    navHeaderMainBinding.setUser(user);
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> startMainActivity());
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}