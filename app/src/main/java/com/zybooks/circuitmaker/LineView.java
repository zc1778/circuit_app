package com.zybooks.circuitmaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LineView extends View {

    private Paint paint;
    private HashMap<String, ArrayList<String>> gateEdges;
    private HashMap<String, GateModel> gateMap;
    private Path currentPath;

    private String lastGate;

    public LineView(Context context) {
        super(context);
        init();
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        gateMap = new HashMap<String, GateModel>();
        gateEdges = new HashMap<String, ArrayList<String>>();
        currentPath = new Path();
        paint.setColor(Color.argb(1.0F, 0.0F, 0.0F, 0.0F));
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        currentPath.reset();

        for (String gateName : gateEdges.keySet()) {
            Log.d("Gate Name: ", gateName);
            ArrayList<String> edges = gateEdges.get(gateName);
            assert edges != null;
            for (String edge : edges) {
                boolean outputConnectionStatus = Objects.requireNonNull(gateMap.get(gateName)).getOutputConnectionStatus();
                Log.d("Output Status", String.valueOf(outputConnectionStatus));
                if (outputConnectionStatus) {

                    ArrayList<Float[]> inputPoints = Objects.requireNonNull(gateMap.get(edge)).getInputPoints();
                    ArrayList<Boolean> inputConnectionStatus = Objects.requireNonNull(gateMap.get(edge)).getInputConnectionStatus();
                    for (int i = 0; i < inputPoints.size(); i++) {
                        Log.d("Input Count", String.format("%d", inputPoints.size()));
                        Log.d("Input Status Count: ", String.format("%d", inputConnectionStatus.size()));
                        Log.d("Input Point X: ", String.valueOf(inputPoints.get(i)[0]));
                        Log.d("Input Status A: , ", String.valueOf(inputConnectionStatus.get(i)));
                        Log.d("Input Point Y: ", String.valueOf(inputPoints.get(i)[1]));

                        if (inputConnectionStatus.get(i)) {
                            Log.d("Input Status B: ", String.valueOf(inputConnectionStatus.get(i)));
                            currentPath.moveTo(Objects.requireNonNull(gateMap.get(gateName)).getOutputPoint()[0], Objects.requireNonNull(gateMap.get(gateName)).getOutputPoint()[1]);
                            currentPath.lineTo(Objects.requireNonNull(inputPoints.get(i)[0]), Objects.requireNonNull(inputPoints.get(i)[1]));
                        }
                        canvas.drawPath(currentPath, paint);
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                currentPath = new Path();
                float firstX = -1.0F, firstY = -1.0F;
                float closestOut = Float.MAX_VALUE;
                GateModel closestOutGate = null;
                for (GateModel gate : gateMap.values()) {
                    Float[] outputPoints = gate.getOutputPoint();
                    Log.d("Output Point X: ", outputPoints[0].toString());
                    Log.d("Output Point Y: ", outputPoints[1].toString());
                    if (Math.abs(outputPoints[0] - event.getX()) <= 200
                        && Math.abs(outputPoints[1] - event.getY()) <= 200
                        && Math.abs(outputPoints[0] - event.getX()) + Math.abs(outputPoints[1] - event.getY()) < closestOut) {
                        firstX = outputPoints[0];
                        firstY = outputPoints[1];
                        closestOut = Math.abs(outputPoints[0] - event.getX()) + Math.abs(outputPoints[1] - event.getY());
                        closestOutGate = gate;
                    }
                }

                Log.d("Event Start Point X: ", String.valueOf(event.getX()));
                Log.d("Event Start Point Y: ", String.valueOf(event.getY()));

                if (closestOutGate != null) {
                    Log.d("Start Point X: ", String.valueOf(firstX));
                    Log.d("Start Point Y: ", String.valueOf(firstY));
//                    currentPath.moveTo(firstX, firstY);
                    closestOutGate.setOutputConnectionStatus(true);
                    if (!gateEdges.containsKey(closestOutGate.getContentDescription().toString())) {
                        gateEdges.put(closestOutGate.getContentDescription().toString(), new ArrayList<String>());
                    }
                    lastGate = closestOutGate.getContentDescription().toString();
                }
//                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                float nextX = -1.0F, nextY = -1.0F;
                float closestIn = Float.MAX_VALUE;
                GateModel closestInGate = null;
                boolean firstGate = true;
                for (GateModel gate : gateMap.values()) {
                    ArrayList<Float[]> inputPoints = gate.getInputPoints();
                    for (Float[] point : inputPoints) {
                        Log.d("Input Point X: ", point[0].toString());
                        Log.d("Input Point Y: ", point[1].toString());
                        if (Math.abs(point[0] - event.getX()) <= 200
                                && Math.abs(point[1] - event.getY()) <= 200
                                && Math.abs(point[0] - event.getX()) + Math.abs(point[1] - event.getY()) <= closestIn
                                && (point[0] > 0.0F && point[1] > 0.0F)) {
                            Log.d("Input Point Passed X: ", point[0].toString());
                            Log.d("Input Point Passed Y: ", point[1].toString());
                            nextX = point[0];
                            nextY = point[1];
                            firstGate = nextY <= event.getY();
                            closestIn = Math.abs(point[0] - event.getX()) + Math.abs(point[1] - event.getY());
                            closestInGate = gate;
                        }
                    }
                }
                Log.d("End Point X: ", String.valueOf(nextX));
                Log.d("End Point Y: ", String.valueOf(nextY));
                Log.d("Event End Point X: ", String.valueOf(event.getX()));
                Log.d("Event End Point Y: ", String.valueOf(event.getY()));

                if (closestInGate != null) {
//                    currentPath.lineTo(nextX, nextY);
                    ArrayList<String> gates = gateEdges.get(lastGate);
                    assert gates != null;
                    gates.add(closestInGate.getContentDescription().toString());
                    gateEdges.put(lastGate, gates);
                    if (firstGate) {
                        closestInGate.setInputConnectionStatus(0, true);
                    } else {
                        closestInGate.setInputConnectionStatus(1, true);
                    }
                }
                invalidate();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void addGate(GateModel gate) {
        gateMap.put(gate.getContentDescription().toString(), gate);
    }
}