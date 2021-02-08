package com.tizzone.go4lunch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.snackbar.Snackbar;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityMainBinding;
import com.tizzone.go4lunch.databinding.NavHeaderMainBinding;
import com.tizzone.go4lunch.ui.auth.AuthActivity;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.ui.settings.SettingsActivity;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends BaseActivity {
    private static final int SIGN_OUT_TASK = 25;
    private ActivityMainBinding mBinding;
    private AppBarConfiguration mAppBarConfiguration;
    private NavHeaderMainBinding navHeaderMainBinding;
    public static final String lunchSpotId = "lunchSpotId";
    public static final String myPreference = "mypref";
    private SharedPreferences sharedPreferences;
    private PlacesViewModel placesViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

        // Declare a StorageReference and initialize it in the onCreate method
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        //  mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        mBinding.setNavigationItemSelectedListener(this);

        View view = mBinding.getRoot();
        setContentView(view);


        sharedPreferences = getSharedPreferences(myPreference,
                Context.MODE_PRIVATE);


        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
        DrawerLayout drawer = mBinding.drawerLayout;
        NavigationView navigationView = mBinding.drawerNavView;
        BottomNavigationView bottomNavigationView = mBinding.bottomNavView;

        String uid = this.getCurrentUser().getUid();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.my_graph);
        placesViewModel = new ViewModelProvider(backStackEntry).get(PlacesViewModel.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId", uid);
        mAppBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navController.setGraph(R.navigation.mobile_navigation, bundle);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });
        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem menuItem) {
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
            }
        });

        View headerView = mBinding.drawerNavView.getHeaderView(0);

        navHeaderMainBinding = NavHeaderMainBinding.bind(headerView);
        updateProfileWhenCreating();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }
}