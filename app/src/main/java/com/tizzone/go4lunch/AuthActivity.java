package com.tizzone.go4lunch;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tizzone.go4lunch.databinding.ActivityAuthBinding;

import java.util.Arrays;
import java.util.List;

public class AuthActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9903;
    private static final String TAG = "SignInActivity";
    private ActivityAuthBinding mAuthBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.isCurrentUserLogged()) {
            this.startBottomNavigationActivity();
        } else {
            signIn();
        }
    }

    // 2 - Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(mAuthBinding.authLayout, message, Snackbar.LENGTH_SHORT).show();
    }



    private void signIn() {
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.fragment_auth)
                .setGoogleButtonId(R.id.google_signin)
                .setFacebookButtonId(R.id.facebook_signin)
                .setEmailButtonId(R.id.email_signin)
                // .setTosAndPrivacyPolicyId(R.id.baz)
                .build();


        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                //  new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());
        //  new AuthUI.IdpConfig.TwitterBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(customLayout)
                        .setLogo(R.drawable.ic_logo_go4lunch)
                        //.setTheme(R.style.LoginTheme)
                        .setIsSmartLockEnabled(false, true)
                        .setAvailableProviders(providers) //EMAIL
                        .build(), RC_SIGN_IN);
    }

    private void startBottomNavigationActivity() {
        Intent intent = new Intent(this, BottomNavigationActivity.class);
        startActivity(intent);
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }
}