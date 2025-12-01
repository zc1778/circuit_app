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
    private ArrayList<Boolean> inputConnectionStatus;
    private boolean outputConnectionStatus;

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
        outputPoint = new Float[]{-1.0F, -1.0F};
        inputConnectionStatus = new ArrayList<>();
        inputStatus = new ArrayList<>();
        inputConnectionStatus.add(false);
        inputConnectionStatus.add(false);
        inputStatus.add(false);
        inputStatus.add(false);
        outputConnectionStatus = false;
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

    public boolean getOutputConnectionStatus() { return outputConnectionStatus; }

    public boolean getOutputStatus() { return outputStatus; }

    public ArrayList<Boolean> getInputConnectionStatus() { return inputConnectionStatus; }

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


    public void setInputConnectionStatus(int index, Boolean inputConnectionStatus) { this.inputConnectionStatus.set(index, inputConnectionStatus); }

    public void setInputStatus(int index, Boolean inputStatus) { this.inputStatus.set(index, inputStatus); }

    public void setOutputConnectionStatus(Boolean outputConnectionStatus) { this.outputConnectionStatus = outputConnectionStatus; }

    public void setOutputStatus(Boolean outputStatus) {
        this.outputStatus = outputStatus;
    }
}
