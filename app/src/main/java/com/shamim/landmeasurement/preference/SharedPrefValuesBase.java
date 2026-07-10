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
import androidx.annotation.NonNull;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;
import com.shamim.landmeasurement.util.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple SharedPreferences helper with listener support. Initialize once in Application.onCreate():
 * SharedPrefValues.init(appContext);
 */
public final class SharedPrefValuesBase {

  private static final String PREF_NAME = "app_prefs_v1";
  private static SharedPreferences prefs;
  private static final Set<OnPrefChangeListener> listeners =
      Collections.synchronizedSet(new HashSet<>());

  public interface OnPrefChangeListener {
    void onPrefChanged(@NonNull String key, @NonNull String newValue);
  }

  // init must be called once (e.g. from Application)
  /**
   * Init.
   *
   * @param context the context
   */
  public static void init(@NonNull Context context) {
    if (prefs == null) {
      prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
  }

  /**
   * Get value.
   *
   * @param key the key
   * @param defaultValue the defaultValue
   * @return the result of the operation
   */
  public static String getValue(@NonNull String key, @NonNull String defaultValue) {
    ensureInit();
    return prefs.getString(key, defaultValue);
  }

  /**
   * Set value.
   *
   * @param key the key
   * @param value the value
   */
  public static void setValue(@NonNull String key, @NonNull String value) {
    ensureInit();
    String old = prefs.getString(key, null);
    if (value.equals(old)) return; // nothing changed

    prefs.edit().putString(key, value).apply();

    // notify listeners
    synchronized (listeners) {
      for (OnPrefChangeListener l : listeners) {
        try {
          l.onPrefChanged(key, value);
        } catch (Exception ignore) {
          // defensive: a misbehaving listener won't crash the loop
        }
      }
    }
  }

  /**
   * Add listener.
   *
   * @param l the l
   */
  public static void addListener(@NonNull OnPrefChangeListener l) {
    listeners.add(l);
  }

  /**
   * Remove listener.
   *
   * @param l the l
   */
  public static void removeListener(@NonNull OnPrefChangeListener l) {
    listeners.remove(l);
  }

  /** Ensure init. */
  private static void ensureInit() {
    if (prefs == null) {
      throw new IllegalStateException(
          "SharedPrefValues not initialized. Call SharedPrefValues.init(context) in"
              + " Application.onCreate()");
    }
  }
}
