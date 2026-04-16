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

  public LandSingleEditTextCardView(Context context) {
    super(context);
    init(context);
  }

  public LandSingleEditTextCardView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public LandSingleEditTextCardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

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

  private void updateChipIcons() {
    for (int i = 0; i < chipGroupUnit.getChildCount(); i++) {
      Chip chip = (Chip) chipGroupUnit.getChildAt(i);
      boolean isSelected =
          chip.getText().toString().equals(getContext().getString(selectedUnitResId));
      chip.setChipIconVisible(isSelected);
    }
  }

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

  public String getValueAsString() {
    double mainValue = parseDoubleOrZero(etFirstVal.getText());
    double inches = parseDoubleOrZero(etFirstIn.getText());

    if (selectedUnitResId == R.string.unit_foot) {
      return mainValue
          + " "
          + getContext().getString(R.string.unit_foot)
          + " "
          + inches
          + " "
          + getContext().getString(R.string.unit_inch);
    } else {
      return mainValue + " " + getContext().getString(selectedUnitResId);
    }
  }

  public String getSelectedUnit() {
    return getContext().getString(selectedUnitResId);
  }

  public int getSelectedUnitResId() {
    return selectedUnitResId;
  }

  public void setSelectedUnit(int unitResId) {
    this.selectedUnitResId = unitResId;
    setupChips(); // Refresh UI
  }

  private double parseDoubleOrZero(CharSequence s) {
    if (s == null || s.toString().trim().isEmpty()) return 0.0;
    try {
      return Double.parseDouble(s.toString().trim());
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  public boolean hasValidInput() {
    return getValueInFeet() > 0;
  }

  public void clear() {
    etFirstVal.setText("");
    etFirstIn.setText("");
    setSelectedUnit(R.string.unit_foot);
  }
}
