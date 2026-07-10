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
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.SideCalculationManager;
import com.shamim.landmeasurement.view.AreaInputCardView;
import com.shamim.landmeasurement.view.LandSingleEditTextCardView;

public class RectangleSideCalculatorActivity extends BaseActivity
    implements com.shamim.landmeasurement.history.HistoryItemSupport {

  private MaterialToolbar toolbar;
  private AreaInputCardView areaInputCard;
  private LandSingleEditTextCardView widthInput;
  private ScrollView scrollView;

  private SideCalculationManager manager;

  /**
   * On create.
   *
   * @param savedInstanceState the savedInstanceState
   */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_base_side);

    manager = new SideCalculationManager(this);

    findViews();
    setupTitle();
    setupToolbar();
    setupListeners();

    String serialized = getIntent().getStringExtra("serialized_inputs");
    if (serialized != null) {
      restoreSerializedInputs(serialized);
    }
  }

  /** Find views. */
  private void findViews() {
    toolbar = findViewById(R.id.toolbar);
    scrollView = findViewById(R.id.scroll_view);

    areaInputCard = findViewById(R.id.area_input_card);
    widthInput = findViewById(R.id.width_input);

    View resultSection = findViewById(R.id.result_section);
    manager.setupResultSection(resultSection);
  }

  /** Setup title. */
  private void setupTitle() {
    widthInput.setTitle(getString(R.string.first_arm_title));
    manager.setTitle(getString(R.string.other_side_title));
  }

  /** Setup toolbar. */
  private void setupToolbar() {
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(R.string.item_quadrilateral);
    }
  }

  /** Setup listeners. */
  private void setupListeners() {

    manager.setCalculateClickListener(this::calculate);
    manager.setShareClickListener(this::showShareOptionsDialog);
  }

  /** Calculate. */
  private void calculate() {
    double area = areaInputCard.getAreaValue();
    double oneSide = widthInput.getValueInFeet();

    if (area <= 0 || oneSide <= 0) {
      Toast.makeText(this, R.string.error_invalid_conversion_land_amount, Toast.LENGTH_SHORT)
          .show();
      manager.hideResult();
      return;
    }

    int selectedAreaUnitResId = areaInputCard.getSelectedUnitResId();

    double areaFactor = manager.getAreaInSqFt(area, selectedAreaUnitResId);

    if (areaFactor <= 0) {
      manager.hideResult();
      return;
    }

    double otherSideInFeet = areaFactor / oneSide;

    manager.showResult(otherSideInFeet);

    // Save to history
    saveToHistory(areaFactor);

    // Share text তৈরি
    String shareText = manager.buildShareableText(sharedTextHeadingBuilder(), otherSideInFeet);

    manager.setLastSharedText(shareText);
  }

  /**
   * Save to history.
   *
   * @param areaSqFt the areaSqFt
   */
  private void saveToHistory(double areaSqFt) {
    if (getIntent().getBooleanExtra("skip_history_save", false)) {
      return;
    }
    String shapeTitle =
        getString(R.string.header_side_calculation)
            + " ("
            + getString(R.string.item_quadrilateral)
            + " - "
            + getString(R.string.item_rectangular)
            + ")";

    String inputs =
        getString(R.string.amount_of_land_title)
            + ": "
            + areaInputCard.getAreaValueAsString()
            + "\n"
            + getString(R.string.first_arm_title)
            + ": "
            + widthInput.getValueAsString();

    String className = getClass().getName();
    String serialized = getSerializedInputs();

    new Thread(
            () -> {
              try {
                com.shamim.landmeasurement.history.HistoryEntry entry =
                    new com.shamim.landmeasurement.history.HistoryEntry(
                        shapeTitle,
                        inputs,
                        areaSqFt,
                        System.currentTimeMillis(),
                        className,
                        serialized);
                com.shamim.landmeasurement.history.HistoryDatabase.getDatabase(this)
                    .historyDao()
                    .insert(entry);
              } catch (Exception e) {
                android.util.Log.e("RectangleSideCalculator", "Error saving history", e);
              }
            })
        .start();
  }

  /**
   * Get serialized inputs.
   *
   * @return the result of the operation
   */
  @Override
  public String getSerializedInputs() {
    return areaInputCard.getAreaValue()
        + ";"
        + areaInputCard.getSelectedUnitResId()
        + ";"
        + widthInput.getSerializedState();
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
      try {
        double areaVal = Double.parseDouble(parts[0]);
        int unitResId = Integer.parseInt(parts[1]);
        areaInputCard.setAreaValue(areaVal);
        areaInputCard.setSelectedUnit(unitResId);
      } catch (NumberFormatException e) {
        e.printStackTrace();
      }
      widthInput.restoreState(parts[2]);
      calculate();
      getIntent().putExtra("skip_history_save", false);
    }
  }

  /**
   * Shared text heading builder.
   *
   * @return the result of the operation
   */
  private StringBuilder sharedTextHeadingBuilder() {

    StringBuilder sb = new StringBuilder();
    sb.append(getString(R.string.amount_of_land_title))
        .append(" : ")
        .append(areaInputCard.getAreaValueAsString())
        .append("\n");
    sb.append(getString(R.string.first_arm_title))
        .append(" : ")
        .append(widthInput.getValueAsString())
        .append("\n\n");
    sb.append("──────────────────────────────\n\n");
    sb.append(getString(R.string.other_side_title)).append(" :\n\n");

    return sb;
  }

  /** Show share options dialog. */
  private void showShareOptionsDialog() {
    manager.showShareOptionsDialog();
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
