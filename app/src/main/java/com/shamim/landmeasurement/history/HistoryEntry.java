package com.shamim.landmeasurement.history;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history_entries")
public class HistoryEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String shapeTitle;
    private String inputs;
    private double areaSqFt;
    private long timestamp;
    private String activityClassName;
    private String serializedInputs;

    public HistoryEntry() {
    }

    public HistoryEntry(String shapeTitle, String inputs, double areaSqFt, long timestamp, String activityClassName, String serializedInputs) {
        this.shapeTitle = shapeTitle;
        this.inputs = inputs;
        this.areaSqFt = areaSqFt;
        this.timestamp = timestamp;
        this.activityClassName = activityClassName;
        this.serializedInputs = serializedInputs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShapeTitle() {
        return shapeTitle;
    }

    public void setShapeTitle(String shapeTitle) {
        this.shapeTitle = shapeTitle;
    }

    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }

    public double getAreaSqFt() {
        return areaSqFt;
    }

    public void setAreaSqFt(double areaSqFt) {
        this.areaSqFt = areaSqFt;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getActivityClassName() {
        return activityClassName;
    }

    public void setActivityClassName(String activityClassName) {
        this.activityClassName = activityClassName;
    }

    public String getSerializedInputs() {
        return serializedInputs;
    }

    public void setSerializedInputs(String serializedInputs) {
        this.serializedInputs = serializedInputs;
    }
}
