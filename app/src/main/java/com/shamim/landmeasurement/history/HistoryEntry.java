/*
 * Copyright (c) 2026 Shafiqul Islam Shamim
 * GitHub: https://github.com/ShafiqulIslamShamim/Land-Measurement
 *
 * All Rights Reserved.
 *
 * This source code is made publicly available solely for viewing, collaboration,
 * educational reference, and submitting pull requests to the official repository.
 *
 * No permission is granted to copy, modify, redistribute, sublicense, or use
 * this source code, in whole or in part, for personal, commercial, or any other
 * purpose without the prior written permission of the copyright holder.
 */
package com.shamim.landmeasurement.history;

import androidx.room.Entity;
import androidx.room.Ignore;
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

  /** History entry. */
  public HistoryEntry() {}

  /**
   * History entry.
   *
   * @param shapeTitle the shapeTitle
   * @param inputs the inputs
   * @param areaSqFt the areaSqFt
   * @param timestamp the timestamp
   * @param activityClassName the activityClassName
   * @param serializedInputs the serializedInputs
   * @return the result of the operation
   */
  @Ignore
  public HistoryEntry(
      String shapeTitle,
      String inputs,
      double areaSqFt,
      long timestamp,
      String activityClassName,
      String serializedInputs) {
    this.shapeTitle = shapeTitle;
    this.inputs = inputs;
    this.areaSqFt = areaSqFt;
    this.timestamp = timestamp;
    this.activityClassName = activityClassName;
    this.serializedInputs = serializedInputs;
  }

  /**
   * Get id.
   *
   * @return the result of the operation
   */
  public int getId() {
    return id;
  }

  /**
   * Set id.
   *
   * @param id the id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Get shape title.
   *
   * @return the result of the operation
   */
  public String getShapeTitle() {
    return shapeTitle;
  }

  /**
   * Set shape title.
   *
   * @param shapeTitle the shapeTitle
   */
  public void setShapeTitle(String shapeTitle) {
    this.shapeTitle = shapeTitle;
  }

  /**
   * Get inputs.
   *
   * @return the result of the operation
   */
  public String getInputs() {
    return inputs;
  }

  /**
   * Set inputs.
   *
   * @param inputs the inputs
   */
  public void setInputs(String inputs) {
    this.inputs = inputs;
  }

  /**
   * Get area sq ft.
   *
   * @return the result of the operation
   */
  public double getAreaSqFt() {
    return areaSqFt;
  }

  /**
   * Set area sq ft.
   *
   * @param areaSqFt the areaSqFt
   */
  public void setAreaSqFt(double areaSqFt) {
    this.areaSqFt = areaSqFt;
  }

  /**
   * Get timestamp.
   *
   * @return the result of the operation
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Set timestamp.
   *
   * @param timestamp the timestamp
   */
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Get activity class name.
   *
   * @return the result of the operation
   */
  public String getActivityClassName() {
    return activityClassName;
  }

  /**
   * Set activity class name.
   *
   * @param activityClassName the activityClassName
   */
  public void setActivityClassName(String activityClassName) {
    this.activityClassName = activityClassName;
  }

  /**
   * Get serialized inputs.
   *
   * @return the result of the operation
   */
  public String getSerializedInputs() {
    return serializedInputs;
  }

  /**
   * Set serialized inputs.
   *
   * @param serializedInputs the serializedInputs
   */
  public void setSerializedInputs(String serializedInputs) {
    this.serializedInputs = serializedInputs;
  }
}
