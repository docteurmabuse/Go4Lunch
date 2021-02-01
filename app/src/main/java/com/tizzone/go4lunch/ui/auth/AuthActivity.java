package com.tizzone.go4lunch.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.tizzone.go4lunch.MainActivity;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityAuthBinding;
import com.tizzone.go4lunch.utils.UserHelper;

import java.util.Arrays;
import java.util.List;

public class AuthActivity extends BaseActivity {
    private static final int RC_SIGN_IN = 9903;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private List<AuthUI.IdpConfig> providers;
    private ActivityAuthBinding mBinding;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
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
        coordinatorLayout = mBinding.mainLayout;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.isCurrentUserLogged()) {
            updateUserIsAuthenticatedInFirestore();
            this.startMainActivity();
        } else {
            startSignInActivity();
        }
    }

    private void init() {

    }

    private void startSignInActivity() {
        //Login
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

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(customLayout)
                .setLogo(R.drawable.ic_logo_go4lunch)
                .setTheme(R.style.LoginTheme)
                .setIsSmartLockEnabled(false, true)
                .setAvailableProviders(providers) //EMAIL
                .build(), RC_SIGN_IN);
    }

    // 2 - Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void startMainActivity() {
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
            String userEmail = this.getCurrentUser().getEmail();
            UserHelper.createUser(uid, userEmail, username, urlPicture, null, null).addOnFailureListener(this.onFailureListener());
        }
    }

    private void updateUserIsAuthenticatedInFirestore() {

        if (this.getCurrentUser() != null) {
            String uid = this.getCurrentUser().getUid();
            UserHelper.updateIsAuthenticated(true, uid).addOnFailureListener(this.onFailureListener());
        }
    }

    // --------------------
    // UTILS
    // --------------------

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createUserInFirestore();
                this.startMainActivity();
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }
}