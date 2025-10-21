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
import java.util.Arrays;

public class LineView extends View {

    private Paint paint;

    private ArrayList<Path> pathList;

    private ArrayList<ArrayList<Float>> inputPoints;
    private ArrayList<ArrayList<Float>> outputPoints;

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
        pathList = new ArrayList<Path>();
        inputPoints = new ArrayList<>();
        outputPoints = new ArrayList<>();
        paint.setColor(Color.argb(1.0F, 0.0F, 0.0F, 0.0F));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        for (Path path : pathList) {
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                pathList.add(currentPath);
                currentPath.moveTo(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_UP:
                boolean connection = false;
                float nextX = event.getX(), nextY = event.getY();
                for (int i = 0; i < inputPoints.size(); i++) {
                    Log.d("Connection Point X: ", (inputPoints.get(i).get(0).toString()));
                    Log.d("Connection Point Y: ", (inputPoints.get(i).get(1)).toString());
                    if (Math.abs(inputPoints.get(i).get(0) - event.getX()) <= 100
                        && Math.abs(inputPoints.get(i).get(1) - event.getY()) <= 100) {
                        nextX = inputPoints.get(i).get(0);
                        nextY = inputPoints.get(i).get(1);
                    }
                }
                Log.d("End Point X: ", String.valueOf(nextX));
                Log.d("End Point Y: ", String.valueOf(nextY));
                Log.d("Event Point X: ", String.valueOf(event.getX()));
                Log.d("Event Point Y: ", String.valueOf(event.getY()));
                currentPath.lineTo(nextX, nextY);
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void clearPaths() {
        pathList.clear();
        invalidate();
    }

    public void addInputPoint(float x, float y) {
        inputPoints.add(new ArrayList<>(Arrays.asList(x, y)));
    }

    public void addOutputPoint(float x, float y) {
        outputPoints.add(new ArrayList<>(Arrays.asList(x, y)));
    }
}