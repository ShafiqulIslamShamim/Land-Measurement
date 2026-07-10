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
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.PreferenceManager;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.preference.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;

public final class LocaleHelper {

  private static final String KEY_LANGUAGE = "language_preference";

  /** Locale helper. */
  private LocaleHelper() {}

  /* ================== PUBLIC API ================== */

  // Apply saved language (CALL in Application / Launcher Activity)
  /**
   * Apply locale.
   *
   * @param context the context
   */
  public static void applyLocale(Context context) {
    String lang = getSavedLanguage(context);

    if ("default".equals(lang)) {
      AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList());
    } else {
      LocaleListCompat locales = LocaleListCompat.forLanguageTags(lang);

      AppCompatDelegate.setApplicationLocales(locales);
    }
  }

  // Save + apply language
  /**
   * Save language.
   *
   * @param context the context
   * @param lang the lang
   */
  public static void saveLanguage(Context context, String lang) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    prefs.edit().putString(KEY_LANGUAGE, lang).apply();
    applyLocale(context);
  }

  // Follow device language
  /**
   * Clear language.
   *
   * @param context the context
   */
  public static void clearLanguage(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    prefs.edit().remove(KEY_LANGUAGE).apply();

    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList());
  }

  /**
   * Get saved language.
   *
   * @param context the context
   * @return the result of the operation
   */
  public static String getSavedLanguage(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    return prefs.getString(KEY_LANGUAGE, "default");
  }
}
