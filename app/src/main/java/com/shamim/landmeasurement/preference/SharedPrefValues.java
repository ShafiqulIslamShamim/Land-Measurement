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
package com.shamim.landmeasurement.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.preference.PreferenceManager;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;
import com.shamim.landmeasurement.util.*;

public class SharedPrefValues {

  // Gets the default SharedPreferences
  /**
   * Get shared preferences.
   *
   * @return the result of the operation
   */
  private static SharedPreferences getSharedPreferences() {

    Context context = AppContext.get();
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  // Unified method to get string from SharedPreferences
  /**
   * Get value.
   *
   * @param key the key
   * @param defaultValue the defaultValue
   * @return the result of the operation
   */
  public static String getValue(String key, String defaultValue) {
    SharedPreferences prefs = getSharedPreferences();
    if (prefs != null && prefs.contains(key)) {
      String value = prefs.getString(key, null);
      return !TextUtils.isEmpty(value) ? value : defaultValue;
    }
    return defaultValue;
  }

  // Unified method to get int from SharedPreferences
  /**
   * Get value.
   *
   * @param key the key
   * @param defaultValue the defaultValue
   * @return the result of the operation
   */
  public static int getValue(String key, int defaultValue) {
    String value = getValue(key, String.valueOf(defaultValue));
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  // Unified method to get float from SharedPreferences
  /**
   * Get value.
   *
   * @param key the key
   * @param defaultValue the defaultValue
   * @return the result of the operation
   */
  public static float getValue(String key, float defaultValue) {
    String value = getValue(key, String.valueOf(defaultValue));
    try {
      return Float.parseFloat(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  // Unified method to get double from SharedPreferences
  /**
   * Get value.
   *
   * @param key the key
   * @param defaultValue the defaultValue
   * @return the result of the operation
   */
  public static double getValue(String key, double defaultValue) {
    String value = getValue(key, String.valueOf(defaultValue));
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Get value.
   *
   * @param key the key
   * @param defaultValue the defaultValue
   * @return the result of the operation
   */
  public static boolean getValue(String key, boolean defaultValue) {
    String value = getValue(key, defaultValue ? "1" : "0");

    try {
      return Integer.parseInt(value) != 0;
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Parse flexible boolean.
   *
   * @param value the value
   * @return the result of the operation
   */
  public static boolean parseFlexibleBoolean(String value) {
    if (value == null) return false;
    value = value.trim().toLowerCase();
    return value.equals("true") || value.equals("1");
  }

  /**
   * Boolean to int.
   *
   * @param value the value
   * @return the result of the operation
   */
  public static int booleanToInt(boolean value) {
    return value ? 1 : 0;
  }

  /**
   * Put value.
   *
   * @param key the key
   * @param value the value
   */
  public static void putValue(String key, String value) {
    SharedPreferences prefs = getSharedPreferences();
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(key, value);
    editor.apply(); // or editor.commit();
  }

  /**
   * Put value if absent.
   *
   * @param key the key
   * @param defaultValue the defaultValue
   */
  public static void putValueIfAbsent(String key, String defaultValue) {
    SharedPreferences prefs = getSharedPreferences();
    if (!prefs.contains(key)) {
      SharedPreferences.Editor editor = prefs.edit();
      editor.putString(key, defaultValue);
      editor.apply();
    }
  }
}
