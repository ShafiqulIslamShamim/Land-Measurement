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

import android.content.Context;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.preference.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;

public class AppContext {
  private static Context appContext;

  /**
   * Init.
   *
   * @param context the context
   */
  public static void init(Context context) {
    if (appContext == null) {
      appContext = context.getApplicationContext();
    }
  }

  /**
   * Get.
   *
   * @return the result of the operation
   */
  public static Context get() {
    if (appContext == null) {
      throw new IllegalStateException("AppContext not initialized! Call init() first.");
    }
    return appContext;
  }
}
