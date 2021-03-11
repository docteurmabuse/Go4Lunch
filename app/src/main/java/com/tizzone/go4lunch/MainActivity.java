package com.tizzone.go4lunch;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityMainBinding;
import com.tizzone.go4lunch.databinding.NavHeaderMainBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.ui.MainFragmentFactory;
import com.tizzone.go4lunch.ui.MainNavHostFragment;
import com.tizzone.go4lunch.ui.auth.AuthActivity;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.ui.settings.SettingsActivity;
import com.tizzone.go4lunch.utils.Utils;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.tizzone.go4lunch.utils.Constants.RESTAURANT;
import static com.tizzone.go4lunch.utils.Constants.TAG;

@AndroidEntryPoint
public class MainActivity extends BaseActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavHeaderMainBinding navHeaderMainBinding;
    @Inject
    public MainFragmentFactory fragmentFactory;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.tizzone.go4lunch.databinding.ActivityMainBinding mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mBinding.setNavigationItemSelectedListener(this);
        View view = mBinding.getRoot();
        setContentView(view);
        MainNavHostFragment navHostFragment =
                (MainNavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
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
                Restaurant lunchSpot = Utils.getRestaurantFromSharedPreferences(view.getContext());
                if (lunchSpot.getUid() != null) {
                    viewRestaurantDetail(lunchSpot);
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


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic("lunch")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END subscribe_topics]


        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

    }

    private void launchSettingsActivity() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    private void viewRestaurantDetail(Restaurant restaurant) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESTAURANT, restaurant);
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


}