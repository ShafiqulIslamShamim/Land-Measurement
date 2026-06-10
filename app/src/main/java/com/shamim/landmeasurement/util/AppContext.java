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

  public static void init(Context context) {
    if (appContext == null) {
      appContext = context.getApplicationContext();
    }
  }

  public static Context get() {
    if (appContext == null) {
      throw new IllegalStateException("AppContext not initialized! Call init() first.");
    }
    return appContext;
  }
}
