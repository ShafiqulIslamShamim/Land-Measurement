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
package com.shamim.landmeasurement.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import com.google.android.material.button.MaterialButton;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.util.*;

public class IntroActivity extends BaseActivity {

  private SharedPreferences prefs;

  /**
   * On create.
   *
   * @param savedInstanceState the savedInstanceState
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // ✅ Initialize SharedPreferences
    prefs = getSharedPreferences("intro_pref", MODE_PRIVATE);

    setContentView(R.layout.activity_intro);

    RadioGroup languageGroup = findViewById(R.id.languageGroup);
    MaterialButton btnGetStarted = findViewById(R.id.btnGetStarted);

    btnGetStarted.setOnClickListener(
        v -> {
          int selectedId = languageGroup.getCheckedRadioButtonId();
          String lang = (selectedId == R.id.langBangla) ? "bn" : "default";

          // ✅ Save language + intro flag
          LocaleHelper.saveLanguage(IntroActivity.this, lang); // or "default"
          prefs.edit().putBoolean("intro_shown", true).apply();

          // ✅ Restart flow cleanly
          Intent intent = new Intent(IntroActivity.this, MainActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          startActivity(intent);
        });
  }
}
