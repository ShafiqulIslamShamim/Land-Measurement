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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.UnitConverter;
import java.util.Arrays;
import java.util.List;

public class LandTwoEditTextCardView extends ConstraintLayout {

  private MaterialTextView tvTitle;
  private TextInputEditText etFirstVal, etFirstIn, etSecondVal, etSecondIn;
  private TextInputLayout tilFirstVal, tilFirstIn, tilSecondVal, tilSecondIn;
  private ChipGroup chipGroupUnit;

  private String typeText;
  private int selectedUnitResId = R.string.unit_foot; // default

  /**
   * Land two edit text card view.
   *
   * @param context the context
   */
  public LandTwoEditTextCardView(Context context) {
    super(context);
    init(context);
  }

  /**
   * Land two edit text card view.
   *
   * @param context the context
   * @param attrs the attrs
   */
  public LandTwoEditTextCardView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  /**
   * Land two edit text card view.
   *
   * @param context the context
   * @param attrs the attrs
   * @param defStyleAttr the defStyleAttr
   */
  public LandTwoEditTextCardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  /**
   * Init.
   *
   * @param context the context
   */
  private void init(Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_land_card_with_two_edittext, this, true);

    tvTitle = findViewById(R.id.tv_title);

    etFirstVal = findViewById(R.id.et_first_val);
    etFirstIn = findViewById(R.id.et_first_in);
    etSecondVal = findViewById(R.id.et_second_val);
    etSecondIn = findViewById(R.id.et_second_in);

    tilFirstVal = findViewById(R.id.til_first_val);
    tilFirstIn = findViewById(R.id.til_first_in);
    tilSecondVal = findViewById(R.id.til_second_val);
    tilSecondIn = findViewById(R.id.til_second_in);

    chipGroupUnit = findViewById(R.id.chip_group_unit);

    typeText = context.getString(R.string.card_length_title);

