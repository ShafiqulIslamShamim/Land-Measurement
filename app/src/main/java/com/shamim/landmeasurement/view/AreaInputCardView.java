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
package com.shamim.landmeasurement.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.UnitConverter;
import java.util.Arrays;
import java.util.List;

public class AreaInputCardView extends ConstraintLayout {

  private TextInputEditText etAreaInput;
  private ChipGroup chipGroupUnits;

  private int selectedUnitResId = R.string.unit_shotok;

  /**
   * Area input card view.
   *
   * @param context the context
   */
  public AreaInputCardView(Context context) {
    this(context, null);
  }

  /**
   * Area input card view.
   *
   * @param context the context
   * @param attrs the attrs
   */
  public AreaInputCardView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  /**
   * Area input card view.
   *
   * @param context the context
   * @param attrs the attrs
   * @param defStyleAttr the defStyleAttr
   */
  public AreaInputCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  /**
   * Init.
   *
   * @param context the context
   */
  private void init(Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_area_input_card, this, true);

    etAreaInput = findViewById(R.id.et_area_input);
    chipGroupUnits = findViewById(R.id.chip_group_area_units);

    setupUnits();
    setupTextWatcher();
  }

  /** Setup text watcher. */
  private void setupTextWatcher() {
    etAreaInput.addTextChangedListener(
        new TextWatcher() {
          /**
           * After text changed.
           *
           * @param s the s
           */
          @Override
          public void afterTextChanged(Editable s) {
            String filtered = s.toString().replaceAll("[^0-9.]", "");
            if (!filtered.equals(s.toString())) {
              etAreaInput.setText(filtered);
              etAreaInput.setSelection(filtered.length());
            }
          }

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
        });
  }

  /** Setup units. */
  public void setupUnits() {
    List<Integer> unitResIds =
        Arrays.asList(
            R.string.unit_shotok,
            R.string.unit_katha,
            R.string.unit_bigha,
            R.string.unit_sqft,
            R.string.unit_sqm,
            R.string.unit_acre,
            R.string.unit_hectare,
            R.string.unit_kora,
            R.string.unit_joistho,
            R.string.unit_kani);

    chipGroupUnits.removeAllViews();

    for (int resId : unitResIds) {
      Chip chip = new Chip(getContext());
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
            } else {
              chip.setChipIconVisible(false);
            }
          });

      chipGroupUnits.addView(chip);
    }
  }

  // ================== Public Methods ==================

  /** Returns raw input value in currently selected unit */
  public double getAreaValue() {
    String text = etAreaInput.getText() != null ? etAreaInput.getText().toString().trim() : "";
    try {
      return Double.parseDouble(text);
    } catch (Exception e) {
      return 0.0;
    }
  }

  /** Returns area converted to Square Feet */
  public double getAreaInSqFt() {
    double value = getAreaValue();
    if (value <= 0) return 0.0;
    return UnitConverter.convertArea(value, selectedUnitResId, R.string.unit_sqft);
  }

  /**
   * Get area value as string.
   *
   * @return the result of the operation
   */
  public String getAreaValueAsString() {
    return String.format("%.2f", getAreaValue()) + " " + getContext().getString(selectedUnitResId);
  }

  /**
   * Get selected unit res id.
   *
   * @return the result of the operation
   */
  public int getSelectedUnitResId() {
    return selectedUnitResId;
  }

  /** Change selected unit programmatically */
  public void setSelectedUnit(int unitResId) {
    this.selectedUnitResId = unitResId;
    // Refresh UI
    for (int i = 0; i < chipGroupUnits.getChildCount(); i++) {
      Chip chip = (Chip) chipGroupUnits.getChildAt(i);
      boolean isSelected = chip.getText().toString().equals(getContext().getString(unitResId));
      chip.setChecked(isSelected);
      chip.setChipIconVisible(isSelected);
    }
  }

  /** Clear. */
  public void clear() {
    etAreaInput.setText("");
  }

  /** Optional: Set input value programmatically */
  public void setAreaValue(double value) {
    etAreaInput.setText(value > 0 ? String.valueOf(value) : "");
  }
}
