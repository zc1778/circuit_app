package com.zybooks.circuitmaker;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    private View tools;
    private ConstraintLayout circuitView;
    private float buttonX;
    private float buttonY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tools = findViewById(R.id.circuit_tools);
        ImageButton button = findViewById(R.id.toolBox);
        Button placeholderButton = findViewById(R.id.placeholder_button);

        button.setOnClickListener(v -> {
            Log.d("BUTTONS", "clicked");
            int toolsVisible = tools.getVisibility();

            Log.d("X position when last dropped", String.valueOf(buttonX));
            Log.d("Y position when last dropped", String.valueOf(buttonY));

            Log.d("Width of tools container", String.valueOf(tools.getWidth()));
            Log.d("Height of tools container", String.valueOf(tools.getHeight()));

            if (toolsVisible == View.VISIBLE) {
                tools = findViewById(R.id.circuit_tools);
                tools.setVisibility(View.GONE);
                if (placeholderButton.getX() < tools.getWidth()) {
                    placeholderButton.setVisibility(View.GONE);
                } else {
                    placeholderButton.setX((float) (buttonX + (tools.getWidth() / 2.0) - 1));
                    placeholderButton.setY((float) (buttonY - (tools.getHeight() / 2.0) - 1));
                }
            } else {
                tools.setVisibility(View.VISIBLE);
                if (placeholderButton.getX() >= tools.getWidth()) {
                    placeholderButton.setX((float) (buttonX - (tools.getWidth() / 2.0) + 1));
                    placeholderButton.setY((float) (buttonY + (tools.getHeight() / 2.0) + 1));
                }
                placeholderButton.setVisibility(View.VISIBLE);
            }

            Log.d("Visibility of tools container", String.valueOf(tools.getVisibility()));
            Log.d("X position of placeholderButton", String.valueOf(placeholderButton.getX()));
            Log.d("Y position of placeholderButton", String.valueOf(placeholderButton.getY()));
        });

        circuitView = findViewById(R.id.main);

        onLongClickListener(circuitView, placeholderButton);

        // Set drag listener for the View
        circuitView.setOnDragListener(this::dragListener);
    }

    @NonNull
    private Button cloneButton(DragEvent e) {

        Log.d("Local state", String.valueOf(((Button) e.getLocalState()).getX()));
        Log.d("Tools x", String.valueOf(tools.getX()));

        if (tools.getWidth() > ((Button) e.getLocalState()).getX()) {

            Button cloneButton = new Button(MainActivity.this);
            Button originalButton = ((Button) e.getLocalState());

            cloneButton.setText(originalButton.getText());
            cloneButton.setTextColor(originalButton.getCurrentTextColor());
            cloneButton.setBackground(originalButton.getBackground());

            onLongClickListener(circuitView, cloneButton);

            return cloneButton;
        }
        return ((Button) e.getLocalState());
    }

    private void onLongClickListener(View v, Button button) {

        button.setOnLongClickListener( view -> {

            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());

            ClipData dragData = new ClipData(
                    (CharSequence) view.getTag(),
                    new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN},
                    item);

            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(button);

            view.startDragAndDrop(dragData,
                    myShadow,
                    view,
                    0);

            return true;
        });
    }

    @NonNull
    private Boolean dragListener(View v, DragEvent e) {
        // Handle each of the expected events
        switch(e.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:

                // Determine whether this View can accept
                if (e.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

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

                Button cloneButton = cloneButton(e);
                if (cloneButton.getX() <= tools.getX()) {
                    circuitView.addView(cloneButton);
                }

                buttonX = e.getX();
                buttonY = e.getY();
                Log.d("Drop X position", String.valueOf(buttonX));
                Log.d("Drop Y position", String.valueOf(buttonY));

                cloneButton.setX(buttonX);
                cloneButton.setY(buttonY);

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
    }
}