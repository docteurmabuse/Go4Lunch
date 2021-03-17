package com.tizzone.go4lunch;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityMainBinding;
import com.tizzone.go4lunch.databinding.NavHeaderMainBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.ui.MainFragmentFactory;
import com.tizzone.go4lunch.ui.MainNavHostFragment;
import com.tizzone.go4lunch.ui.auth.AuthActivity;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.ui.settings.SettingsActivity;
import com.tizzone.go4lunch.utils.Permissions;
import com.tizzone.go4lunch.utils.Utils;
import com.tizzone.go4lunch.viewmodels.LocationViewModel;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;
import static com.tizzone.go4lunch.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.tizzone.go4lunch.utils.Constants.RESTAURANT_ID;
import static com.tizzone.go4lunch.utils.Utils.getRadiusFromSharedPreferences;


@AndroidEntryPoint
public class MainActivity extends BaseActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavHeaderMainBinding navHeaderMainBinding;
    private ActivityMainBinding mBinding;
    @Inject
    public MainFragmentFactory fragmentFactory;
    @Inject
    public List<Restaurant> restaurantsList;
    private UserViewModel userViewModel;

    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private NavController navController;
    private PlacesViewModel placesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mBinding.setNavigationItemSelectedListener(this);
        placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

        View view = mBinding.getRoot();
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(view);
        MainNavHostFragment navHostFragment =
                (MainNavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
        DrawerLayout drawer = mBinding.drawerLayout;
        NavigationView navigationView = mBinding.drawerNavView;
        BottomNavigationView bottomNavigationView = mBinding.bottomNavView;
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
                if (this.getCurrentUser() != null)
                    bundle.putString("userId", this.getCurrentUser().getUid());
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
                String lunchSpotId = Utils.getRestaurantIdFromSharedPreferences(view.getContext());
                if (lunchSpotId != null) {
                    Log.d(ContentValues.TAG, lunchSpotId);
                    viewRestaurantDetail(lunchSpotId);
                } else {
                    Snackbar.make(mBinding.getRoot(), R.string.no_lunch_spot_notification, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
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
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        if (this.getCurrentUser() != null) {
            userViewModel.getUserInfoFromFirestore(this.getCurrentUser().getUid());
        }
        updateProfileWhenCreating();
        checkGooglePlayService();
        getLocationPermission();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), this::onActivityResult);

    private void onActivityResult(Boolean isGranted) {
        if (isGranted) {
            mLocationPermissionGranted = true;
            Utils.addIsGrantedInSharedPreferences(getApplicationContext(), true);
            getDeviceLocation();
            navController.navigate(R.id.navigation_map);
        } else {
            showInContextUI();
        }
    }

    private void getLocationPermission() {
        Permissions.checkLocationPermission(this);
        if (Permissions.checkLocationPermission(this)) {
            mLocationPermissionGranted = true;
            //Utils.addIsGrantedInSharedPreferences(getApplicationContext(), true);
            getDeviceLocation();
            navController.navigate(R.id.navigation_map);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale()) {
                showInContextUI();
            } else {
                requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    private boolean shouldShowRequestPermissionRationale() {
        return false;
    }

    private void showInContextUI() {
        showSnackBar(mBinding.getRoot(), getString(R.string.permissions_denied_text));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted. Continue the workflow
                navController.navigate(R.id.navigation_map);
            } else {
                mLocationPermissionGranted = false;
                Utils.addIsGrantedInSharedPreferences(getApplicationContext(), false);
                showInContextUI();
            }
        }
    }

    private void launchSettingsActivity() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    private void viewRestaurantDetail(String lunchSpotId) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(RESTAURANT_ID, lunchSpotId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void updateProfileWhenCreating() {
        userViewModel.getCurrentUser().observe(this, user -> navHeaderMainBinding.setUser(user));
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

    private void checkGooglePlayService() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            Log.e(ContentValues.TAG, "Error");
            showSnackBar(mBinding.getRoot(), getString(R.string.common_google_play_services_updating_text));
        } else {
            Log.i(ContentValues.TAG, "Google play services updated");
        }
    }

    private void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
                            setCurrentLocation(mLastKnownLocation);
                        }
                    } else {
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void setCurrentLocation(Location mLastKnownLocation) {
        Utils.addSpotLocationInSharedPreferences(this, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        locationViewModel.setUserLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        int radius = getRadiusFromSharedPreferences(this);
        placesViewModel.setRestaurants(mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude(), radius);
        //placesViewModel.setFakeRestaurantList();
    }
}