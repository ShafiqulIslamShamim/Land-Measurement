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

public class TriangularLandActivityArm extends BaseActivity
    implements com.shamim.landmeasurement.history.HistoryItemSupport {

  private LandResultManager resultManager;
  private LandSingleEditTextCardView FirstArmInput;
  private LandSingleEditTextCardView SecondArmInput;
  private LandSingleEditTextCardView ThirdArmInput;
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
    setContentView(R.layout.activity_triangular_land_arm);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getString(R.string.item_rectangular_arm_based));
    }

    FirstArmInput = findViewById(R.id.first_arm_input);
    SecondArmInput = findViewById(R.id.second_arm_input);
    ThirdArmInput = findViewById(R.id.third_arm_input);
    scrollView = findViewById(R.id.scroll_view);

    FirstArmInput.setTitle(getString(R.string.first_arm_title));
    SecondArmInput.setTitle(getString(R.string.second_arm_title));
    ThirdArmInput.setTitle(getString(R.string.third_arm_title));

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
    return FirstArmInput.getSerializedState()
        + ";"
        + SecondArmInput.getSerializedState()
        + ";"
        + ThirdArmInput.getSerializedState();
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
    if (parts.length >= 3) {
      getIntent().putExtra("skip_history_save", true);
      FirstArmInput.restoreState(parts[0]);
      SecondArmInput.restoreState(parts[1]);
      ThirdArmInput.restoreState(parts[2]);
      calculate();
      getIntent().putExtra("skip_history_save", false);
    }
  }

  /** Calculate. */
  private void calculate() {
    double FirstArm = FirstArmInput.getValueInFeet();
    double SecondArm = SecondArmInput.getValueInFeet();
    double ThirdArm = ThirdArmInput.getValueInFeet();

    if (!FirstArmInput.hasValidInput()
        || !SecondArmInput.hasValidInput()
        || !ThirdArmInput.hasValidInput()) {
      Toast.makeText(this, getString(R.string.item_triangular_arm_input_error), Toast.LENGTH_LONG)
          .show();
      resultManager.hideResult();
      return;
    }

    // অর্ধ-পরিসীমা হিসাব
    double s = (FirstArm + SecondArm + ThirdArm) / 2.0;

    double areaSqFt = Math.sqrt(s * (s - FirstArm) * (s - SecondArm) * (s - ThirdArm));

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

    return getString(R.string.first_arm_title)
        + " : "
        + FirstArmInput.getValueAsString()
        + "\n"
        + getString(R.string.second_arm_title)
        + " : "
        + SecondArmInput.getValueAsString()
        + "\n"
        + getString(R.string.third_arm_title)
        + " : "
        + ThirdArmInput.getValueAsString();
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
