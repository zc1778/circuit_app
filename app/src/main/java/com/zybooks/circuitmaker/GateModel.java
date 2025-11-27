package com.zybooks.circuitmaker;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

public class GateModel extends androidx.appcompat.widget.AppCompatButton {

    private int cloneNumber;
    private ArrayList<Float[]> inputPoints;
    private Float[] outputPoint;
    private ArrayList<Boolean> inputStatus;
    private boolean outputStatus;

    public GateModel(Context context) {
        super(context);
        init();
    }

    public GateModel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GateModel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        cloneNumber = 0;
        inputPoints = new ArrayList<>();
        inputPoints.add(new Float[]{-1.0F, -1.0F});
        inputPoints.add(new Float[]{-1.0F, -1.0F});
        outputPoint = new Float[]{0.0F, 0.0F};
        inputStatus = new ArrayList<>();
        inputStatus.add(false);
        inputStatus.add(false);
        outputStatus = false;
    }

    // Getters

    public int getCloneNumber() { return cloneNumber; }

    public ArrayList<Float[]> getInputPoints() {
        return inputPoints;
    }

    public Float[] getOutputPoint() {
        return outputPoint;
    }

    public boolean getOutputStatus() { return outputStatus; }

    public ArrayList<Boolean> getInputStatus() { return inputStatus; }

    // Setters

    public void incrementCloneNumber() { cloneNumber++; }


    // Setters

    public void setCloneNumber(int cloneNumber) { this.cloneNumber = cloneNumber; }
    public void setInputPoints(int index, Float[] inputPoint) {
        this.inputPoints.set(index, inputPoint);
    }

    public void setOutputPoint(Float[] outputPoint) {
        this.outputPoint = outputPoint;
    }

    public void setInputStatus(int index, Boolean inputStatus) { this.inputStatus.set(index, inputStatus); }

    public void setOutputStatus(Boolean outputStatus) {
        this.outputStatus = outputStatus;
    }
}
