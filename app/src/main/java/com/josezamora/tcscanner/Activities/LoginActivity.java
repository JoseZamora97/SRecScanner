package com.josezamora.tcscanner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.josezamora.tcscanner.AppGlobals;
import com.josezamora.tcscanner.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AppGlobals.ID_CLIENT_OAUTH_TOKEN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        if(firebaseAuth.getCurrentUser() != null)
            toMainActivity();
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
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

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                            toMainActivity();
                        else
                            showSnakBar(task.getException());
                    }
                });

    }

    private void showSnakBar(Exception exception) {
        String messaje = "Fallo en la Autenticación: ";
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
