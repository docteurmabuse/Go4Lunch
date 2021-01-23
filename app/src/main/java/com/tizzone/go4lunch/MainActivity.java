package com.tizzone.go4lunch;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tizzone.go4lunch.api.UserHelper;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityMainBinding;
import com.tizzone.go4lunch.databinding.NavHeaderMainBinding;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.models.places.Result;
import com.tizzone.go4lunch.ui.auth.AuthActivity;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;

import java.util.List;

public class MainActivity extends BaseActivity {
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
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
        DrawerLayout drawer = mBinding.drawerLayout;
        NavigationView navigationView = mBinding.drawerNavView;
        BottomNavigationView bottomNavigationView = mBinding.bottomNavView;
        String uid = this.getCurrentUser().getUid();


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // navController.getGraph().findNode(R.id.navigation_workmates).addArgument("userId", );
        Bundle bundle = new Bundle();
        bundle.putString("userId", uid);
        mAppBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setOpenableLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navController.setGraph(R.navigation.mobile_navigation, bundle);
        // bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        //Navigation.findNavController(view).navigate(R.id.navigation_workmates, bundle);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_map) {
                    navController.navigate(id);
                } else if (id == R.id.navigation_list) {
                    navController.navigate(id);
                } else if (id == R.id.navigation_workmates) {
                    NavArgument argument = new NavArgument.Builder().setDefaultValue(uid).build();
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", uid);
                    navController.navigate(id, bundle);
                }
                return true;
            }
        });
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
        ///listViewPlaces = findViewById(R.id.listViewPlaces);
        key = getText(R.string.google_maps_key).toString();
        View headerView = mBinding.drawerNavView.getHeaderView(0);

        navHeaderMainBinding = NavHeaderMainBinding.bind(headerView);
        updateProfileWhenCreating();
    }


    private void updateProfileWhenCreating() {
        if (this.getCurrentUser() != null) {
            navHeaderMainBinding.profileName.setText(this.getCurrentUser().getDisplayName());
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

    private void getUserInFirestore() {
        if (this.getCurrentUser() != null) {
            String uid = this.getCurrentUser().getUid();
            // 5 - Get additional data from Firestore
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }


}