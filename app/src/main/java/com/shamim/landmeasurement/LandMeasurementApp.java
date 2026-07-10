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
package com.shamim.landmeasurement;

import android.app.Application;
import android.content.Context;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.preference.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;
import com.shamim.landmeasurement.util.*;
import io.github.mohammedbaqernull.seasonal.SeasonalEffects;

public class LandMeasurementApp extends Application {
  private static Context appContext;

  /** On create. */
  @Override
  public void onCreate() {
    super.onCreate();
    AppContext.init(this);
    boolean seasonalEffect = SharedPrefValues.getValue("disable_seasonal_effect", false);

    if (seasonalEffect != true && GlobalWinterSystem.isWinterNow()) {
      SeasonalEffects.INSTANCE.init(this);
      SeasonalEffects.INSTANCE.enableChristmas();
      SeasonalEffects.INSTANCE.setSnowflakeCount(20);
    }
  }
}
