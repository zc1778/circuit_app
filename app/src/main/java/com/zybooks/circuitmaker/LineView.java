package com.zybooks.circuitmaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class LineView extends View {

    private Paint paint;

    private ArrayList<Path> pathList;

    private Path currentPath;

//    private Float startX;
//    private Float startY;
//    private Float endX;
//    private Float endY;

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
        paint.setColor(Color.argb(1.0F, 0.0F, 0.0F, 0.0F));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
//        setStartX(0.0F);
//        setStartY(0.0F);
//        setEndX(0.0F);
//        setEndY(0.0F);
    }

//    private void setStartY(float f) {
//        startY = f;
//    }
//
//    private void setStartX(float f) {
//        startX = f;
//    }
//
//    private void setEndY(float f) {
//        endY = f;
//    }
//
//    private void setEndX(float f) {
//        endX = f;
//    }
//
//    private float getStartY() {
//        return startY;
//    }
//
//    private float getStartX() {
//        return startX;
//    }
//
//    private float getEndY() {
//        return endY;
//    }
//
//    private float getEndX() {
//        return endX;
//    }

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
                currentPath.lineTo(event.getX(), event.getY());
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
}