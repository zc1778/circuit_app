package com.zybooks.circuitmaker;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

public class GateModel extends androidx.appcompat.widget.AppCompatButton {

    private int cloneNumber;
    private ArrayList<Float[]> inputPoints;
    private Float[] outputPoint;
    private Boolean[] inputStatus;
    private Boolean outputStatus;

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
        outputPoint = new Float[]{0.0F, 0.0F};
        inputStatus = new Boolean[]{false, false};
        outputStatus = false;;
    }

    // Getters

    public int getCloneNumber() { return cloneNumber; }

    public ArrayList<Float[]> getInputPoints() {
        return inputPoints;
    }

    public Float[] getOutputPoint() {
        return outputPoint;
    }

    public Boolean getOutputStatus() { return outputStatus; }

    public Boolean[] getInputStatus() { return inputStatus; }

    // Setters

    public void incrementCloneNumber() { cloneNumber++; }

    public void setInputPoints(Float[] inputPoint) {
        this.inputPoints.add(inputPoint);
    }

    public void setOutputPoint(Float[] outputPoint) {
        this.outputPoint = outputPoint;
    }

    public void setInputStatus(Boolean[] inputStatus) { this.inputStatus = inputStatus; }

    public void setOutputStatus(Boolean outputStatus) {
        this.outputStatus = outputStatus;
    }
}
