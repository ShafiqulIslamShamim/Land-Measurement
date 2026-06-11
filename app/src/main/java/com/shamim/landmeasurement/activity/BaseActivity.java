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

public abstract class BaseActivity extends AppCompatActivity {

  private int currentAppliedThemeRes = 0;
  private String currentAppliedLanguage = "";

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
    int expectedTheme = getExpectedThemeRes();
    String expectedLanguage = LocaleHelper.getSavedLanguage(this);

    if (currentAppliedThemeRes != expectedTheme || !currentAppliedLanguage.equals(expectedLanguage)) {
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
  protected int getExpectedThemeRes() {
    boolean isLight = isLightThemeActive();
    String appThemePref = SharedPrefValues.getValue("app_theme_preference", "0");
    boolean isAmoled = SharedPrefValues.getValue("amoled_black_mode", false);

    final int themeRes;

    if (isLight) {
      switch (appThemePref) {
        case "1":
          themeRes = R.style.AppThemeEmeraldLight;
          break;
        case "2":
          themeRes = R.style.AppThemeBlossomLight;
          break;
        case "3":
          themeRes = R.style.AppThemeOceanLight;
          break;
        case "4":
          themeRes = R.style.AppThemeAmberLight;
          break;
        case "5":
          themeRes = R.style.AppThemeCoralLight;
          break;
        case "6":
          themeRes = R.style.AppThemeDefaultLight;
          break;
        default:
          themeRes = R.style.AppThemeLight;
          break;
      }
    } else {
      switch (appThemePref) {
        case "1":
          themeRes = isAmoled ? R.style.AppThemeEmeraldDarkAmoled : R.style.AppThemeEmeraldDark;
          break;
        case "2":
          themeRes = isAmoled ? R.style.AppThemeBlossomDarkAmoled : R.style.AppThemeBlossomDark;
          break;
        case "3":
          themeRes = isAmoled ? R.style.AppThemeOceanDarkAmoled : R.style.AppThemeOceanDark;
          break;
        case "4":
          themeRes = isAmoled ? R.style.AppThemeAmberDarkAmoled : R.style.AppThemeAmberDark;
          break;
        case "5":
          themeRes = isAmoled ? R.style.AppThemeCoralDarkAmoled : R.style.AppThemeCoralDark;
          break;
        case "6":
          themeRes = isAmoled ? R.style.AppThemeDefaultDarkAmoled : R.style.AppThemeDefaultDark;
          break;
        default:
          themeRes = isAmoled ? R.style.AppThemeDarkAmoled : R.style.AppThemeDark;
          break;
      }
    }

    return themeRes;
  }

  protected void applyLocalTheme() {
    currentAppliedThemeRes = getExpectedThemeRes();
    setTheme(currentAppliedThemeRes);
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
  private boolean isLightThemeActive() {

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
