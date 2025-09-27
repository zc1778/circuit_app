package com.zybooks.circuitmaker;

import android.content.ClipData;
import android.content.ClipDescription;
//import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
//import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private View tools;

    private float buttonX;
    private float buttonY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton button = findViewById(R.id.toolBox);
        Button placeholderButton = findViewById(R.id.placeholder_button);

        button.setOnClickListener(v -> {
            Log.d("BUTTONS", "clicked");
            tools = findViewById(R.id.circuit_tools);
            int toolsVisible = tools.getVisibility();

            Log.d("X position when last dropped", String.valueOf(buttonX));
            Log.d("Y position when last dropped", String.valueOf(buttonY));

            Log.d("Width of tools container", String.valueOf(tools.getWidth()));
            Log.d("Height of tools container", String.valueOf(tools.getHeight()));

            if (toolsVisible == View.VISIBLE) {
                tools.setVisibility(View.GONE);
                if (placeholderButton.getX() < tools.getWidth()) {
                    placeholderButton.setVisibility(View.GONE);
                } else {
                    placeholderButton.setX((float) (buttonX + (tools.getWidth() / 2.0) - 1));
                    placeholderButton.setY((float) (buttonY - (tools.getHeight() / 2.0) - 1));
                }
            } else {
                tools.setVisibility(View.VISIBLE);
                placeholderButton.setX((float) (buttonX - (tools.getWidth() / 2.0) + 1));
                placeholderButton.setY((float) (buttonY + (tools.getHeight() / 2.0) + 1));
                placeholderButton.setVisibility(View.VISIBLE);
            }

            Log.d("Visibility of tools container", String.valueOf(tools.getVisibility()));
            Log.d("X position of placeholderButton", String.valueOf(placeholderButton.getX()));
            Log.d("Y position of placeholderButton", String.valueOf(placeholderButton.getY()));
        });

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

        View view = findViewById(R.id.circuit_view);

        // Set drag listener for the View
        view.setOnDragListener( (v, e) -> {

            // Handle each of the expected events
            switch(e.getAction()) {

                case DragEvent.ACTION_DRAG_STARTED:

                    // Determine whether this View can accept
                    if (e.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        // ((ImageView) v).setColorFilter(Color.BLUE);

                        v.invalidate();

                        return true;
                    }

                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:

                    // ((ImageView) v).setColorFilter(Color.GREEN);

                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:

                    return true;

                case DragEvent.ACTION_DROP:

                    ClipData.Item item = e.getClipData().getItemAt(0);

                    CharSequence dragData = item.getText();

                    buttonX = e.getX();
                    buttonY = e.getY();
                    Log.d("Drop X position", String.valueOf(buttonX));
                    Log.d("Drop Y position", String.valueOf(buttonY));
                    placeholderButton.setX(buttonX);
                    placeholderButton.setY(buttonY);

                    Toast.makeText(this, "Dragged data is " + dragData, Toast.LENGTH_LONG).show();

                    // ((ImageView) v).clearColorFilter();

                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    // ((ImageView) v).clearColorFilter();

                    v.invalidate();

                    if (e.getResult()) {
                        Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG).show();
                    }

                    return true;

                default:
                    Log.e("DragDrop test", "Unknown action type received by View.OnDragListener");
                    break;
            }

            return false;
        });
    }
}