package com.shamim.landmeasurement.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.shamim.landmeasurement.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinearUnitsConversionActivity extends BaseActivity {

  private MaterialToolbar toolbar;
  private TextInputEditText etInputLength;
  private ChipGroup chipGroupUnits;
  private ScrollView scrollView;
  private MaterialButton btnCalculate;
  private MaterialCardView cardResult;
  private ViewGroup containerLengthUnits;

  private int selectedUnitResId = R.string.unit_foot;

  // unit resource id → meter conversion factor
  private final Map<Integer, Double> conversionFactorsToMeter = new HashMap<>();

  {
    conversionFactorsToMeter.put(R.string.unit_inch, 12.0); // 1 foot = 12.0 inch
    conversionFactorsToMeter.put(R.string.unit_foot, 1.0); // 1 foot = 1.0 ft
    conversionFactorsToMeter.put(R.string.unit_yard, 0.333333); // 1 foot = 0.333333 yard
    conversionFactorsToMeter.put(
        R.string.unit_hath, 0.6666666666666666666); // 1 foot = 0.6666666666666666666
    conversionFactorsToMeter.put(R.string.unit_meter, 0.3048);
    conversionFactorsToMeter.put(R.string.unit_centimeter, 30.48);
    conversionFactorsToMeter.put(R.string.unit_millimeter, 304.8);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_linear_units_conversion);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(R.string.item_cov_linear_units);
    }

    etInputLength = findViewById(R.id.et_input_length);
    chipGroupUnits = findViewById(R.id.chip_group_units);
    scrollView = findViewById(R.id.scroll_view);
    btnCalculate = findViewById(R.id.btn_calculate);
    cardResult = findViewById(R.id.card_result);
    containerLengthUnits = findViewById(R.id.container_length_units);

    setupChipGroup();
    setupListeners();
  }

  private void setupChipGroup() {
    List<Integer> unitResIds =
        Arrays.asList(
            R.string.unit_inch,
            R.string.unit_foot,
            R.string.unit_yard,
            R.string.unit_hath,
            R.string.unit_meter,
            R.string.unit_centimeter,
            R.string.unit_millimeter);

    for (int resId : unitResIds) {
      Chip chip = new Chip(this);
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

  private void setupListeners() {
    etInputLength.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          @Override
          public void afterTextChanged(Editable s) {
            String filtered = s.toString().replaceAll("[^0-9.]", "");
            if (!filtered.equals(s.toString())) {
              etInputLength.setText(filtered);
              etInputLength.setSelection(filtered.length());
            }
          }
        });

    btnCalculate.setOnClickListener(v -> calculate());
  }

  private void calculate() {
    String inputStr = etInputLength.getText().toString().trim();
    double input = parseDoubleOrZero(inputStr);

    if (input <= 0) {
      Toast.makeText(this, R.string.conversation_length_error_invalid_value, Toast.LENGTH_SHORT)
          .show();
      hideResult();
      return;
    }

    Double factor = conversionFactorsToMeter.get(selectedUnitResId);
    if (factor == null) {
      Toast.makeText(this, R.string.conversation_length_error_unit_not_found, Toast.LENGTH_SHORT)
          .show();
      hideResult();
      return;
    }

    double valueInMeters = input * factor;
    showResult(valueInMeters, getString(selectedUnitResId));

    // Auto scroll to result
    scrollView.postDelayed(
        () -> {
          int bottom = scrollView.getChildAt(0).getBottom();
          scrollView.smoothScrollTo(0, bottom);
        },
        150);
  }

  private void showResult(double feets, String selectedUnit) {
    containerLengthUnits.removeAllViews();

    List<UnitValue> units = new ArrayList<>();
    units.add(new UnitValue(getString(R.string.unit_inch), feets * 12.0));
    units.add(new UnitValue(getString(R.string.unit_foot), feets));
    units.add(new UnitValue(getString(R.string.unit_yard), feets * 0.333333));
    units.add(new UnitValue(getString(R.string.unit_hath), feets * 0.6666666666666666666));
    units.add(new UnitValue(getString(R.string.unit_meter), feets * 0.3048));
    units.add(new UnitValue(getString(R.string.unit_centimeter), feets * 30.48));
    units.add(new UnitValue(getString(R.string.unit_millimeter), feets * 304.8));

    for (UnitValue uv : units) {
      if (!uv.unit.equals(selectedUnit)) {
        String formatted = formatValue(uv.value);
        addUnitRow(containerLengthUnits, uv.unit, formatted);
      }
    }

    cardResult.setVisibility(View.VISIBLE);
  }

  private String formatValue(double value) {
    if (value >= 10000 || value < 0.001) {
      return String.format("%.4f", value);
    } else if (value >= 100) {
      return String.format("%.2f", value);
    } else {
      return String.format("%.3f", value);
    }
  }

  private void hideResult() {
    cardResult.setVisibility(View.GONE);
  }

  private void addUnitRow(ViewGroup parent, String label, String value) {
    View row = LayoutInflater.from(this).inflate(R.layout.item_unit_row, parent, false);
    MaterialTextView tvLabel = row.findViewById(R.id.tv_unit_name);
    MaterialTextView tvValue = row.findViewById(R.id.tv_unit_value);
    tvLabel.setText(label);
    tvValue.setText(value);
    parent.addView(row);
  }

  private double parseDoubleOrZero(String s) {
    if (s == null || s.isEmpty()) return 0.0;
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  private static class UnitValue {
    String unit;
    double value;

    UnitValue(String unit, double value) {
      this.unit = unit;
      this.value = value;
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
