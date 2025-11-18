package com.zybooks.circuitmaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class LineView extends View {

    private Paint paint;
    private HashMap<String, Path> pathMap;
    private ArrayList<GateModel> gateList;
    private Path currentPath;

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
        gateList = new ArrayList<GateModel>();
        pathMap = new HashMap<String, Path>();
        paint.setColor(Color.argb(1.0F, 0.0F, 0.0F, 0.0F));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        for (GateModel gate : gateList) {
            Boolean[] inputStatus = gate.getInputStatus();
            ArrayList<Float[]> inputPoints = gate.getInputPoints();
            for (int i = 0; i < inputPoints.size(); i++) {
                if (inputStatus[i]) {
                    currentPath.lineTo(inputPoints.get(i)[0], inputPoints.get(i)[1]);
                }
            }
            if (gate.getOutputStatus()) {
                currentPath.moveTo(gate.getOutputPoint()[0], gate.getOutputPoint()[1]);
            }
            canvas.drawPath(currentPath, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                float firstX = -1.0F, firstY = -1.0F;
                float closestOut = Float.MAX_VALUE;
                GateModel closestOutGate = null;
                for (GateModel gate : gateList) {
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

                if (closestOutGate != null) {
                    currentPath.moveTo(firstX, firstY);
                    closestOutGate.setOutputStatus(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                float nextX = -1.0F, nextY = -1.0F;
                float closestIn = Float.MAX_VALUE;
                GateModel closestInGate = null;

                for (GateModel gate : gateList) {
                    ArrayList<Float[]> inputPoints = gate.getInputPoints();
                    for (Float[] point : inputPoints) {
                        if (Math.abs(point[0] - event.getX()) <= 200
                                && Math.abs(point[1] - event.getY()) <= 200
                                && Math.abs(point[0] - event.getX()) + Math.abs(point[1] - event.getY()) < closestIn) {
                            nextX = point[0];
                            nextY = point[1];
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
                    currentPath.lineTo(nextX, nextY);
                    pathMap.put(closestInGate.getContentDescription().toString(), currentPath);
                    closestInGate.setInputStatus(new Boolean[]{true, true});
                }
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void clearPaths() {
        pathMap.clear();
        invalidate();
    }

    public void addGate(GateModel gate) {
        gateList.add(gate);
    }

    public void updatePath(String desc, float x, float y) {
        Path path = pathMap.get(desc);
        assert path != null;
        path.lineTo(x, y);
        pathMap.put(desc, path);
    }
}