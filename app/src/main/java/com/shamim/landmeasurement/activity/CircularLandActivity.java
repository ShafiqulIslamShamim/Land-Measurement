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

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.*;
import com.shamim.landmeasurement.view.*;
import java.util.*;

public class CircularLandActivity extends BaseActivity
    implements com.shamim.landmeasurement.history.HistoryItemSupport {

  private LandResultManager resultManager;
  private LandSingleEditTextCardView radiusInput;
  private ScrollView scrollView;
  private MaterialToolbar toolbar;

  /**
   * On create.
   *
   * @param savedInstanceState the savedInstanceState
   */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_circular_land);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getString(R.string.item_circular));
    }

    radiusInput = findViewById(R.id.radius_input);
    scrollView = findViewById(R.id.scroll_view);

    radiusInput.setTitle(getString(R.string.card_radius_title));

    resultManager = new LandResultManager(findViewById(android.R.id.content), this);
    resultManager.setOnCalculateClickListener(this::calculate);

    String serialized = getIntent().getStringExtra("serialized_inputs");
    if (serialized != null) {
      restoreSerializedInputs(serialized);
    }
  }

  /**
   * Get serialized inputs.
   *
   * @return the result of the operation
   */
  @Override
  public String getSerializedInputs() {
    return radiusInput.getSerializedState();
  }

  /**
   * Restore serialized inputs.
   *
   * @param data the data
   */
  @Override
  public void restoreSerializedInputs(String data) {
    if (data == null || data.isEmpty()) return;
    getIntent().putExtra("skip_history_save", true);
    radiusInput.restoreState(data);
    calculate();
    getIntent().putExtra("skip_history_save", false);
  }

  /** Calculate. */
  private void calculate() {
    double Radius = radiusInput.getValueInFeet();

    if (!radiusInput.hasValidInput()) {
      Toast.makeText(this, getString(R.string.item_circular_input_error), Toast.LENGTH_LONG).show();
      resultManager.hideResult();
      return;
    }

    double areaSqFt = Math.PI * Math.pow(Radius, 2);

    resultManager.showResultWithScroll(areaSqFt, "", scrollView);

    String shareText = resultManager.buildShareableText(areaSqFt, sharedTextHeading());

    resultManager.setLastSharedText(shareText);
  }

  /**
   * Shared text heading.
   *
   * @return the result of the operation
   */
  private String sharedTextHeading() {

    return getString(R.string.card_radius_title) + " : " + radiusInput.getValueAsString();
  }

  /**
   * On support navigate up.
   *
   * @return the result of the operation
   */
  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
