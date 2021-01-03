package com.tizzone.go4lunch;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityMainBinding;
import com.tizzone.go4lunch.databinding.NavHeaderMainBinding;
import com.tizzone.go4lunch.databinding.AppBarMainBinding;
import com.tizzone.go4lunch.models.places.Result;
import com.tizzone.go4lunch.ui.auth.AuthActivity;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;

import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity implements OnNavigationItemSelectedListener{
    private static final int SIGN_OUT_TASK = 25;
    private ActivityMainBinding mBinding;
    private AppBarConfiguration mAppBarConfiguration;
    private Location lastKnownLocation;
    private ListView listViewPlaces;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 546;
    private final LatLng mDefaultLocation = new LatLng(48.850559, 2.377078);
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private List<Result> places;
    private String key;
    private MutableLiveData<List<Result>> placesList;
    private PlacesViewModel placesViewModel;
    private StorageReference mStorageRef;
    private NavHeaderMainBinding navHeaderMainBinding;
    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Declare a StorageReference and initialize it in the onCreate method
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
      //  mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        mBinding.setNavigationItemSelectedListener(this);

        View view = mBinding.getRoot();
        setContentView(view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = mBinding.drawerLayout;
        NavigationView navigationView = mBinding.drawerNavView;
        BottomNavigationView bottomNavigationView = mBinding.bottomNavView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        mAppBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setOpenableLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_logout) {
                    signOutUserFromFirebase();
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        listViewPlaces = findViewById(R.id.listViewPlaces);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        key = getText(R.string.google_maps_key).toString();
        View headerView = mBinding.drawerNavView.getHeaderView(0);

        navHeaderMainBinding = NavHeaderMainBinding.bind(headerView);
        updateProfileWhenCreating();
    }

    private void updateProfileWhenCreating() {
        if (this.getCurrentUser() != null) {
            (navHeaderMainBinding.profileName.setText(this.getCurrentUser().getDisplayName());
            navHeaderMainBinding.profileEmail.setText(this.getCurrentUser().getEmail());
            if (this.getCurrentUser().getDisplayName() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(navHeaderMainBinding.profilePicture);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);

        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));

        return true;
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
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startMainActivity();
                    }
                });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}