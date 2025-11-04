package com.zybooks.circuitmaker;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button registerButton = (Button) findViewById(R.id.registerButton);

        loginButton.setOnClickListener( v -> {
            EditText emailInput = (EditText) findViewById(R.id.loginEmail);
            EditText passwordInput = (EditText) findViewById(R.id.loginPassword);
            loginCheck(emailInput.getText().toString(), passwordInput.getText().toString());
        });

        registerButton.setOnClickListener( v -> {
            EditText emailInput = (EditText) findViewById(R.id.loginEmail);
            EditText passwordInput = (EditText) findViewById(R.id.loginPassword);
            registerUser(emailInput.getText().toString(), passwordInput.getText().toString());
        });
    }
}
