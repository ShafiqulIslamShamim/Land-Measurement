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

public class LandTwoEditTextCardView extends ConstraintLayout {

  private MaterialTextView tvTitle;
  private TextInputEditText etFirstVal, etFirstIn, etSecondVal, etSecondIn;
  private TextInputLayout tilFirstVal, tilFirstIn, tilSecondVal, tilSecondIn;
  private ChipGroup chipGroupUnit;
  private Chip chipFt, chipInch, chipHat, chipMeter;
  private String typeText; // default
  private Context viewContext;

  public LandTwoEditTextCardView(Context context) {
    super(context);
    init(context);
  }

  public LandTwoEditTextCardView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public LandTwoEditTextCardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_land_card_with_two_edittext, this, true);

    viewContext = context;

    typeText = viewContext.getString(R.string.card_length_title);

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
    chipFt = findViewById(R.id.chip_ft);
    chipInch = findViewById(R.id.chip_inch);
    chipHat = findViewById(R.id.chip_hat);
    chipMeter = findViewById(R.id.chip_meter);

    // Default to feet
    chipGroupUnit.check(R.id.chip_ft);

    // Listen to chip selection changes
    chipGroupUnit.setOnCheckedStateChangeListener(
        (group, checkedIds) -> {
          if (!checkedIds.isEmpty()) {
            int checkedId = checkedIds.get(0);
            updateHintsAndInchFields(checkedId);
          }
        });

    // Initial hints
    updateHintsAndInchFields(chipGroupUnit.getCheckedChipId());
  }

  private void updateHintsAndInchFields(int checkedId) {
    boolean showInchFields = (checkedId == R.id.chip_ft);

    String format1 = viewContext.getString(R.string.et_double_first_hind);
    String format2 = viewContext.getString(R.string.et_double_second_hind);

    String unit;

    if (checkedId == R.id.chip_ft) {
      unit = viewContext.getString(R.string.et_double_feet_hind);
    } else if (checkedId == R.id.chip_inch) {
      unit = viewContext.getString(R.string.et_double_inch_hind);
    } else if (checkedId == R.id.chip_hat) {
      unit = viewContext.getString(R.string.et_double_hat_hind);
    } else if (checkedId == R.id.chip_meter) {
      unit = viewContext.getString(R.string.et_double_meter_hind);
    } else {
      unit = viewContext.getString(R.string.et_double_default_hind);
    }

    String mainHint1 = String.format(format1, typeText, unit);
    String mainHint2 = String.format(format2, typeText, unit);

    tilFirstVal.setHint(mainHint1);
    tilSecondVal.setHint(mainHint2);

    tilFirstIn.setVisibility(showInchFields ? View.VISIBLE : View.GONE);
    tilSecondIn.setVisibility(showInchFields ? View.VISIBLE : View.GONE);

    // Clear inch fields when hidden
    if (!showInchFields) {
      etFirstIn.setText("");
      etSecondIn.setText("");
    }
  }

  public void setTitle(String title) {
    tvTitle.setText(title);
    typeText = title;

    // Refresh hints when title changes
    updateHintsAndInchFields(chipGroupUnit.getCheckedChipId());
  }

  public double getAverageInFeet() {
    int checkedId = chipGroupUnit.getCheckedChipId();

    double val1 = parseDoubleOrZero(etFirstVal.getText());
    double in1 = parseDoubleOrZero(etFirstIn.getText());
    double val2 = parseDoubleOrZero(etSecondVal.getText());
    double in2 = parseDoubleOrZero(etSecondIn.getText());

    double total1 = val1;
    double total2 = val2;

    // Add inches only if feet is selected
    if (checkedId == R.id.chip_ft) {
      total1 += (in1 / 12.0);
      total2 += (in2 / 12.0);
    }
    // Convert other units to feet
    else if (checkedId == R.id.chip_hat) {
      total1 *= 1.5;
      total2 *= 1.5;
    } else if (checkedId == R.id.chip_inch) {
      total1 /= 12.0;
      total2 /= 12.0;
    } else if (checkedId == R.id.chip_meter) {
      total1 *= 3.28084;
      total2 *= 3.28084;
    }

    if (total1 == 0 || total2 == 0) {
      return total1 + total2; // if one is zero, return the other
    }

    return (total1 + total2) / 2.0;
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
    return getAverageInFeet() > 0;
  }

  public void clear() {
    etFirstVal.setText("");
    etFirstIn.setText("");
    etSecondVal.setText("");
    etSecondIn.setText("");
    chipGroupUnit.check(R.id.chip_ft);
  }
}
