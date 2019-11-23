package com.josezamora.tcscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.josezamora.tcscanner.Firebase.Controllers.FirebaseDatabaseController;
import com.josezamora.tcscanner.Interfaces.AppGlobals;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    FirebaseDatabaseController databaseController;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseController = new FirebaseDatabaseController();
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        if (user != null)
            toMainActivity();
        else
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(AppGlobals.PROVIDERS)
                            .setIsSmartLockEnabled(false, true)
                            .setTheme(R.style.AppTheme)
                            .build(),
                    AppGlobals.REQUEST_CODE_SIGN_IN);
    }

    private void toMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppGlobals.REQUEST_CODE_SIGN_IN) {

            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser userFromOAuth = FirebaseAuth.getInstance().getCurrentUser();
                assert userFromOAuth != null;
                toMainActivity();
            }
            else {
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }
                if (Objects.requireNonNull(response.getError()).getErrorCode()
                        == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }
                showSnackbar(R.string.unknown_error);
            }
        }
    }

    private void showSnackbar(int stringRes) {
        //TODO: snack con error.
    }
}
