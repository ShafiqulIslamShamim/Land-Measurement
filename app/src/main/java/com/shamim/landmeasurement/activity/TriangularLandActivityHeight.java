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

public class TriangularLandActivityHeight extends BaseActivity
    implements com.shamim.landmeasurement.history.HistoryItemSupport {

  private LandResultManager resultManager;
  private LandSingleEditTextCardView baseInput;
  private LandSingleEditTextCardView heightInput;
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
    setContentView(R.layout.activity_triangular_land_height);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getString(R.string.item_rectangular_height_based));
    }

    baseInput = findViewById(R.id.base_input);
    heightInput = findViewById(R.id.height_input);
    scrollView = findViewById(R.id.scroll_view);

    baseInput.setTitle(getString(R.string.card_base_title));
    heightInput.setTitle(getString(R.string.card_height_title));

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
    return baseInput.getSerializedState() + ";" + heightInput.getSerializedState();
  }

  /**
   * Restore serialized inputs.
   *
   * @param data the data
   */
  @Override
  public void restoreSerializedInputs(String data) {
    if (data == null || data.isEmpty()) return;
    String[] parts = data.split(";");
    if (parts.length >= 2) {
      getIntent().putExtra("skip_history_save", true);
      baseInput.restoreState(parts[0]);
      heightInput.restoreState(parts[1]);
      calculate();
      getIntent().putExtra("skip_history_save", false);
    }
  }

  /** Calculate. */
  private void calculate() {
    double Base = baseInput.getValueInFeet();
    double Height = heightInput.getValueInFeet();

    if (!baseInput.hasValidInput() || !heightInput.hasValidInput()) {
      Toast.makeText(
              this, getString(R.string.item_triangular_height_input_error), Toast.LENGTH_LONG)
          .show();
      resultManager.hideResult();
      return;
    }

    double areaSqFt = 0.5 * Base * Height;

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

    return getString(R.string.card_base_title)
        + " : "
        + baseInput.getValueAsString()
        + "\n"
        + getString(R.string.card_height_title)
        + " : "
        + heightInput.getValueAsString();
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
