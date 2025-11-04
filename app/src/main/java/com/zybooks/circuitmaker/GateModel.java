package com.zybooks.circuitmaker;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Path;

public class GateModel extends androidx.appcompat.widget.AppCompatButton {

    private Path inputPath;
    private Float[] inputPoints;
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
        inputPath = new Path();
        inputPoints = new Float[]{0.0F, 0.0F};
        outputPoint = new Float[]{0.0F, 0.0F};
        inputStatus = new Boolean[]{false, false};
        outputStatus = false;;
    }

    // Getters
    public Path getInputPath() {
        return inputPath;
    }

    public Float[] getInputPoints() {
        return inputPoints;
    }

    public Float[] getOutputPoint() {
        return outputPoint;
    }

    public Boolean[] getInputStatus() {
        return inputStatus;
    }

    public Boolean getOutputStatus() {
        return outputStatus;
    }

    // Setters
    public void setInputPath(Path inputPath) {
        this.inputPath = inputPath;
    }

    public void setInputPoints(Float[] inputPoints) {
        this.inputPoints = inputPoints;
    }

    public void setOutputPoint(Float[] outputPoint) {
        this.outputPoint = outputPoint;
    }

    public void setInputStatus(Boolean[] inputStatus) {
        this.inputStatus = inputStatus;
    }

    public void setOutputStatus(Boolean outputStatus) {
        this.outputStatus = outputStatus;
    }
}