    setupChips();
  }

  /** Setup chips. */
  private void setupChips() {
    List<Integer> unitResIds =
        Arrays.asList(
            R.string.unit_inch,
            R.string.unit_foot,
            R.string.unit_yard,
            R.string.unit_hath,
            R.string.unit_meter,
            R.string.unit_centimeter,
            R.string.unit_millimeter);

    chipGroupUnit.removeAllViews();

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
              updateChipIcons();
              updateHintsAndInchFields();
            }
          });

      chipGroupUnit.addView(chip);
    }

    updateHintsAndInchFields();
  }

  /** Update chip icons. */
  private void updateChipIcons() {
    for (int i = 0; i < chipGroupUnit.getChildCount(); i++) {
      Chip chip = (Chip) chipGroupUnit.getChildAt(i);
      boolean isSelected =
          chip.getText().toString().equals(getContext().getString(selectedUnitResId));
      chip.setChipIconVisible(isSelected);
    }
  }

  /** Update hints and inch fields. */
  private void updateHintsAndInchFields() {
    boolean isFeet = (selectedUnitResId == R.string.unit_foot);
    String unit = getContext().getString(selectedUnitResId);

    String format1 = getContext().getString(R.string.et_double_first_hind);
    String format2 = getContext().getString(R.string.et_double_second_hind);

    tilFirstVal.setHint(String.format(format1, typeText, unit));
    tilSecondVal.setHint(String.format(format2, typeText, unit));

    tilFirstIn.setVisibility(isFeet ? View.VISIBLE : View.GONE);
    tilSecondIn.setVisibility(isFeet ? View.VISIBLE : View.GONE);

    if (!isFeet) {
      etFirstIn.setText("");
      etSecondIn.setText("");
    }
  }

  /**
   * Set title.
   *
   * @param title the title
   */
  public void setTitle(String title) {
    tvTitle.setText(title);
    typeText = title;
    updateHintsAndInchFields();
  }

  // ================== Value Getters ==================

  /** Returns first value in currently selected unit */
  public double getFirstValue() {
    double main = parseDoubleOrZero(etFirstVal.getText());
    double inch = parseDoubleOrZero(etFirstIn.getText());

    if (selectedUnitResId == R.string.unit_foot) {
      return main + (inch / 12.0);
    }
    return main;
  }

  /** Returns second value in currently selected unit */
  public double getSecondValue() {
    double main = parseDoubleOrZero(etSecondVal.getText());
    double inch = parseDoubleOrZero(etSecondIn.getText());

    if (selectedUnitResId == R.string.unit_foot) {
      return main + (inch / 12.0);
    }
    return main;
  }

  /** ✅ Returns first value converted to Feet */
  public double getFirstValueInFeet() {
    double value = getFirstValue();
    if (value <= 0) return 0.0;

    return UnitConverter.convertLength(value, selectedUnitResId, R.string.unit_foot);
  }

  /** ✅ Returns second value converted to Feet */
  public double getSecondValueInFeet() {
    double value = getSecondValue();
    if (value <= 0) return 0.0;

    return UnitConverter.convertLength(value, selectedUnitResId, R.string.unit_foot);
  }

  /** First value as readable string */
  public String getFirstValueAsString() {
    double main = parseDoubleOrZero(etFirstVal.getText());
    double inch = parseDoubleOrZero(etFirstIn.getText());

    if (selectedUnitResId == R.string.unit_foot) {
      return String.format("%.2f", main)
          + " "
          + getContext().getString(R.string.unit_foot)
          + " "
          + String.format("%.2f", inch)
          + " "
          + getContext().getString(R.string.unit_inch);
    } else {
      return String.format("%.2f", main) + " " + getContext().getString(selectedUnitResId);
    }
  }

  /** Second value as readable string */
  public String getSecondValueAsString() {
    double main = parseDoubleOrZero(etSecondVal.getText());
    double inch = parseDoubleOrZero(etSecondIn.getText());

    if (selectedUnitResId == R.string.unit_foot) {
      return String.format("%.2f", main)
          + " "
          + getContext().getString(R.string.unit_foot)
          + " "
          + String.format("%.2f", inch)
          + " "
          + getContext().getString(R.string.unit_inch);
    } else {
      return String.format("%.2f", main) + " " + getContext().getString(selectedUnitResId);
    }
  }

  /**
   * Get selected unit.
   *
   * @return the result of the operation
   */
  public String getSelectedUnit() {
    return getContext().getString(selectedUnitResId);
  }

  /**
   * Get selected unit res id.
   *
   * @return the result of the operation
   */
  public int getSelectedUnitResId() {
    return selectedUnitResId;
  }

  /**
   * Set selected unit.
   *
   * @param unitResId the unitResId
   */
  public void setSelectedUnit(int unitResId) {
    this.selectedUnitResId = unitResId;
    setupChips();
  }

  /**
   * Parse double or zero.
   *
   * @param s the s
   * @return the result of the operation
   */
  private double parseDoubleOrZero(CharSequence s) {
    if (s == null || s.toString().trim().isEmpty()) return 0.0;
    try {
      return Double.parseDouble(s.toString().trim());
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  /**
   * Has valid input.
   *
   * @return the result of the operation
   */
  public boolean hasValidInput() {
    return getFirstValueInFeet() > 0 && getSecondValueInFeet() > 0;
  }

  /**
   * Has valid input.
   *
   * @param input the input
   * @return the result of the operation
   */
  public boolean hasValidInput(double input) {
    return input > 0;
  }

  /**
   * Get serialized state.
   *
   * @return the result of the operation
   */
  public String getSerializedState() {
    String firstVal = etFirstVal.getText() != null ? etFirstVal.getText().toString().trim() : "";
    String firstIn = etFirstIn.getText() != null ? etFirstIn.getText().toString().trim() : "";
    String secondVal = etSecondVal.getText() != null ? etSecondVal.getText().toString().trim() : "";
    String secondIn = etSecondIn.getText() != null ? etSecondIn.getText().toString().trim() : "";
    return firstVal + "," + firstIn + "," + secondVal + "," + secondIn + "," + selectedUnitResId;
  }

  /**
   * Restore state.
   *
   * @param serialized the serialized
   */
  public void restoreState(String serialized) {
    if (serialized == null || serialized.isEmpty()) return;
    String[] parts = serialized.split(",", -1);
    if (parts.length >= 5) {
      etFirstVal.setText(parts[0]);
      etFirstIn.setText(parts[1]);
      etSecondVal.setText(parts[2]);
      etSecondIn.setText(parts[3]);
      try {
        int unitResId = Integer.parseInt(parts[4]);
        setSelectedUnit(unitResId);
      } catch (NumberFormatException e) {
        // fallback
      }
    }
  }

  /** Clear. */
  public void clear() {
    etFirstVal.setText("");
    etFirstIn.setText("");
    etSecondVal.setText("");
    etSecondIn.setText("");
    setSelectedUnit(R.string.unit_foot);
  }
}
