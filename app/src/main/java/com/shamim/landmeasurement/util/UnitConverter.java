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

    public double toFeet(double value) {
      return value * toFeet;
    }

    public double fromFeet(double feet) {
      return feet / toFeet;
    }

    public int getResId() {
      return resId;
    }

    // resId → enum
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

    public double toSqFt(double value) {
      return value * toSqFt;
    }

    public double fromSqFt(double sqFt) {
      return sqFt / toSqFt;
    }

    public int getResId() {
      return resId;
    }

    // resId → enum
    public static AreaUnit fromResId(int resId) {
      for (AreaUnit unit : values()) {
        if (unit.resId == resId) return unit;
      }
      throw new IllegalArgumentException("Unknown AreaUnit resId: " + resId);
    }
  }

  // ===== ENUM BASED =====

  public static double convertLength(double value, LengthUnit from, LengthUnit to) {
    double feet = from.toFeet(value);
    return to.fromFeet(feet);
  }

  public static double convertArea(double value, AreaUnit from, AreaUnit to) {
    double sqFt = from.toSqFt(value);
    return to.fromSqFt(sqFt);
  }

  // ===== ID BASED =====

  public static double convertLength(double value, int fromResId, int toResId) {
    LengthUnit from = LengthUnit.fromResId(fromResId);
    LengthUnit to = LengthUnit.fromResId(toResId);
    return convertLength(value, from, to);
  }

  public static double convertArea(double value, int fromResId, int toResId) {
    AreaUnit from = AreaUnit.fromResId(fromResId);
    AreaUnit to = AreaUnit.fromResId(toResId);
    return convertArea(value, from, to);
  }
}
