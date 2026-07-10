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
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.LandResultManager;
import com.shamim.landmeasurement.util.UnitConverter;
import java.util.Arrays;
import java.util.List;

public class AreaUnitsConversionActivity extends BaseActivity
    implements com.shamim.landmeasurement.history.HistoryItemSupport {

  private LandResultManager resultManager;
  private MaterialToolbar toolbar;

  private TextInputEditText etInputValue;
  private ChipGroup chipGroupUnits;
  private ScrollView scrollView;

  private int selectedUnitResId = R.string.unit_shotok;

  /**
   * On create.
   *
   * @param savedInstanceState the savedInstanceState
   */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_area_units_conversion);

    initViews();
    setupToolbar();
    setupChipGroup();
    setupListeners();

    String serialized = getIntent().getStringExtra("serialized_inputs");
    if (serialized != null) {
      restoreSerializedInputs(serialized);
    }
  }

  /** Init views. */
  private void initViews() {
    toolbar = findViewById(R.id.toolbar);
    etInputValue = findViewById(R.id.et_input_value);
    chipGroupUnits = findViewById(R.id.chip_group_units);
    scrollView = findViewById(R.id.scroll_view);

    // Initialize LandResultManager
    resultManager = new LandResultManager(findViewById(android.R.id.content), this);
  }

  /** Setup toolbar. */
  private void setupToolbar() {
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(R.string.item_cov_area_units);
    }
  }

  /** Setup chip group. */
  private void setupChipGroup() {
    List<Integer> unitResIds =
        Arrays.asList(
            R.string.unit_sqft,
            R.string.unit_sqm,
            R.string.unit_shotok,
            R.string.unit_katha,
            R.string.unit_bigha,
            R.string.unit_acre,
            R.string.unit_hectare,
            R.string.unit_kora,
            R.string.unit_joistho,
            R.string.unit_kani);

    for (int resId : unitResIds) {
      Chip chip = new Chip(this);
      chip.setId(resId);
      chip.setText(resId);
      chip.setCheckable(true);
      chip.setChecked(resId == selectedUnitResId);
      chip.setChipIconResource(R.drawable.ic_check);
      chip.setChipIconVisible(resId == selectedUnitResId);

      chip.setOnCheckedChangeListener(
          (buttonView, isChecked) -> {
            if (isChecked) {
              selectedUnitResId = resId;
              chip.setChipIconVisible(true);
              calculate();
            } else {
              chip.setChipIconVisible(false);
            }
          });

      chipGroupUnits.addView(chip);
    }
  }

  /** Setup listeners. */
  private void setupListeners() {
    etInputValue.addTextChangedListener(
        new TextWatcher() {
          /**
           * Before text changed.
           *
           * @param s the s
           * @param start the start
           * @param count the count
           * @param after the after
           */
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          /**
           * On text changed.
           *
           * @param s the s
           * @param start the start
           * @param before the before
           * @param count the count
           */
          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          /**
           * After text changed.
           *
           * @param s the s
           */
          @Override
          public void afterTextChanged(Editable s) {
            String filtered = s.toString().replaceAll("[^0-9.]", "");
            if (!filtered.equals(s.toString())) {
              etInputValue.setText(filtered);
              etInputValue.setSelection(filtered.length());
            }
          }
        });

    resultManager.setOnCalculateClickListener(this::calculate);
  }

  /** Calculate. */
  private void calculate() {
    String inputStr = etInputValue.getText().toString().trim();
    double inputValue = parseDoubleOrZero(inputStr);

    if (inputValue <= 0) {
      Toast.makeText(this, R.string.error_invalid_conversion_land_amount, Toast.LENGTH_SHORT)
          .show();
      resultManager.hideResult();
      return;
    }

    double areaInSqFt =
        UnitConverter.convertArea(inputValue, selectedUnitResId, R.string.unit_sqft);

    resultManager.showResultWithScroll(areaInSqFt, getString(selectedUnitResId), scrollView);

    String shareText = resultManager.buildShareableText(areaInSqFt, sharedTextHeading(inputValue));

    resultManager.setLastSharedText(shareText);
  }

  /**
   * Shared text heading.
   *
   * @param inputValue the inputValue
   * @return the result of the operation
   */
  private String sharedTextHeading(double inputValue) {

    return getString(R.string.amount_of_land_title)
        + " : "
        + inputValue
        + " "
        + getString(selectedUnitResId);
  }

  /**
   * Parse double or zero.
   *
   * @param s the s
   * @return the result of the operation
   */
  private double parseDoubleOrZero(String s) {
    if (s == null || s.isEmpty()) return 0.0;
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      return 0.0;
    }
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

  /**
   * Get serialized inputs.
   *
   * @return the result of the operation
   */
  @Override
  public String getSerializedInputs() {
    return etInputValue.getText().toString() + ";" + selectedUnitResId;
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
      etInputValue.setText(parts[0]);
      try {
        selectedUnitResId = Integer.parseInt(parts[1]);
        chipGroupUnits.check(selectedUnitResId);
      } catch (NumberFormatException e) {
        e.printStackTrace();
      }
      calculate();
      getIntent().putExtra("skip_history_save", false);
    }
  }
}
