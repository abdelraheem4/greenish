package com.example.android.greenish.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private boolean invisible = true;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        doCustomColoring();
        ///////////////

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            return;
        }

        Button btnRegister = findViewById(R.id.registerButton);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


        TextView loginText;
        loginText = (TextView) findViewById(R.id.textView5);
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        ///////////

        ImageButton visibilityImgBtn = findViewById(R.id.signupPassVisibilityImageButton);
        visibilityImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordEditText = findViewById(R.id.signupPassEditText);
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

    }


    private void registerUser()
    {
        EditText etFirstName = findViewById(R.id.signupUserEditText);
        EditText etRegisterEmail = findViewById(R.id.signupEmailEditText);
        EditText etRegisterPassword = findViewById(R.id.signupPassEditText);

        String firstName = etFirstName.getText().toString();
        String email = etRegisterEmail.getText().toString();
        String password = etRegisterPassword.getText().toString();

        if (firstName.isEmpty()  || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(firstName, null, email);
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            ((UserClient) getApplicationContext()).setUser(new User(
                                                    firstName, "", email
                                            ));
                                            showMainActivity();
                                            Toast.makeText(RegisterActivity.this, "Succeed.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }


    private void showMainActivity()
    {
        Intent intent = new Intent(RegisterActivity.this, MapActivity.class);
        startActivity(intent);
        finish();
    }

    protected void doCustomColoring () {
        getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color, null));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.nav_bar_color, null));
    }

}