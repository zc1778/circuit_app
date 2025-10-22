package com.zybooks.circuitmaker;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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
}
