package com.zybooks.circuitmaker;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class CircuitActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    // private final CircuitController controller = new CircuitController();
    private View tools;

    private View toolBar;
    private LineView draw;
    private ConstraintLayout circuitView;
    private Button isGate;
    private ImageButton notGate;
    private ImageButton andGate;
    private ImageButton nandGate;
    private ImageButton orGate;
    private ImageButton norGate;
    private ImageButton xorGate;
    private ImageButton xnorGate;
    // private ImageButton undo, redo, stroke;
    private Button save; //, clear;
    private ArrayList<Button> components;
    private boolean isCloned = false;
    private float buttonX;
    private float buttonY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
        }

        draw = findViewById(R.id.circuit_view);
        save = findViewById(R.id.save);

        toolBar = findViewById(R.id.toolbar);
        tools = findViewById(R.id.circuit_tools);

        components = new ArrayList<Button>();
        circuitView = findViewById(R.id.main);

        // Set all gate buttons
        isGate = findViewById(R.id.is_gate);

        save.setOnClickListener(v -> draw.post(() -> {
            Bitmap bitmap = getBitmapFromView(draw);
            Uri imageUri = saveBitmapToStorage(bitmap);

            if (imageUri != null) {
                Log.e("draw", "Image saved at: " + imageUri);
                Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
                shareImage(imageUri);
            } else {
                Log.e("draw", "Saving failed");
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }));

        ImageButton button = findViewById(R.id.toolBox);

        button.setOnClickListener(v -> {
            Log.d("BUTTONS", "clicked");
            int toolsVisible = tools.getVisibility();

            Log.d("X position when last dropped", String.valueOf(buttonX));
            Log.d("Y position when last dropped", String.valueOf(buttonY));

            Log.d("Width of tools container", String.valueOf(tools.getWidth()));
            Log.d("Height of tools container", String.valueOf(tools.getHeight()));

            if (toolsVisible == View.VISIBLE) {
                tools.setVisibility(View.GONE);
                for (Button b : components) {
                    b.setX(b.getX() - tools.getWidth() / 2.0F);
                    b.setY(b.getY() - tools.getHeight() / 2.0F);
                }
                isGate.setVisibility(View.GONE);
            } else {
                tools.setVisibility(View.VISIBLE);
                for (Button b : components) {
                    b.setX(b.getX() + tools.getWidth() / 2.0F);
                    b.setY(b.getY() + tools.getHeight() / 2.0F);
                }
                isGate.setVisibility(View.VISIBLE);
            }

            Log.d("Visibility of tools container", String.valueOf(tools.getVisibility()));
            Log.d("X position of placeholderButton", String.valueOf(isGate.getX()));
            Log.d("Y position of placeholderButton", String.valueOf(isGate.getY()));
        });


        // Set drag listener for the View
        onLongClickListenerComponent(circuitView, isGate);

        circuitView.setOnDragListener(this::dragListenerComponent);
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (view.getBackground() != null) {
            view.getBackground().draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return bitmap;
    }

    private Uri saveBitmapToStorage(Bitmap bitmap) {
        String filename = "Drawing " + System.currentTimeMillis() + ".png";
        Uri uri = null;

        try {
            File imagesDir = new File(getExternalFilesDir(null) + "/Pictures/DrawingApp");

            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            File imageFile = new File(imagesDir, filename);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            MediaStore.Images.Media.insertImage(getContentResolver(),
                    imageFile.getAbsolutePath(), filename, null);
            uri = Uri.fromFile(imageFile);
        } catch (Exception e) {
            Log.e("draw", "Error saving image: " + e.getMessage());
            e.printStackTrace();
        }

        return uri;
    }

    private void shareImage(Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @NonNull
    private Button cloneButton(DragEvent e) {

        Log.d("Local state", String.valueOf(((Button) e.getLocalState()).getX()));
        Log.d("Drag position X: ", String.valueOf(e.getX()));
        Log.d("Tools x", String.valueOf(tools.getWidth()));
        Log.d("Content Description: ", ((Button) e.getLocalState()).getContentDescription().toString());
        String desc = ((Button) e.getLocalState()).getContentDescription().toString();

        if (tools.getWidth() > ((Button) e.getLocalState()).getX()
            && tools.getVisibility() == View.VISIBLE
            && desc.equals("YES")) {

            Button cloneButton = getButton(e);

            onLongClickListenerComponent(circuitView, cloneButton);

            isCloned = true;
            return cloneButton;
        }
        isCloned = false;
        return ((Button) e.getLocalState());
    }

    @NonNull
    private Button getButton(DragEvent e) {
        Button cloneButton = new Button(CircuitActivity.this);
        Button originalButton = ((Button) e.getLocalState());

        String imageType = (String) originalButton.getContentDescription();

        switch (imageType) {
            case "YES":
                cloneButton.setContentDescription("YES_CLONE");
                break;
            case "NOT":
                cloneButton.setContentDescription("NOT_CLONE");
                break;
            default:
                break;
        }

        cloneButton.setBackground(originalButton.getBackground());

        ViewGroup.LayoutParams params = originalButton.getLayoutParams();
        cloneButton.setLayoutParams(params);

        cloneButton.setContentDescription("YES_CLONE");
        return cloneButton;
    }

    private void onLongClickListenerComponent(ConstraintLayout circuitView, Button button) {

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
    private Boolean dragListenerComponent(View v, DragEvent e) {
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

                v.invalidate();

                return true;

            case DragEvent.ACTION_DRAG_LOCATION:

                return true;

            case DragEvent.ACTION_DROP:

                ClipData.Item item = e.getClipData().getItemAt(0);

                CharSequence dragData = item.getText();

                buttonX = e.getX();
                buttonY = e.getY();

                Button cloneButton = cloneButton(e);

                cloneButton.setX(buttonX - cloneButton.getWidth() / 2.0F);
                cloneButton.setY(buttonY - cloneButton.getHeight() / 2.0F);

                if (isCloned) {

                    cloneButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            cloneButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            cloneButton.setX(buttonX - cloneButton.getWidth() / 2.0F);
                            cloneButton.setY(buttonY - cloneButton.getHeight() / 2.0F);

                            Log.d("Button height: ", String.valueOf(cloneButton.getHeight()));
                            Log.d("Connection X: ", String.valueOf(cloneButton.getX() - tools.getWidth()));
                            Log.d("Connection Y: ", String.valueOf(cloneButton.getY() - toolBar.getHeight()));
                            draw.addInputPoint(cloneButton.getX() - tools.getWidth() + 5.0F, cloneButton.getY() - toolBar.getHeight() - cloneButton.getHeight() / 2.0F + (cloneButton.getHeight() / 5.0F));
                            draw.addOutputPoint(cloneButton.getX() - tools.getWidth() + cloneButton.getWidth() - 5.0F, cloneButton.getY() - toolBar.getHeight() - cloneButton.getHeight() / 2.0F + (cloneButton.getHeight() / 5.0F));
                        }
                    });

                    if (cloneButton.getX() > tools.getX() && tools.getVisibility() == View.VISIBLE) {
                        circuitView.addView(cloneButton);
                        components.add(cloneButton);
                    }
                }

                Log.d("Drop X position", String.valueOf(buttonX));
                Log.d("Drop Y position", String.valueOf(buttonY));

                Toast.makeText(this, "Dragged data is " + dragData, Toast.LENGTH_LONG).show();

                v.invalidate();

                return true;

            case DragEvent.ACTION_DRAG_ENDED:

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