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
package com.shamim.landmeasurement.util;

import androidx.annotation.StringRes;
import com.shamim.landmeasurement.R;

public class UnitConverter {

  // ===== LENGTH UNITS =====
  public enum LengthUnit {
    INCH(R.string.unit_inch, 1.0 / 12.0),
    FOOT(R.string.unit_foot, 1.0),
    YARD(R.string.unit_yard, 3.0),
    HATH(R.string.unit_hath, 1.5),
    METER(R.string.unit_meter, 3.28084),
    CENTIMETER(R.string.unit_centimeter, 0.0328084),
    MILLIMETER(R.string.unit_millimeter, 0.00328084);

    private final int resId;
    private final double toFeet;

    LengthUnit(@StringRes int resId, double toFeet) {
      this.resId = resId;
      this.toFeet = toFeet;
    }

    /**
     * To feet.
     *
     * @param value the value
     * @return the result of the operation
     */
    public double toFeet(double value) {
      return value * toFeet;
    }

    /**
     * From feet.
     *
     * @param feet the feet
     * @return the result of the operation
     */
    public double fromFeet(double feet) {
      return feet / toFeet;
    }

    /**
     * Get res id.
     *
     * @return the result of the operation
     */
    public int getResId() {
      return resId;
    }

    // resId → enum
    /**
     * From res id.
     *
     * @param resId the resId
     * @return the result of the operation
     */
    public static LengthUnit fromResId(int resId) {
      for (LengthUnit unit : values()) {
        if (unit.resId == resId) return unit;
      }
      throw new IllegalArgumentException("Unknown LengthUnit resId: " + resId);
    }
  }

  // ===== AREA UNITS =====
  public enum AreaUnit {
    SQFT(R.string.unit_sqft, 1.0),
    SQM(R.string.unit_sqm, 10.7639),
    SHOTOK(R.string.unit_shotok, 435.6),
    KATHA(R.string.unit_katha, 720.0),
    BIGHA(R.string.unit_bigha, 14400.0),
    ACRE(R.string.unit_acre, 43560.0),
    HECTARE(R.string.unit_hectare, 107639.0),
    KORA(R.string.unit_kora, 435.6 * 2),
    JOISTHO(R.string.unit_joistho, 435.6 * 2 * 10),
    KANI(R.string.unit_kani, 435.6 * 2 * 80);

    private final int resId;
    private final double toSqFt;

    AreaUnit(@StringRes int resId, double toSqFt) {
      this.resId = resId;
      this.toSqFt = toSqFt;
    }

    /**
     * To sq ft.
     *
     * @param value the value
     * @return the result of the operation
     */
    public double toSqFt(double value) {
      return value * toSqFt;
    }

    /**
     * From sq ft.
     *
     * @param sqFt the sqFt
     * @return the result of the operation
     */
    public double fromSqFt(double sqFt) {
      return sqFt / toSqFt;
    }

    /**
     * Get res id.
     *
     * @return the result of the operation
     */
    public int getResId() {
      return resId;
    }

    // resId → enum
    /**
     * From res id.
     *
     * @param resId the resId
     * @return the result of the operation
     */
    public static AreaUnit fromResId(int resId) {
      for (AreaUnit unit : values()) {
        if (unit.resId == resId) return unit;
      }
      throw new IllegalArgumentException("Unknown AreaUnit resId: " + resId);
    }
  }

  // ===== ENUM BASED =====

  /**
   * Convert length.
   *
   * @param value the value
   * @param from the from
   * @param to the to
   * @return the result of the operation
   */
  public static double convertLength(double value, LengthUnit from, LengthUnit to) {
    double feet = from.toFeet(value);
    return to.fromFeet(feet);
  }

  /**
   * Convert area.
   *
   * @param value the value
   * @param from the from
   * @param to the to
   * @return the result of the operation
   */
  public static double convertArea(double value, AreaUnit from, AreaUnit to) {
    double sqFt = from.toSqFt(value);
    return to.fromSqFt(sqFt);
  }

  // ===== ID BASED =====

  /**
   * Convert length.
   *
   * @param value the value
   * @param fromResId the fromResId
   * @param toResId the toResId
   * @return the result of the operation
   */
  public static double convertLength(double value, int fromResId, int toResId) {
    LengthUnit from = LengthUnit.fromResId(fromResId);
    LengthUnit to = LengthUnit.fromResId(toResId);
    return convertLength(value, from, to);
  }

  /**
   * Convert area.
   *
   * @param value the value
   * @param fromResId the fromResId
   * @param toResId the toResId
   * @return the result of the operation
   */
  public static double convertArea(double value, int fromResId, int toResId) {
    AreaUnit from = AreaUnit.fromResId(fromResId);
    AreaUnit to = AreaUnit.fromResId(toResId);
    return convertArea(value, from, to);
  }
}
