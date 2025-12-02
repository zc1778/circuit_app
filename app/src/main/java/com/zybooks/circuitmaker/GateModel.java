package com.zybooks.circuitmaker;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.HashMap;

public class GateModel extends androidx.appcompat.widget.AppCompatButton {

    private int cloneNumber;
    private ArrayList<Float[]> inputPoints;
    private ArrayList<Boolean> inputConnectionStatus;
    private ArrayList<Boolean> inputStatus;
    private Float[] outputPoint;
    private boolean outputConnectionStatus;
    private boolean outputStatus;
    private HashMap<String, Integer> edgeGates;

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
        inputConnectionStatus = new ArrayList<>();
        inputConnectionStatus.add(false);
        inputConnectionStatus.add(false);
        inputStatus = new ArrayList<>();
        inputStatus.add(false);
        inputStatus.add(false);
        outputPoint = new Float[]{-1.0F, -1.0F};
        outputConnectionStatus = false;
        outputStatus = false;
        edgeGates = new HashMap<>();
    }

    // Getters

    public int getCloneNumber() { return cloneNumber; }

    public ArrayList<Float[]> getInputPoints() {
        return inputPoints;
    }

    public ArrayList<Boolean> getInputConnectionStatus() { return inputConnectionStatus; }

    public ArrayList<Boolean> getInputStatus() { return inputStatus; }

    public Float[] getOutputPoint() {
        return outputPoint;
    }

    public boolean getOutputConnectionStatus() { return outputConnectionStatus; }

    public boolean getOutputStatus() { return outputStatus; }

    public HashMap<String, Integer> getEdgeGates() { return edgeGates; }

    // Modifiers

    public void incrementCloneNumber() { cloneNumber++; }

    public void addEdgeGate(String edgeGate, int inputIndex) { edgeGates.put(edgeGate, inputIndex); }

    // Setters

    public void setCloneNumber(int cloneNumber) { this.cloneNumber = cloneNumber; }
    public void setInputPoints(int index, Float[] inputPoint) { this.inputPoints.set(index, inputPoint); }

    public void setInputConnectionStatus(int index, Boolean inputConnectionStatus) { this.inputConnectionStatus.set(index, inputConnectionStatus); }

    public void setInputStatus(int index, Boolean inputStatus) { this.inputStatus.set(index, inputStatus); }

    public void setOutputPoint(Float[] outputPoint) {
        this.outputPoint = outputPoint;
    }

    public void setOutputConnectionStatus(Boolean outputConnectionStatus) { this.outputConnectionStatus = outputConnectionStatus; }

    public void setOutputStatus(Boolean outputStatus) {
        this.outputStatus = outputStatus;
    }

    public void setEdgeGates(HashMap<String, Integer> edgeGates) { this.edgeGates = edgeGates; }

    public void clearInputPoints() {
        inputPoints.clear();
    }
    public void clearInputConnectionStatus() {
        inputConnectionStatus.clear();
    }

    public void clearInputStatus() {
        inputStatus.clear();
    }

    public void analyzeOutput() {
        String desc = this.getContentDescription().toString().split("_")[0];
        switch (desc) {
            case "YES":
                outputStatus = inputStatus.get(0);
                break;
            case "NOT":
                outputStatus = !inputStatus.get(0);
                break;
            case "AND":
                outputStatus = inputStatus.get(0) && inputStatus.get(1);
                break;
            case "NAND":
                outputStatus = !(inputStatus.get(0) && inputStatus.get(1));
                break;
            case "OR":
                outputStatus = inputStatus.get(0) || inputStatus.get(1);
                break;
            case "NOR":
                outputStatus = !(inputStatus.get(0) || inputStatus.get(1));
                break;
            case "XOR":
                outputStatus = inputStatus.get(0) ^ inputStatus.get(1);
                break;
            case "XNOR":
                outputStatus = !(inputStatus.get(0) ^ inputStatus.get(1));
                break;
            default:
                outputStatus = false;
                break;
        }
    }
}
