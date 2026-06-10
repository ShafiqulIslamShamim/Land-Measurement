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
