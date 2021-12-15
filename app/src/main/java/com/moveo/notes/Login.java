package com.moveo.notes;

import androidx.activity.result.ActivityResultLauncher;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends ActivityAncestor {
    private EditText email, password;
    private Button signInButton, signInWithGoogleButton;
    private TextView register;
    private NotesApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (NotesApp) getApplication();
        centerTitle();
        app.gps.search(this);

        // ~~~~~~~checks if user is already logged in~~~~~~~
        String loggedIn = app.info.getLoggedIn();
        if (!loggedIn.equals("")) {
            String password = app.info.sp.getString("password", "__");
            if (!password.equals("__")) {
                app.info.loadUserFromPaper();
                startActivity(new Intent(this, MainScreen.class));
                finish();
            }
            return;
        } else {
            setContentView(R.layout.activity_main);
        }

        email = findViewById(R.id.welcome_screen_EmailAddress_field);
        password = findViewById(R.id.welcome_screen_password_field);
        signInButton = findViewById(R.id.welcome_screen_sign_in_button);
        signInButton.setOnClickListener(view -> onClickHandle(signInButton));
        signInButton.setEnabled(false);
        signInWithGoogleButton = findViewById(R.id.sign_in_with_google);
        signInWithGoogleButton.setOnClickListener(view -> onClickHandle(signInWithGoogleButton));
        signInWithGoogleButton.setEnabled(false);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                signInWithGoogleButton.setEnabled(true);
                signInButton.setEnabled(false);
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!email.getText().toString().isEmpty()) {
                    email.setText(email.getText().toString().trim());
                    signInButton.setEnabled(true);
                    signInWithGoogleButton.setEnabled(false);
                }
                if (password.getText().toString().isEmpty()) {
                    email.setText(email.getText().toString().trim());
                    signInButton.setEnabled(false);
                    signInWithGoogleButton.setEnabled(true);
                }
            }
        });
        register = findViewById(R.id.welcome_screen_register);
        register.setOnClickListener(view -> onClickHandle(register));
    }

    public void onClickHandle(View view) {
        switch (view.getId()) {

            case R.id.welcome_screen_register:
                Intent RegisterIntent = new Intent(Login.this, Register.class);
                startActivity(RegisterIntent);
                finish();
            case R.id.welcome_screen_sign_in_button:
                findUserInFirestore(email.getText().toString(), password.getText().toString());
                break;
            case R.id.sign_in_with_google:
                findUserInFirestore(email.getText().toString(), "");
                break;
        }
    }

    private void findUserInFirestore(String email, String password) {
        signInWithGoogleButton.setEnabled(false);
        signInButton.setEnabled(false);
        app.info.db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                        Intent intent = new Intent(this, MainScreen.class);
                        app.info.setUser(new User(email, password), task.getResult().getDocuments().get(0).getId());
                        app.info.RemmemberLogIn(email);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        }
    }
}