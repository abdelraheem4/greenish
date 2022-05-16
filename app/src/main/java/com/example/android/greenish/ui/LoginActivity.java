package com.example.android.greenish.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.greenish.R;
import com.example.android.greenish.model.User;
import com.example.android.greenish.model.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "log_trace";
    private FirebaseAuth mAuth;
    private boolean invisible = true;
    private EditText passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        doCustomColoring();
        ///////////////

        // Initialize Firebase Auth
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            ((UserClient) getApplicationContext()).setUser(new User(
                    null, "", user.getEmail()
            ));
            startActivity(new Intent(LoginActivity.this, MapActivity.class));
        }

        Button btnLogin = findViewById(R.id.loginButton);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateUser();
            }
        });

        ImageButton visibilityImgBtn = findViewById(R.id.passwordVisibilityImageButton);
        visibilityImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordEditText = findViewById(R.id.passwordEditText);
                if (invisible) {
                    passwordEditText.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance() // Show text password
                    );
                    visibilityImgBtn.setImageResource(R.drawable.ic_visibility_on);

                } else {
                    passwordEditText.setTransformationMethod(
                            PasswordTransformationMethod.getInstance() // Hide text password
                    );
                    visibilityImgBtn.setImageResource(R.drawable.ic_visibility_off);
                }
                invisible = !invisible;
            }
        });

        TextView tvSwitchToRegister = findViewById(R.id.signupLinkTextView);
        tvSwitchToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToRegister();
            }
        });

    }


    private void authenticateUser(){
        EditText etLoginEmail = findViewById(R.id.emailEditText);
        EditText etLoginPassword = findViewById(R.id.passwordEditText);

        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ((UserClient) getApplicationContext()).setUser(new User(
                                    "", "", email
                            ));
                            showMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void showMainActivity()
    {
        Intent intent = new Intent(LoginActivity.this, MapActivity.class);
        startActivity(intent);
        finish();
    }


    private void switchToRegister()
    {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    protected void doCustomColoring () {
        getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color, null));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.nav_bar_color, null));
    }

}