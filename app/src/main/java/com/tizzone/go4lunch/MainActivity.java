package com.tizzone.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 9903;
    private static final String TAG = "SignInActivity";
    private ActivityMainBinding mBinding;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 4 - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // 2 - Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        // 3 - Method that handles response after SignIn Activity close
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.mBinding.mainLayout, getString(R.string.connection_succeed));
                this.startBottomNavigationActivity();
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.mBinding.mainLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.mBinding.mainLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.mBinding.mainLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        super.onResume();
        if (this.isCurrentUserLogged()) {
            this.startBottomNavigationActivity();
        } else {
            // signIn();
            this.startBottomNavigationActivity();

        }
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
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

    private void startBottomNavigationActivity() {
        Intent intent = new Intent(this, BottomNavigationActivity.class);
        startActivity(intent);
    }

    private void signIn() {
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_auth)
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
                        .setTheme(R.style.LoginTheme)
                        .setIsSmartLockEnabled(false, true)
                        .setAvailableProviders(providers) //EMAIL
                        .build(), RC_SIGN_IN);
    }


}