package com.shamim.landmeasurement.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.shamim.landmeasurement.R;

public class LandSingleEditTextCardView extends ConstraintLayout {

  private MaterialTextView tvTitle;
  private TextInputEditText etFirstVal, etFirstIn;
  private TextInputLayout tilFirstVal, tilFirstIn;
  private MaterialButtonToggleGroup toggleUnit;
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
    toggleUnit = findViewById(R.id.toggle_unit);

    // Default selection
    toggleUnit.check(R.id.btn_ft);

    // Listen to unit changes
    toggleUnit.addOnButtonCheckedListener(
        (group, checkedId, isChecked) -> {
          if (isChecked) {
            updateHintAndInchField(checkedId);
          }
        });

    // Set initial state
    updateHintAndInchField(toggleUnit.getCheckedButtonId());
  }

  private void updateHintAndInchField(int checkedId) {
    String mainHint;
    boolean showInchField = false;

    if (checkedId == R.id.btn_ft) {
      mainHint = viewContext.getString(R.string.et_single_feet_hind);
      showInchField = true;
    } else if (checkedId == R.id.btn_inch) {
      mainHint = viewContext.getString(R.string.et_single_inch_hind);
      showInchField = false;
    } else if (checkedId == R.id.btn_hat) {
      mainHint = viewContext.getString(R.string.et_single_hat_hind);
      showInchField = false;
    } else if (checkedId == R.id.btn_meter) {
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
    int unit = toggleUnit.getCheckedButtonId();

    double mainValue = parseDoubleOrZero(etFirstVal.getText());
    double inches = parseDoubleOrZero(etFirstIn.getText());

    double totalFeet = mainValue;

    // If feet is selected → add inches
    if (unit == R.id.btn_ft) {
      totalFeet += (inches / 12.0);
    }
    // Convert other units to feet
    else if (unit == R.id.btn_hat) {
      totalFeet = mainValue * 1.5;
    } else if (unit == R.id.btn_inch) {
      totalFeet = mainValue / 12.0;
    } else if (unit == R.id.btn_meter) {
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
    toggleUnit.check(R.id.btn_ft);
  }
}
