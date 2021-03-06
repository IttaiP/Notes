package com.moveo.notes;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class Register extends ActivityAncestor {
    private EditText editTextEmail, editTextPassword;
    private Button registerButton;
    private Button signUpWithGoogleButton;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private final static boolean WITH_GOOGLE = true;
    private final static boolean WITHOUT_GOOGLE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.getInstance().signOut();
        centerTitle();

        signUpWithGoogleButton = findViewById(R.id.google_signIn);
        createRequest();
        signUpWithGoogleButton.setOnClickListener(view -> signIn());
        registerButton = findViewById(R.id.create_user_register_button);
        registerButton.setOnClickListener(view -> registerUser());
        editTextEmail = findViewById(R.id.create_user_email_address_field);
        editTextPassword = findViewById(R.id.create_user_password_field);
    }


    // ==================================== REGISTER WITH GOOGLE ===================================
    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("22011293528-l4mpfjoig2elfss0irohmee5h175vl3n.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        writeNewUserToFirestoreDatabase(WITH_GOOGLE);
                    } else {
                        // If sign in fails, display a message to the user.
                        Exception exception = task.getException();
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    // =============================== REGISTER WITH EMAIL & PASSWORD ==============================
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        registerButton.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        writeNewUserToFirestoreDatabase(WITHOUT_GOOGLE);
                    } else {
                        Exception exception = task.getException();
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ==================================== SHARED ===================================
    private void writeNewUserToFirestoreDatabase(boolean withGoogle) {
        User user = null;
        if (withGoogle) {
            FirebaseUser googleUser = FirebaseAuth.getInstance().getCurrentUser();
            if (googleUser != null) {
                String userEmail = googleUser.getEmail();
                user = new User(userEmail, "");

            }
        } else if (!withGoogle) {
            user = new User(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim());
        }
//        user.SetID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        app.info.setUser(user, FirebaseAuth.getInstance().getCurrentUser().getUid());  // wont be null because in case of new user we called "createUserWithEmailAndPassword"


        app.info.db.collection("users").
                document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(user)
                .addOnCompleteListener(this, subTask -> {

                    if (subTask.isSuccessful()) {
                        Toast.makeText(this,
                                "User has been registered successfully!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        firebaseUser.delete()
                                .addOnCompleteListener(this, subTask1 -> {

                                    if (subTask1.isSuccessful()) {
                                        Log.d("DELETE CURRENT USER",
                                                "User account deleted.");
                                    }
                                });

                        Toast.makeText(this,
                                "Failed to register! Try again...",
                                Toast.LENGTH_LONG).show();
                    }
                });
        Intent intent = new Intent(this, MainScreen.class); //
        app.info.RemmemberLogIn(user.email);

        startActivity(intent);
        finish();
    }
    // =============================================================================================

}