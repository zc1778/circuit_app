package com.zybooks.circuitmaker;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
                tools = findViewById(R.id.tools_view);
                int toolsVisible = tools.getVisibility();
                if (toolsVisible == View.VISIBLE) {
                    tools.setVisibility(View.GONE);
                } else {
                    tools.setVisibility(View.VISIBLE);
                }
            }
        });

        Button placeholderButton = findViewById(R.id.placeholder_button);
        placeholderButton.setOnLongClickListener( view -> {

            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());

            ClipData dragData = new ClipData(
                    (CharSequence) view.getTag(),
                    new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN},
                    item);

            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(placeholderButton);

            view.startDragAndDrop(dragData,
                    myShadow,
                    null,
                    0);

            return true;
        });
    }
}