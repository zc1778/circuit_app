package com.zybooks.circuitmaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        Button continueButton = findViewById(R.id.Continue);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification();
        continueButton.setOnClickListener(v -> {
            emailCheck();
        });
    }

    void emailCheck() {
        FirebaseUser user = mAuth.getCurrentUser();
        user.reload();
        Log.d("TAG", "VALUUUEEEEE: " + user.isEmailVerified());
        if(user.isEmailVerified()) {
            Intent myIntent = new Intent(VerificationActivity.this, CircuitActivity.class);
            startActivity(myIntent);
        }
        else {
            Toast toast = Toast.makeText(VerificationActivity.this , "email not verified", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
