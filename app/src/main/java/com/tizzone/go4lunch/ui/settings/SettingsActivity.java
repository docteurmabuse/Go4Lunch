package com.tizzone.go4lunch.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.SettingsActivityBinding;
import com.tizzone.go4lunch.notifications.NotificationHelper;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;
import static com.tizzone.go4lunch.utils.Constants.TITLE_TAG;
import static com.tizzone.go4lunch.utils.Constants.notifications;
import static com.tizzone.go4lunch.utils.Constants.radius;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    public static final Preference.OnPreferenceChangeListener sBindPreferences = (preference, newValue) -> true;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.tizzone.go4lunch.databinding.SettingsActivityBinding mBinding = SettingsActivityBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        Toolbar toolbar = mBinding.toolbar;
        toolbar.setNavigationOnClickListener(mView -> onBackPressed());
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                () -> {
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        setTitle(R.string.title_activity_settings);
                    }
                });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        pref.setOnPreferenceChangeListener(sBindPreferences);
        return true;
    }

    @AndroidEntryPoint
    public static class HeaderFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals(radius)) {
                Log.e(TAG, "Preference value was updated to: " + sharedPreferences.getString(s, ""));
            }
            if (s.equals(notifications)) {
                Log.e(TAG, "Preference value was updated to: " + sharedPreferences.getBoolean(s, true));
                boolean isNotificationEnabled = sharedPreferences.getBoolean(s, true);
                if (isNotificationEnabled) {
                    registerNotification();
                } else {
                    cancelNotification();
                }
            }
        }

        private void registerNotification() {
            NotificationHelper.scheduleRepeatingRTCNotification(requireActivity());
            NotificationHelper.enableBootReceiver(requireActivity());
            NotificationHelper.registerNotificationInFirebase(requireActivity());
        }

        private void cancelNotification() {
            NotificationHelper.cancelAlarm();
            NotificationHelper.disableBootReceiver(requireActivity());
            NotificationHelper.unregisterNotificationInFirebase(requireActivity());

        }


        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }

}