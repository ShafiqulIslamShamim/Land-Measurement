package com.shamim.landmeasurement.activity;

import android.content.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.preference.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;
import com.shamim.landmeasurement.util.*;
import java.util.*;

public abstract class BaseActivity extends AppCompatActivity {

  protected String lastAppliedThemePref;
  protected String lastAppliedAppThemePref;
  protected boolean lastAppliedAmoled;

  protected String currentAppliedLanguage;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    applyLocalTheme();

    LocaleHelper.applyLocale(this);
    currentAppliedLanguage = LocaleHelper.getSavedLanguage(this);

    // Modern Android edge-to-edge
    EdgeToEdge.enable(this);

    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onResume() {
    super.onResume();

    String themePref = SharedPrefValues.getValue("theme_preference", "0");
    String appThemePref = SharedPrefValues.getValue("app_theme_preference", "0");
    boolean amoled = SharedPrefValues.getValue("amoled_black_mode", false);
    String language = LocaleHelper.getSavedLanguage(this);

    boolean needsRecreate =
        !Objects.equals(lastAppliedThemePref, themePref)
            || !Objects.equals(lastAppliedAppThemePref, appThemePref)
            || lastAppliedAmoled != amoled
            || !Objects.equals(currentAppliedLanguage, language);

    if (needsRecreate) {
      lastAppliedThemePref = themePref;
      lastAppliedAppThemePref = appThemePref;
      lastAppliedAmoled = amoled;
      currentAppliedLanguage = language;

      recreate();
    }
  }

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    afterContentSet();
  }

  @Override
  public void setContentView(View view) {
    super.setContentView(view);
    afterContentSet();
  }

  private void afterContentSet() {
    setupEdgeToEdgePadding();
    getWindow().getDecorView().post(this::applySystemBarAppearance);
  }

  // THEME LOGIC (Light/Dark + AppTheme/AppThemeDefault)
  protected void applyLocalTheme() {
    boolean isLight = isLightThemeActive();
    String appThemePref = SharedPrefValues.getValue("app_theme_preference", "0");
    boolean amoledEnabled = SharedPrefValues.getValue("amoled_black_mode", false);

    lastAppliedThemePref = SharedPrefValues.getValue("theme_preference", "0");
    lastAppliedAppThemePref = appThemePref;
    lastAppliedAmoled = amoledEnabled;

    final int themeRes;

    if (appThemePref.equals("0")) {
      themeRes = isLight ? R.style.AppThemeLight : R.style.AppThemeDark;
    } else if (appThemePref.equals("1")) {
      themeRes = isLight ? R.style.AppThemeEmeraldLight : R.style.AppThemeEmeraldDark;
    } else if (appThemePref.equals("2")) {
      themeRes = isLight ? R.style.AppThemeBlossomLight : R.style.AppThemeBlossomDark;
    } else if (appThemePref.equals("3")) {
      themeRes = isLight ? R.style.AppThemeOceanLight : R.style.AppThemeOceanDark;
    } else if (appThemePref.equals("4")) {
      themeRes = isLight ? R.style.AppThemeAmberLight : R.style.AppThemeAmberDark;
    } else if (appThemePref.equals("5")) {
      themeRes = isLight ? R.style.AppThemeCoralLight : R.style.AppThemeCoralDark;
    } else {
      themeRes = isLight ? R.style.AppThemeLight : R.style.AppThemeDark;
    }

    setTheme(themeRes);

    if (!isLight && amoledEnabled) {
      getTheme().applyStyle(R.style.AmoledOverlay, true);
    }
  }

  // Status + Navigation bar icon color (Light/Dark)
  protected void applySystemBarAppearance() {
    boolean isLight = isLightThemeActive();

    View decorView = getWindow().getDecorView();
    WindowInsetsControllerCompat controller =
        new WindowInsetsControllerCompat(getWindow(), decorView);

    controller.setAppearanceLightStatusBars(isLight);
    controller.setAppearanceLightNavigationBars(isLight);
  }

  // Edge-to-edge safe padding
  private void setupEdgeToEdgePadding() {
    View root = findViewById(android.R.id.content);
    if (root == null) return;

    ViewCompat.setOnApplyWindowInsetsListener(
        root,
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());

          int bottomPadding = Math.max(systemBars.bottom, imeInsets.bottom);

          v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);

          return insets;
        });
  }

  // Detect whether current UI should be "light" appearance
  protected boolean isLightThemeActive() {

    String themePref = SharedPrefValues.getValue("theme_preference", "0");

    switch (themePref) {
      case "2": // Dark forced
        return false;

      case "3": // Light forced
        return true;

      default: // Follow system
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        return mode != Configuration.UI_MODE_NIGHT_YES;
    }
  }

  protected void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }
}
