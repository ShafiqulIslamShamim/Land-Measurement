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

public class LandSingleEditTextCardView extends ConstraintLayout {

  private MaterialTextView tvTitle;
  private TextInputEditText etFirstVal, etFirstIn;
  private TextInputLayout tilFirstVal, tilFirstIn;
  private ChipGroup chipGroupUnit;
  private Chip chipFt, chipInch, chipHat, chipMeter;
  private Context viewContext;

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

    viewContext = context;

    tvTitle = findViewById(R.id.tv_title);
    etFirstVal = findViewById(R.id.et_first_val);
    etFirstIn = findViewById(R.id.et_first_in);
    tilFirstVal = findViewById(R.id.til_first_val);
    tilFirstIn = findViewById(R.id.til_first_in);

    chipGroupUnit = findViewById(R.id.chip_group_unit);
    chipFt = findViewById(R.id.chip_ft);
    chipInch = findViewById(R.id.chip_inch);
    chipHat = findViewById(R.id.chip_hat);
    chipMeter = findViewById(R.id.chip_meter);

    // Default selection
    chipGroupUnit.check(R.id.chip_ft);

    // Listen to chip selection changes
    chipGroupUnit.setOnCheckedStateChangeListener(
        (group, checkedIds) -> {
          if (!checkedIds.isEmpty()) {
            int checkedId = checkedIds.get(0);
            updateHintAndInchField(checkedId);
          }
        });

    // Set initial state
    updateHintAndInchField(chipGroupUnit.getCheckedChipId());
  }

  private void updateHintAndInchField(int checkedId) {
    String mainHint;
    boolean showInchField = false;

    if (checkedId == R.id.chip_ft) {
      mainHint = viewContext.getString(R.string.et_single_feet_hind);
      showInchField = true;
    } else if (checkedId == R.id.chip_inch) {
      mainHint = viewContext.getString(R.string.et_single_inch_hind);
      showInchField = false;
    } else if (checkedId == R.id.chip_hat) {
      mainHint = viewContext.getString(R.string.et_single_hat_hind);
      showInchField = false;
    } else if (checkedId == R.id.chip_meter) {
      mainHint = viewContext.getString(R.string.et_single_meter_hind);
      showInchField = false;
    } else {
      mainHint = viewContext.getString(R.string.et_single_default_hind);
      showInchField = false;
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

  public double getValueInFeet() {
    int checkedId = chipGroupUnit.getCheckedChipId();

    double mainValue = parseDoubleOrZero(etFirstVal.getText());
    double inches = parseDoubleOrZero(etFirstIn.getText());

    double totalFeet = mainValue;

    // If feet is selected → add inches
    if (checkedId == R.id.chip_ft) {
      totalFeet += (inches / 12.0);
    }
    // Convert other units to feet
    else if (checkedId == R.id.chip_hat) {
      totalFeet = mainValue * 1.5;
    } else if (checkedId == R.id.chip_inch) {
      totalFeet = mainValue / 12.0;
    } else if (checkedId == R.id.chip_meter) {
      totalFeet = mainValue * 3.28084;
    }

    return totalFeet;
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
    chipGroupUnit.check(R.id.chip_ft);
  }
}
