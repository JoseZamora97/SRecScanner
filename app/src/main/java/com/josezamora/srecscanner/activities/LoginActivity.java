package com.josezamora.srecscanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.josezamora.srecscanner.AppGlobals;
import com.josezamora.srecscanner.R;

/**
 * The type Login activity.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * The Google sign in client.
     */
    GoogleSignInClient googleSignInClient;
    /**
     * The Google sign in options.
     */
    GoogleSignInOptions googleSignInOptions;
    /**
     * The Firebase auth.
     */
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get Authentication instance.
        firebaseAuth = FirebaseAuth.getInstance();

        // Build Google sign in options.
        googleSignInOptions =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AppGlobals.ID_CLIENT_OAUTH_TOKEN)
                .requestEmail()
                .build();

        // Load client with those options.
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // Create a button and set the listener up.
        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(view -> signIn());

        // If its successful operation open next activity
        if(firebaseAuth.getCurrentUser() != null)
            toMainActivity();
    }

    /**
     * Opens a new activity for result to get the google client from user device.
     */
    public void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, AppGlobals.REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check the request code.
        if(requestCode == AppGlobals.REQUEST_CODE_SIGN_IN) {
            try {
                // If there isn't exception on getSignedInAccountFromIntent() call, handle
                // the results.
                handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data));
            } catch (ApiException e) {
                // Inform the error.
                Toast.makeText(this, "Code error: "
                        + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Methods which extracts the credentials from GoogleSignInAccount object and
     * register those credentials into Firebase Authentication service.
     *
     * @param completedTask a task with the result of sing in operation.
     * @throws ApiException Exception if somethings went wrong.
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) throws ApiException {

        GoogleSignInAccount account = completedTask.getResult(ApiException.class);

        assert account != null;

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful())
                        toMainActivity();
                    else
                        showSnackBar(task.getException());
                });

    }

    /**
     * Method that show a notification to the user in case the authentication has not went good.
     *
     * @param exception We pass the exception to be shown.
     */
    private void showSnackBar(Exception exception) {
        String messaje = this.getString(R.string.fallo_auth);
        if(exception != null)
            messaje += exception.getMessage();
        Snackbar.make(findViewById(R.id.login_layout),
                messaje  , Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Method with take the user to main activity.
     */
    private void toMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
