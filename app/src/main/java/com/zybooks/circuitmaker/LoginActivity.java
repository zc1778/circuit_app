package com.zybooks.circuitmaker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button registerButton = (Button) findViewById(R.id.registerButton);

        loginButton.setOnClickListener( v -> {
            // login logic
        });

        registerButton.setOnClickListener( v -> {
            // register logic
        });
    }

    void loginCheck() {
        EditText emailInput = (EditText) findViewById(R.id.loginEmail);
        EditText passwordInput = (EditText) findViewById(R.id.loginPassword);
    }

    void registerUser() {
        EditText emailInput = (EditText) findViewById(R.id.loginEmail);
        EditText passwordInput = (EditText) findViewById(R.id.loginPassword);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
    }
}
