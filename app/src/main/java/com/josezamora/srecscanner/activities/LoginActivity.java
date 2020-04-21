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

public class LoginActivity extends AppCompatActivity {

    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        googleSignInOptions =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AppGlobals.ID_CLIENT_OAUTH_TOKEN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(view -> signIn());

        if(firebaseAuth.getCurrentUser() != null)
            toMainActivity();
    }

    public void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, AppGlobals.REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == AppGlobals.REQUEST_CODE_SIGN_IN) {
            try {
                handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data));
            } catch (ApiException e) {
                Toast.makeText(this, "Code error: "
                        + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) throws ApiException {

        GoogleSignInAccount account = completedTask.getResult(ApiException.class);

        assert account != null;

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful())
                        toMainActivity();
                    else
                        showSnakBar(task.getException());
                });

    }

    private void showSnakBar(Exception exception) {
        String messaje = this.getString(R.string.fallo_auth);
        if(exception != null)
            messaje += exception.getMessage();
        Snackbar.make(findViewById(R.id.login_layout),
                messaje  , Snackbar.LENGTH_SHORT).show();
    }

    private void toMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
