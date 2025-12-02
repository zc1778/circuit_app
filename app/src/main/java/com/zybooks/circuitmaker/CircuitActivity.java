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
import android.widget.ScrollView;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

public class CircuitActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    // private final CircuitController controller = new CircuitController();
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ScrollView tools;
    private View toolBar;
    private LineView draw;
    private ConstraintLayout circuitView;
    private GateModel powerGate;
    private GateModel isGate;
    private GateModel notGate;
    private GateModel andGate;
    private GateModel nandGate;
    private GateModel orGate;
    private GateModel norGate;
    private GateModel xorGate;
    private GateModel xnorGate;
    // private ImageButton undo, redo, stroke;
    private Button save; //, clear;
    private ArrayList<GateModel> components;
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

        user = FirebaseAuth.getInstance().getCurrentUser();

        Log.d("User", String.valueOf(user));

        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
        }

        draw = findViewById(R.id.circuit_view);

        save = findViewById(R.id.save);

        toolBar = findViewById(R.id.toolbar);
        tools = findViewById(R.id.circuit_tools);

        components = new ArrayList<GateModel>();
        circuitView = findViewById(R.id.main);

        // Set all gate buttons
        powerGate = findViewById(R.id.power_button);
        isGate = findViewById(R.id.is_gate);
        notGate = findViewById(R.id.not_gate);
        andGate = findViewById(R.id.and_gate);
        nandGate = findViewById(R.id.nand_gate);
        orGate = findViewById(R.id.or_gate);
        norGate = findViewById(R.id.nor_gate);
        xorGate = findViewById(R.id.xor_gate);
        xnorGate = findViewById(R.id.xnor_gate);

        save.setOnClickListener(v -> /*Bitmap bitmap = getBitmapFromView(draw);
            Uri imageUri = saveBitmapToStorage(bitmap);

            if (imageUri != null) {
                Log.e("draw", "Image saved at: " + imageUri);
                Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
                shareImage(imageUri);
            } else {
                Log.e("draw", "Saving failed");
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }*/ draw.post(this::saveGateDataToDatabase));

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
                for (GateModel g : components) {
                    g.setX(g.getX() - tools.getWidth());
//                    ArrayList<Float[]> inputPoints = g.getInputPoints();
//                    for (int i = 0; i < inputPoints.size(); i++) {
//                        inputPoints.get(i)[0] -= tools.getWidth();
//                        g.setInputPoints(i, inputPoints.get(i));
//                    }
//                    Float[] outputPoint = g.getOutputPoint();
//                    outputPoint[0] -= tools.getWidth();
//                    g.setOutputPoint(outputPoint);
                }
                powerGate.setVisibility(View.GONE);
                isGate.setVisibility(View.GONE);
                notGate.setVisibility(View.GONE);
                andGate.setVisibility(View.GONE);
                nandGate.setVisibility(View.GONE);
                orGate.setVisibility(View.GONE);
                norGate.setVisibility(View.GONE);
                xorGate.setVisibility(View.GONE);
                xnorGate.setVisibility(View.GONE);
                draw.invalidate();
            } else {
                tools.setVisibility(View.VISIBLE);
                for (GateModel g : components) {
                    g.setX(g.getX() + tools.getWidth());
//                    ArrayList<Float[]> inputPoints = g.getInputPoints();
//                    for (int i = 0; i < inputPoints.size(); i++) {
//                        inputPoints.get(i)[0]+= tools.getWidth();
//                        g.setInputPoints(i, inputPoints.get(i));
//                    }
//                    Float[] outputPoint = g.getOutputPoint();
//                    outputPoint[0]+= tools.getWidth();
//                    g.setOutputPoint(outputPoint);
                }
                powerGate.setVisibility(View.VISIBLE);
                isGate.setVisibility(View.VISIBLE);
                notGate.setVisibility(View.VISIBLE);
                andGate.setVisibility(View.VISIBLE);
                nandGate.setVisibility(View.VISIBLE);
                orGate.setVisibility(View.VISIBLE);
                norGate.setVisibility(View.VISIBLE);
                xorGate.setVisibility(View.VISIBLE);
                xnorGate.setVisibility(View.VISIBLE);
                draw.invalidate();
            }

            Log.d("Visibility of tools container", String.valueOf(tools.getVisibility()));
        });

        // Set drag listener for the View
        onLongClickListenerComponent(circuitView, powerGate);
        onLongClickListenerComponent(circuitView, isGate);
        onLongClickListenerComponent(circuitView, notGate);
        onLongClickListenerComponent(circuitView, andGate);
        onLongClickListenerComponent(circuitView, nandGate);
        onLongClickListenerComponent(circuitView, orGate);
        onLongClickListenerComponent(circuitView, norGate);
        onLongClickListenerComponent(circuitView, xorGate);
        onLongClickListenerComponent(circuitView, xnorGate);

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
    private GateModel cloneGate(DragEvent e) {

        Log.d("Local state", String.valueOf(((Button) e.getLocalState()).getX()));
        Log.d("Drag position X: ", String.valueOf(e.getX()));
        Log.d("Tools x", String.valueOf(tools.getWidth()));
        Log.d("Content Description: ", ((Button) e.getLocalState()).getContentDescription().toString());
        String desc = ((Button) e.getLocalState()).getContentDescription().toString();

        if (tools.getWidth() > ((Button) e.getLocalState()).getX()
            && tools.getVisibility() == View.VISIBLE
            && !desc.contains("CLONE")) {

            GateModel cloneGate = getGate(e);

            onLongClickListenerComponent(circuitView, cloneGate);

            if (desc.contains("PWR")) {
                cloneGate.setOnClickListener(v -> {
                    Log.d("POWER GATE", "clicked");
                    cloneGate.setOutputStatus(!cloneGate.getOutputStatus());
                    draw.invalidate();
                });
            }

            isCloned = true;
            return cloneGate;
        }
        isCloned = false;
        return ((GateModel) e.getLocalState());
    }

    @NonNull
    private GateModel getGate(DragEvent e) {
        GateModel cloneGate = new GateModel(CircuitActivity.this);
        GateModel originalGate = ((GateModel) e.getLocalState());
        cloneGate.setCloneNumber(originalGate.getCloneNumber());
        originalGate.incrementCloneNumber();
        Log.d("Clone Number: ", String.valueOf(cloneGate.getCloneNumber()));

        String imageType = (String) originalGate.getContentDescription();

        switch (imageType) {
            case "PWR":
                LineView.incrementPowerCount();
                cloneGate.setContentDescription(String.format(Locale.getDefault(), "PWR_CLONE_%d", cloneGate.getCloneNumber()));
                break;
            case "YES":
                cloneGate.setContentDescription(String.format(Locale.getDefault(), "YES_CLONE_%d", cloneGate.getCloneNumber()));
                break;
            case "NOT":
                cloneGate.setContentDescription(String.format(Locale.getDefault(), "NOT_CLONE_%d", cloneGate.getCloneNumber()));
                break;
            case "AND":
                cloneGate.setContentDescription(String.format(Locale.getDefault(), "AND_CLONE_%d", cloneGate.getCloneNumber()));
                break;
            case "NAND":
                cloneGate.setContentDescription(String.format(Locale.getDefault(), "NAND_CLONE_%d", cloneGate.getCloneNumber()));
                break;
            case "OR":
                cloneGate.setContentDescription(String.format(Locale.getDefault(), "OR_CLONE_%d", cloneGate.getCloneNumber()));
                break;
            case "NOR":
                cloneGate.setContentDescription(String.format(Locale.getDefault(), "NOR_CLONE_%d", cloneGate.getCloneNumber()));
                break;
            case "XOR":
                cloneGate.setContentDescription(String.format(Locale.getDefault(), "XOR_CLONE_%d", cloneGate.getCloneNumber()));
                break;
            case "XNOR":
                cloneGate.setContentDescription(String.format(Locale.getDefault(), "XNOR_CLONE_%d", cloneGate.getCloneNumber()));
                break;
            default:
                Log.e("draw", "Unknown gate type");
                break;
        }

        cloneGate.setBackground(originalGate.getBackground());

        ViewGroup.LayoutParams params = originalGate.getLayoutParams();
        cloneGate.setLayoutParams(params);
        cloneGate.setText(originalGate.getText());

        return cloneGate;
    }

    private void onLongClickListenerComponent(ConstraintLayout circuitView, GateModel gate) {

        gate.setOnLongClickListener( view -> {

            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());

            ClipData dragData = new ClipData(
                (CharSequence) view.getTag(),
                new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN},
                item);

            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(gate);

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

                GateModel cloneGate = cloneGate(e);

                cloneGate.setX(buttonX - cloneGate.getWidth() / 2.0F);
                cloneGate.setY(buttonY - cloneGate.getHeight() / 2.0F);

                if (isCloned) {

                    cloneGate.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            cloneGate.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            cloneGate.setX(buttonX - cloneGate.getWidth() / 2.0F);
                            cloneGate.setY(buttonY - cloneGate.getHeight() / 2.0F);

                            Log.d("Button height: ", String.valueOf(cloneGate.getHeight()));
                            Log.d("Connection X: ", String.valueOf(cloneGate.getX() - tools.getWidth() + 5.0F));
                            Log.d("Connection Y: ", String.valueOf(cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F + (cloneGate.getHeight() / 5.0F)));
                            if (cloneGate.getContentDescription().toString().contains("YES") || cloneGate.getContentDescription().toString().contains("NOT")) {
                                cloneGate.setInputPoints(0, new Float[]{cloneGate.getX() - tools.getWidth() + 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F + (cloneGate.getHeight() / 5.0F)});
                            } else {
                                cloneGate.setInputPoints(0, new Float[]{cloneGate.getX() - tools.getWidth() + 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F + (2 * cloneGate.getHeight() / 5.0F)});
                                cloneGate.setInputPoints(1, new Float[]{cloneGate.getX() - tools.getWidth() + 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F});
                            }
                            cloneGate.setOutputPoint(new Float[]{cloneGate.getX() - tools.getWidth() + cloneGate.getWidth() - 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F + (cloneGate.getHeight() / 5.0F)});
                        }
                    });

                    if (cloneGate.getX() > tools.getWidth() && tools.getVisibility() == View.VISIBLE) {
                        circuitView.addView(cloneGate);
                        components.add(cloneGate);
                        draw.addGate(cloneGate);
                    }
                } else {
                    if (tools.getVisibility() == View.VISIBLE) {

                        if (cloneGate.getContentDescription().toString().contains("YES_CLONE") || cloneGate.getContentDescription().toString().contains("NOT_CLONE")) {
                            cloneGate.setInputPoints(0, new Float[]{cloneGate.getX() - tools.getWidth() + 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F + (cloneGate.getHeight() / 5.0F)});
                        } else {
                            cloneGate.setInputPoints(0, new Float[]{cloneGate.getX() - tools.getWidth() + 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F + (2 * cloneGate.getHeight() / 5.0F)});
                            cloneGate.setInputPoints(1, new Float[]{cloneGate.getX() - tools.getWidth() + 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F});
                        }
                        cloneGate.setOutputPoint(new Float[]{cloneGate.getX() - tools.getWidth() + cloneGate.getWidth() - 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F + (cloneGate.getHeight() / 5.0F)});
                    } else {

                        if (cloneGate.getContentDescription().toString().contains("YES_CLONE") || cloneGate.getContentDescription().toString().contains("NOT_CLONE")) {
                            cloneGate.setInputPoints(0, new Float[]{cloneGate.getX() + 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F + (cloneGate.getHeight() / 5.0F)});
                        } else {
                            cloneGate.setInputPoints(0, new Float[]{cloneGate.getX() + 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F + (2 * cloneGate.getHeight() / 5.0F)});
                            cloneGate.setInputPoints(1, new Float[]{cloneGate.getX() + 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F});
                        }
                        cloneGate.setOutputPoint(new Float[]{cloneGate.getX() + cloneGate.getWidth() - 5.0F, cloneGate.getY() - toolBar.getHeight() - cloneGate.getHeight() / 2.0F + (cloneGate.getHeight() / 5.0F)});
                    }
                    draw.invalidate();
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

    void saveGateDataToDatabase() {
        Map<String, Object> gateMap = new HashMap<>();
        for(int i = 0; i < components.size(); i++) {
            gateMap.put("x location", components.get(i).getX());
            gateMap.put("y location", components.get(i).getY());
            db.collection("gates").add(gateMap);
            gateMap.clear();
        }
    }
}