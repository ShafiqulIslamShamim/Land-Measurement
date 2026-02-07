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
import com.shamim.landmeasurement.util.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversionActivity extends BaseActivity {

  private LandResultManager resultManager;
  private MaterialToolbar toolbar;

  private TextInputEditText etInputValue;
  private ChipGroup chipGroupUnits;
  private ScrollView scrollView;

  private int selectedUnitResId = R.string.unit_shotok;

  // unit resource id → square feet conversion factor
  private final Map<Integer, Double> conversionFactors = new HashMap<>();

  {
    conversionFactors.put(R.string.unit_sqft, 1.0);
    conversionFactors.put(R.string.unit_sqm, 10.7639);
    conversionFactors.put(R.string.unit_shotok, 435.6);
    conversionFactors.put(R.string.unit_katha, 720.0);
    conversionFactors.put(R.string.unit_bigha, 14400.0);
    conversionFactors.put(R.string.unit_acre, 43560.0);
    conversionFactors.put(R.string.unit_hectare, 107639.0);
    conversionFactors.put(R.string.unit_kora, 435.6 * 2);
    conversionFactors.put(R.string.unit_joistho, 435.6 * 2 * 10);
    conversionFactors.put(R.string.unit_kani, 435.6 * 2 * 80);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_land_conversion);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(R.string.header_conversion);
    }

    findViews();
    setupChipGroup();
    setupListeners();
  }

  private void findViews() {
    etInputValue = findViewById(R.id.et_input_value);
    chipGroupUnits = findViewById(R.id.chip_group_units);
    scrollView = findViewById(R.id.scroll_view);
    resultManager = new LandResultManager(findViewById(android.R.id.content), this);
  }

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

      chip.setText(resId); // uses string resource
      chip.setCheckable(true);
      chip.setChecked(resId == selectedUnitResId);

      // Set the icon (this is your "button")
      chip.setChipIconResource(R.drawable.ic_check); // use any icon you want
      chip.setChipIconVisible(resId == selectedUnitResId); // visible only if selected
      // chip.setChipIconTintResource(R.color.white); // optional

      chip.setOnCheckedChangeListener(
          (buttonView, isChecked) -> {
            if (isChecked) {
              selectedUnitResId = resId;

              // Show icon when selected
              chip.setChipIconVisible(true);

              calculate();
            } else {
              // Hide icon when not selected
              chip.setChipIconVisible(false);
            }
          });

      chipGroupUnits.addView(chip);
    }
  }

  private void setupListeners() {
    etInputValue.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

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

  private void calculate() {
    String inputStr = etInputValue.getText().toString().trim();
    double input = parseDoubleOrZero(inputStr);

    if (input <= 0) {
      Toast.makeText(this, R.string.error_invalid_conversion_land_amount, Toast.LENGTH_SHORT)
          .show();
      resultManager.hideResult();
      return;
    }

    Double factor = conversionFactors.get(selectedUnitResId);
    if (factor == null) {
      Toast.makeText(this, R.string.error_invalid_conversion_land_unit, Toast.LENGTH_SHORT).show();
      resultManager.hideResult();
      return;
    }

    double sqFt = input * factor;

    resultManager.showResult(sqFt);

    // Scroll to bottom
    scrollView.postDelayed(
        () -> {
          int bottom = scrollView.getChildAt(0).getBottom();
          scrollView.smoothScrollTo(0, bottom);
        },
        150);
  }

  private double parseDoubleOrZero(String s) {
    if (s == null || s.isEmpty()) return 0.0;
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
