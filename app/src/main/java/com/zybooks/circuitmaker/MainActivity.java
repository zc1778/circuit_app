package com.zybooks.circuitmaker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private View tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton button = findViewById(R.id.toolBox);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "clicked");
                tools = findViewById(R.id.circuit_tools);
                int toolsVisible = tools.getVisibility();
                if (toolsVisible == View.VISIBLE) {
                    tools.setVisibility(View.GONE);
                } else {
                    tools.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}