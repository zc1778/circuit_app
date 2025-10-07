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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mihir.drawingcanvas.drawingView;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    private View tools;
    private drawingView draw;
    private ConstraintLayout circuitView;
    private ImageButton undo, redo, stroke;
    private Button clear, save;
    private float buttonX;
    private float buttonY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }

//        draw = findViewById(R.id.circuit_view);
//        undo = findViewById(R.id.undo);
//        redo = findViewById(R.id.redo);
//        clear = findViewById(R.id.clean);
//        save = findViewById(R.id.save);
//
//        undo.setOnClickListener(v -> draw.undo());
//        redo.setOnClickListener(v -> draw.redo());
//        clear.setOnClickListener(v -> draw.clearDrawingBoard());
//
//        save.setOnClickListener(v -> draw.post(() -> {
//            Bitmap bitmap = getBitmapFromView(draw);
//            Uri imageUri = saveBitmapToStorage(bitmap);
//
//            if (imageUri != null) {
//                Log.e("draw", "Image saved at: " + imageUri);
//                Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
//                shareImage(imageUri);
//            } else {
//                Log.e("draw", "Saving failed");
//                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
//            }
//        }));

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

//    private Bitmap getBitmapFromView(View view) {
//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
//                Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        if (view.getBackground() != null) {
//            view.getBackground().draw(canvas);
//        } else {
//            canvas.drawColor(Color.WHITE);
//        }
//        view.draw(canvas);
//        return bitmap;
//    }
//
//    private Uri saveBitmapToStorage(Bitmap bitmap) {
//        String filename = "Drawing " + System.currentTimeMillis() + ".png";
//        Uri uri = null;
//
//        try {
//            File imagesDir = new File(getExternalFilesDir(null) + "/Pictures/DrawingApp");
//
//            if (!imagesDir.exists()) {
//                imagesDir.mkdirs();
//            }
//
//            File imageFile = new File(imagesDir, filename);
//            FileOutputStream outputStream = new FileOutputStream(imageFile);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            outputStream.flush();
//            outputStream.close();
//
//            MediaStore.Images.Media.insertImage(getContentResolver(),
//                    imageFile.getAbsolutePath(), filename, null);
//            uri = Uri.fromFile(imageFile);
//        } catch (Exception e) {
//            Log.e("draw", "Error saving image: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return uri;
//    }
//
//    private void shareImage(Uri uri) {
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        shareIntent.setType("image/png");
//        startActivity(Intent.createChooser(shareIntent, "Share via"));
//    }

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