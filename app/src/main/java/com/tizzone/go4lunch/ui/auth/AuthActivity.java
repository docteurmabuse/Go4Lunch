package com.tizzone.go4lunch.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tizzone.go4lunch.MainActivity;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.api.UserHelper;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityAuthBinding;

import java.util.Arrays;
import java.util.List;

public class AuthActivity extends BaseActivity {
    private static final int RC_SIGN_IN = 9903;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private List<AuthUI.IdpConfig> providers;
    private ActivityAuthBinding mBinding;

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAuthBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        init();
    }

//    @Override
//    public int getFragmentLayout() {
//        return R.layout.activity_auth;
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.isCurrentUserLogged()) {
            updateUserIsAuthenticatedInFirestore();
            this.startBottomNavigationActivity();
        } else {
            init();
        }
    }

    private void init() {
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.fragment_auth)
                .setGoogleButtonId(R.id.google_signin)
                .setFacebookButtonId(R.id.facebook_signin)
                .setEmailButtonId(R.id.email_signin)
                // .setTosAndPrivacyPolicyId(R.id.baz)
                .build();

        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                //  new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());
        //  new AuthUI.IdpConfig.TwitterBuilder().build());

        mFirebaseAuth = FirebaseAuth.getInstance(); //Init state of Firebase Auth
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    startBottomNavigationActivity();
                    showSnackBar("You're already logged in with uid: " + user.getUid());
                    Toast.makeText(AuthActivity.this, "You're already logged in with uid: " + user.getUid(), Toast.LENGTH_SHORT);

                } else {
                    //Login
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAuthMethodPickerLayout(customLayout)
                            .setLogo(R.drawable.ic_logo_go4lunch)
                            .setTheme(R.style.LoginTheme)
                            .setIsSmartLockEnabled(false, true)
                            .setAvailableProviders(providers) //EMAIL
                            .build(), RC_SIGN_IN);
                }
            }

        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                createUserInFirestore();
                startBottomNavigationActivity();

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                showSnackBar("Signing error. Code:" + response.getError().getErrorCode());
            }
        }
    }

    // 2 - Show Snack Bar with a message
    private void showSnackBar(String message) {
        Snackbar.make(mBinding.mainLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void startBottomNavigationActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void createUserInFirestore() {

        if (this.getCurrentUser() != null) {

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();

            UserHelper.createUser(uid, true, username, urlPicture, null, null).addOnFailureListener(this.onFailureListener());
        }
    }

    private void updateUserIsAuthenticatedInFirestore() {

        if (this.getCurrentUser() != null) {
            String uid = this.getCurrentUser().getUid();
            UserHelper.updateIsAuthenticated(true, uid).addOnFailureListener(this.onFailureListener());
        }
    }
}