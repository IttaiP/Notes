package com.moveo.notes;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {
    NotesApp app = (NotesApp) getApplication();
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private EditText email, password;
    private Button signInButton, signInWithGoogleButton;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //todo: add check whether user is already logged in

        email = findViewById(R.id.welcome_screen_EmailAddress_field);
        password = findViewById(R.id.welcome_screen_password_field);
        auth = FirebaseAuth.getInstance();
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
                if (password.getText().toString().isEmpty()){
                    email.setText(email.getText().toString().trim());
                    signInButton.setEnabled(false);
                    signInWithGoogleButton.setEnabled(true);
                }
            }
        });



        register = findViewById(R.id.welcome_screen_register);
        register.setOnClickListener(view -> onClickHandle(register));





//
//
//        // Choose authentication providers
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build());
//
//// Create and launch sign-in intent
//        Intent signInIntent = AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .build();
//        signInLauncher.launch(signInIntent);
//
//
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                    }
//                });

    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }


    public void onClickHandle(View view) {
        switch (view.getId()){

            // new user
            case R.id.welcome_screen_register:
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                break;

            // exist user
            case R.id.welcome_screen_sign_in_button:
//                firebaseFirestore.collection("users")
//                        .whereEqualTo("email", email.getText().toString())
//                        .whereEqualTo("password", password.getText().toString()).get()
//                        .addOnCompleteListener(task -> {
//
//                            // found user with provided email
//                            if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()){
//                                app.info.myID = task.getResult().getDocuments().get(0).getId();
//
//                                Intent intent = new Intent(this.getContext(), HomeScreen.class);
//                                List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
//
//                                if (documentSnapshotList.isEmpty()){
//                                    Toast.makeText(this.getContext(), "User was not found.", Toast.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()) {
//                                        String name = documentSnapshot.getString("name");
//                                        intent.putExtra("Full Name" , name);
//                                    }
//                                    intent.putExtra("Email" , email.getText().toString());
//                                    app.info.setUserEmail(email.getText().toString());
//                                    startActivity(intent);
//                                }
//                            }
//
//                            //user was not found
//                            else {
//                                Toast.makeText(this.getContext(), "User was not found.", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                break;

            case R.id.sign_in_with_google:
//                firebaseFirestore.collection("users")
//                        .whereEqualTo("email", email.getText().toString()).get()
//                        .addOnCompleteListener(task -> {
//
//                            // found user with provided email
//                            if (task.isSuccessful()){
//
//                                Intent intent = new Intent(this.getContext(), HomeScreen.class);
//                                List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
//
//                                if (documentSnapshotList.isEmpty()){
//                                    Toast.makeText(this.getContext(), "User was not found.", Toast.LENGTH_SHORT).show();
//                                }
//                                else {
//
//                                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()) {
//                                        String name = documentSnapshot.getString("name");
//                                        intent.putExtra("Full Name" , name);
//                                    }
//                                    app.info.myID = task.getResult().getDocuments().get(0).getId();
//
//                                    intent.putExtra("Email" , email.getText().toString());
//                                    app.info.setUserEmail(email.getText().toString());
//                                    startActivity(intent);
//                                }
//                            }
//
//                            //user was not found
//                            else {
//                                Toast.makeText(this.getContext(), "User was not found.", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                break;

        }
    }
}