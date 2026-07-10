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

public class LandSingleEditTextCardView extends ConstraintLayout {

  private MaterialTextView tvTitle;
  private TextInputEditText etFirstVal, etFirstIn;
  private TextInputLayout tilFirstVal, tilFirstIn;
  private ChipGroup chipGroupUnit;

  private int selectedUnitResId = R.string.unit_foot; // default

  /**
   * Land single edit text card view.
   *
   * @param context the context
   */
  public LandSingleEditTextCardView(Context context) {
    super(context);
    init(context);
  }

  /**
   * Land single edit text card view.
   *
   * @param context the context
   * @param attrs the attrs
   */
  public LandSingleEditTextCardView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  /**
   * Land single edit text card view.
   *
   * @param context the context
   * @param attrs the attrs
   * @param defStyleAttr the defStyleAttr
   */
  public LandSingleEditTextCardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  /**
   * Init.
   *
   * @param context the context
   */
  private void init(Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_land_card_with_single_edittext, this, true);

    tvTitle = findViewById(R.id.tv_title);
    etFirstVal = findViewById(R.id.et_first_val);
    etFirstIn = findViewById(R.id.et_first_in);
    tilFirstVal = findViewById(R.id.til_first_val);
    tilFirstIn = findViewById(R.id.til_first_in);
    chipGroupUnit = findViewById(R.id.chip_group_unit);

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
              updateHintAndInchField();
            }
          });

      chipGroupUnit.addView(chip);
    }

    updateHintAndInchField();
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

  /** Update hint and inch field. */
  private void updateHintAndInchField() {
    String mainHint;
    boolean showInchField = (selectedUnitResId == R.string.unit_foot);

    if (showInchField) {
      mainHint = getContext().getString(R.string.et_single_feet_hind);
    } else {
      mainHint = getContext().getString(selectedUnitResId);
    }

    tilFirstVal.setHint(mainHint);
    tilFirstIn.setVisibility(showInchField ? View.VISIBLE : View.GONE);

    if (!showInchField) {
      etFirstIn.setText("");
    }
  }

  /**
   * Set title.
   *
   * @param title the title
   */
  public void setTitle(String title) {
    tvTitle.setText(title);
  }

  /** Returns value in currently selected unit */
  public double getValue() {
    double mainValue = parseDoubleOrZero(etFirstVal.getText());
    double inches = parseDoubleOrZero(etFirstIn.getText());

    if (selectedUnitResId == R.string.unit_foot) {
      return mainValue + (inches / 12.0);
    }
    return mainValue;
  }

  /** Returns value converted to Feet */
  public double getValueInFeet() {
    double valueInSelectedUnit = getValue();
    if (valueInSelectedUnit <= 0) return 0.0;

    return UnitConverter.convertLength(valueInSelectedUnit, selectedUnitResId, R.string.unit_foot);
  }

  /**
   * Get value as string.
   *
   * @return the result of the operation
   */
  public String getValueAsString() {
    double mainValue = parseDoubleOrZero(etFirstVal.getText());
    double inches = parseDoubleOrZero(etFirstIn.getText());

    if (selectedUnitResId == R.string.unit_foot) {
      return String.format("%.2f", mainValue)
          + " "
          + getContext().getString(R.string.unit_foot)
          + " "
          + String.format("%.2f", inches)
          + " "
          + getContext().getString(R.string.unit_inch);
    } else {
      return String.format("%.2f", mainValue) + " " + getContext().getString(selectedUnitResId);
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
    setupChips(); // Refresh UI
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
    return getValueInFeet() > 0;
  }

  /**
   * Get serialized state.
   *
   * @return the result of the operation
   */
  public String getSerializedState() {
    String mainVal = etFirstVal.getText() != null ? etFirstVal.getText().toString().trim() : "";
    String inchVal = etFirstIn.getText() != null ? etFirstIn.getText().toString().trim() : "";
    return mainVal + "," + inchVal + "," + selectedUnitResId;
  }

  /**
   * Restore state.
   *
   * @param serialized the serialized
   */
  public void restoreState(String serialized) {
    if (serialized == null || serialized.isEmpty()) return;
    String[] parts = serialized.split(",", -1);
    if (parts.length >= 3) {
      etFirstVal.setText(parts[0]);
      etFirstIn.setText(parts[1]);
      try {
        int unitResId = Integer.parseInt(parts[2]);
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
    setSelectedUnit(R.string.unit_foot);
  }
}
