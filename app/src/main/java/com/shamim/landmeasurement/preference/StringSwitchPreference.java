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
import android.util.AttributeSet;
import androidx.preference.SwitchPreferenceCompat;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;
import com.shamim.landmeasurement.util.*;

public class StringSwitchPreference extends SwitchPreferenceCompat {

  /**
   * String switch preference.
   *
   * @param context the context
   * @param attrs the attrs
   */
  public StringSwitchPreference(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Use Material3 switch widget
    setWidgetLayoutResource(R.layout.preference_switch_material3);
  }

  /**
   * Persist boolean.
   *
   * @param value the value
   * @return the result of the operation
   */
  @Override
  protected boolean persistBoolean(boolean value) {
    return persistString(value ? "1" : "0");
  }

  /**
   * Get persisted boolean.
   *
   * @param defaultReturnValue the defaultReturnValue
   * @return the result of the operation
   */
  @Override
  public boolean getPersistedBoolean(boolean defaultReturnValue) {
    String stringValue = getPersistedString(defaultReturnValue ? "1" : "0");
    return "1".equals(stringValue);
  }
}
