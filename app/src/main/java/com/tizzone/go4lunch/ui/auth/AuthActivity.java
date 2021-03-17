package com.tizzone.go4lunch.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.base.BaseActivity;
import com.tizzone.go4lunch.databinding.ActivityAuthBinding;
import com.tizzone.go4lunch.ui.MainActivity;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

import static com.tizzone.go4lunch.utils.Constants.RC_SIGN_IN;

@AndroidEntryPoint
public class AuthActivity extends BaseActivity {
    private CoordinatorLayout coordinatorLayout;
    private UserViewModel userViewModel;
    private ActivityAuthBinding mBinding;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createUserInFirestore();
                this.startMainActivity();
                showSnackBar(mBinding.getRoot(), getString(R.string.connection_succeed));
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(mBinding.getRoot(), getString(R.string.error_authentication_canceled));
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(mBinding.getRoot(), getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(mBinding.getRoot(), getString(R.string.error_unknown_error));
                }
            }
        }
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
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.isCurrentUserLogged()) {
            this.startMainActivity();
        } else {
            startSignInActivity();
        }
    }

    private void startSignInActivity() {
        //Login
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.fragment_auth)
                .setGoogleButtonId(R.id.google_signin)
                .setFacebookButtonId(R.id.facebook_signin)
                .setEmailButtonId(R.id.email_signin)
                .setTwitterButtonId(R.id.twitter_signin)
                // .setTosAndPrivacyPolicyId(R.id.baz)
                .build();

        //  new AuthUI.IdpConfig.PhoneBuilder().build(),
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                //  new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(customLayout)
                .setLogo(R.drawable.ic_logogo4lunch)
                .setTheme(R.style.LoginTheme)
                .setIsSmartLockEnabled(false, true)
                .setAvailableProviders(providers) //EMAIL
                .build(), RC_SIGN_IN);
    }

    private void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void createUserInFirestore() {
        if (this.getCurrentUser() != null) {
            userViewModel.createUser(this.getCurrentUser());
        }
    }
}