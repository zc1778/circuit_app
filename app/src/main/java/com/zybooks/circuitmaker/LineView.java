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
import java.util.Locale;
import java.util.Objects;

public class LineView extends View {

    private Paint standbyPaint;
    private Paint onPaint;
    private HashMap<String, ArrayList<String>> gateEdges;
    private HashMap<String, GateModel> gateMap;
    private Path currentPath;
    private static int powerCount;

    private GateModel lastGate;

    public LineView(Context context) {
        super(context);
        init();
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        standbyPaint = new Paint();
        onPaint = new Paint();
        gateMap = new HashMap<String, GateModel>();
        gateEdges = new HashMap<String, ArrayList<String>>();
        currentPath = new Path();
        standbyPaint.setColor(Color.argb(1.0F, 0.0F, 0.0F, 0.0F));
        onPaint.setColor(Color.argb(1.0F, 1.0F, 0.2F, 0.1F));
        standbyPaint.setStrokeWidth(8);
        onPaint.setStrokeWidth(10);
        standbyPaint.setStyle(Paint.Style.STROKE);
        onPaint.setStyle(Paint.Style.STROKE);
        powerCount = -1;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        currentPath.reset();

        for (int i = 0; i <= powerCount; i++) {
            GateModel gate = gateMap.get(String.format(Locale.getDefault(), "PWR_CLONE_%d", i));
            assert gate != null;
            for (String gateName : gate.getEdgeGates().keySet()) {
                GateModel edgeGate = gateMap.get(gateName);
                assert edgeGate != null;
                int idex = (gate.getEdgeGates()).get(gateName);
                edgeGate.setInputStatus(idex, gate.getOutputStatus());
                edgeGate.analyzeOutput();
            }
        }

        for (String gateName : gateEdges.keySet()) {
            Log.d("Gate Name: ", gateName);
            ArrayList<String> edges = gateEdges.get(gateName);
            assert edges != null;
            for (String edge : edges) {
                boolean outputConnectionStatus = Objects.requireNonNull(gateMap.get(gateName)).getOutputConnectionStatus();
                boolean outputStatus = Objects.requireNonNull(gateMap.get(gateName)).getOutputStatus();
                Log.d("Output Status", String.valueOf(outputConnectionStatus));
                if (outputConnectionStatus) {

                    ArrayList<ArrayList<Float>> inputPoints = Objects.requireNonNull(gateMap.get(edge)).getInputPoints();
                    ArrayList<Boolean> inputConnectionStatus = Objects.requireNonNull(gateMap.get(edge)).getInputConnectionStatus();
                    for (int i = 0; i < inputPoints.size(); i++) {
                        Log.d("Input Count", String.format("%d", inputPoints.size()));
                        Log.d("Input Status Count: ", String.format("%d", inputConnectionStatus.size()));
                        Log.d("Input Point X: ", String.valueOf(inputPoints.get(i).get(0)));
                        Log.d("Input Point Y: ", String.valueOf(inputPoints.get(i).get(1)));
                        Log.d("Input Status A: , ", String.valueOf(inputConnectionStatus.get(i)));

                        if (inputConnectionStatus.get(i)) {
                            Log.d("Input Status B: ", String.valueOf(inputConnectionStatus.get(i)));
                            currentPath.moveTo(Objects.requireNonNull(gateMap.get(gateName)).getOutputPoint().get(0), Objects.requireNonNull(gateMap.get(gateName)).getOutputPoint().get(1));
                            currentPath.lineTo(Objects.requireNonNull(inputPoints.get(i).get(0)), Objects.requireNonNull(inputPoints.get(i).get(1)));
                        }
                        if (!outputStatus) {
                            canvas.drawPath(currentPath, standbyPaint);
                        }
                        else {
                            canvas.drawPath(currentPath, onPaint);
                        }
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
                    ArrayList<Float> outputPoints = gate.getOutputPoint();
                    Log.d("Output Point X: ", outputPoints.get(0).toString());
                    Log.d("Output Point Y: ", outputPoints.get(1).toString());
                    if (Math.abs(outputPoints.get(0) - event.getX()) <= 200
                        && Math.abs(outputPoints.get(1) - event.getY()) <= 200
                        && Math.abs(outputPoints.get(0) - event.getX()) + Math.abs(outputPoints.get(1) - event.getY()) < closestOut) {
                        firstX = outputPoints.get(0);
                        firstY = outputPoints.get(1);
                        closestOut = Math.abs(outputPoints.get(0) - event.getX()) + Math.abs(outputPoints.get(1) - event.getY());
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
                    lastGate = closestOutGate;
                }
//                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                float nextX = -1.0F, nextY = -1.0F;
                float closestIn = Float.MAX_VALUE;
                GateModel closestInGate = null;
                boolean firstGate = true;
                for (GateModel gate : gateMap.values()) {
                    ArrayList<ArrayList<Float>> inputPoints = gate.getInputPoints();
                    for (ArrayList<Float> point : inputPoints) {
                        Log.d("Input Point X: ", point.get(0).toString());
                        Log.d("Input Point Y: ", point.get(1).toString());
                        if (Math.abs(point.get(0) - event.getX()) <= 200
                                && Math.abs(point.get(1) - event.getY()) <= 200
                                && Math.abs(point.get(0) - event.getX()) + Math.abs(point.get(1) - event.getY()) <= closestIn
                                && (point.get(0) > 0.0F && point.get(1) > 0.0F)) {
                            Log.d("Input Point Passed X: ", point.get(0).toString());
                            Log.d("Input Point Passed Y: ", point.get(1).toString());
                            nextX = point.get(0);
                            nextY = point.get(1);
                            firstGate = (nextY <= event.getY())
                                    || gate.getContentDescription().toString().contains("YES")
                                    || gate.getContentDescription().toString().contains("NOT")
                                    || gate.getContentDescription().toString().contains("PWR");
                            closestIn = Math.abs(point.get(0) - event.getX()) + Math.abs(point.get(1) - event.getY());
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
                    int idex = -1;
                    ArrayList<String> gates = gateEdges.get(lastGate.getContentDescription().toString());
                    assert gates != null;
                    gates.add(closestInGate.getContentDescription().toString());
                    gateEdges.put(lastGate.getContentDescription().toString(), gates);
                    if (firstGate) {
                        idex = 0;
                        closestInGate.setInputConnectionStatus(0, true);
                    } else {
                        idex = 1;
                        closestInGate.setInputConnectionStatus(1, true);
                    }
                    lastGate.addEdgeGate(closestInGate.getContentDescription().toString(), idex);
                    if (lastGate.getOutputStatus()) {
                        closestInGate.setInputStatus(idex, true);
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

    public static void incrementPowerCount() {
        powerCount++;
    }

    public static int getPowerCount() {
        return powerCount;
    }
}