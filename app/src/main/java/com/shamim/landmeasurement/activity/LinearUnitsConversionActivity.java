package com.shamim.landmeasurement.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.*;
import java.util.Arrays;
import java.util.List;

public class LinearUnitsConversionActivity extends BaseActivity {

  private MaterialToolbar toolbar;
  private TextInputEditText etInputLength, etInputInch;
  private TextInputLayout tilInputInch;
  private ChipGroup chipGroupUnits;
  private ScrollView scrollView;
  private MaterialButton btnCalculate, btnShare;
  private MaterialCardView cardResult;
  private ViewGroup containerLengthUnits;

  private int selectedUnitResId = R.string.unit_foot;
  private String lastSharedText = "";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_linear_units_conversion);

    initViews();
    setupToolbar();
    setupChipGroup();
    setupListeners();
    updateInchFieldVisibility(); // Initial setup
  }

  private void initViews() {
    toolbar = findViewById(R.id.toolbar);
    etInputLength = findViewById(R.id.et_input_length);
    chipGroupUnits = findViewById(R.id.chip_group_units);
    scrollView = findViewById(R.id.scroll_view);
    btnCalculate = findViewById(R.id.btn_calculate);
    btnShare = findViewById(R.id.btn_share);
    cardResult = findViewById(R.id.card_result);
    containerLengthUnits = findViewById(R.id.container_length_units);

    // Extra views for Inch when Foot is selected
    tilInputInch = findViewById(R.id.til_first_in);
    etInputInch = findViewById(R.id.et_first_in);
  }

  private void setupToolbar() {
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(R.string.item_cov_linear_units);
    }
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

    chipGroupUnits.removeAllViews();

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
              updateChipIcons();
              updateInchFieldVisibility();
              calculate(); // Auto calculate on unit change
            }
          });

      chipGroupUnits.addView(chip);
    }
  }

  private void updateChipIcons() {
    for (int i = 0; i < chipGroupUnits.getChildCount(); i++) {
      Chip chip = (Chip) chipGroupUnits.getChildAt(i);
      boolean isSelected = chip.getText().toString().equals(getString(selectedUnitResId));
      chip.setChipIconVisible(isSelected);
    }
  }

  private void updateInchFieldVisibility() {
    boolean isFoot = (selectedUnitResId == R.string.unit_foot);

    if (tilInputInch != null) {
      tilInputInch.setVisibility(isFoot ? View.VISIBLE : View.GONE);
    }

    if (etInputInch != null && !isFoot) {
      etInputInch.setText("");
    }
  }

  private void setupListeners() {
    // Input filtering
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

    // Optional: Also add listener on inch field
    if (etInputInch != null) {
      etInputInch.addTextChangedListener(
          new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
              String filtered = s.toString().replaceAll("[^0-9.]", "");
              if (!filtered.equals(s.toString())) {
                etInputInch.setText(filtered);
                etInputInch.setSelection(filtered.length());
              }
            }
          });
    }

    btnCalculate.setOnClickListener(v -> calculate());
    btnShare.setOnClickListener(
        v ->
            ShareUtils.showShareOptionsDialog(
                this, lastSharedText, "Linear_units_conversion_result"));
  }

  private void calculate() {
    double mainValue = parseDoubleOrZero(etInputLength.getText());
    double inches = (etInputInch != null) ? parseDoubleOrZero(etInputInch.getText()) : 0.0;

    double valueInSelectedUnit;

    if (selectedUnitResId == R.string.unit_foot) {
      valueInSelectedUnit = mainValue + (inches / 12.0);
    } else {
      valueInSelectedUnit = mainValue;
    }

    if (valueInSelectedUnit <= 0) {
      Toast.makeText(this, R.string.conversation_length_error_invalid_value, Toast.LENGTH_SHORT)
          .show();
      hideResult();
      return;
    }

    // Convert to Feet (base unit)
    double valueInFeet =
        UnitConverter.convertLength(valueInSelectedUnit, selectedUnitResId, R.string.unit_foot);

    showResult(valueInFeet, getString(selectedUnitResId));
  }

  private void showResult(double feet, String selectedUnit) {
    containerLengthUnits.removeAllViews();

    for (UnitConverter.LengthUnit unit : UnitConverter.LengthUnit.values()) {
      String unitName = getString(unit.getResId());
      if (unitName.equals(selectedUnit)) continue;

      double convertedValue = unit.fromFeet(feet);
      String formatted = formatValue(convertedValue);

      addUnitRow(containerLengthUnits, unitName, formatted);
    }

    lastSharedText = buildShareableText(feet, selectedUnit);
    cardResult.setVisibility(View.VISIBLE);

    // Scroll to result
    cardResult.post(
        () -> {
          cardResult.requestFocus();
          cardResult.requestRectangleOnScreen(
              new android.graphics.Rect(0, 0, cardResult.getWidth(), cardResult.getHeight()), true);
        });
  }

  private String buildShareableText(double feet, String selectedUnit) {
    StringBuilder sb = new StringBuilder();

    double inputValue = UnitConverter.convertLength(feet, R.string.unit_foot, selectedUnitResId);

    sb.append(getString(R.string.label_converted_values)).append("\n\n");
    sb.append(getString(R.string.label_convert_length)).append(": ");

    // Show proper format when Foot + Inch
    if (selectedUnitResId == R.string.unit_foot) {
      double mainFt = parseDoubleOrZero(etInputLength.getText());
      double inch = parseDoubleOrZero(etInputInch.getText());
      sb.append(String.format("%.2f", mainFt))
          .append(" ")
          .append(getString(R.string.unit_foot))
          .append(" ")
          .append(String.format("%.2f", inch))
          .append(" ")
          .append(getString(R.string.unit_inch));
    } else {
      sb.append(formatValue(inputValue)).append(" ").append(selectedUnit);
    }

    sb.append("\n\n");
    sb.append("──────────────────────────────\n\n");
    sb.append(getString(R.string.label_converted_values)).append(":\n");

    for (UnitConverter.LengthUnit unit : UnitConverter.LengthUnit.values()) {
      double value = unit.fromFeet(feet);
      sb.append("• ")
          .append(getString(unit.getResId()))
          .append(" : ")
          .append(formatValue(value))
          .append("\n");
    }

    sb.append("\n").append(getString(R.string.share_footer));
    return sb.toString();
  }

  // ================== Helpers ==================
  private void addUnitRow(ViewGroup parent, String label, String value) {
    View row = LayoutInflater.from(this).inflate(R.layout.item_unit_row, parent, false);
    MaterialTextView tvLabel = row.findViewById(R.id.tv_unit_name);
    MaterialTextView tvValue = row.findViewById(R.id.tv_unit_value);
    tvLabel.setText(label);
    tvValue.setText(value);
    parent.addView(row);
  }

  private String formatValue(double value) {
    if (value >= 10000 || value < 0.001) return String.format("%.4f", value);
    if (value >= 100) return String.format("%.2f", value);
    return String.format("%.3f", value);
  }

  private double parseDoubleOrZero(CharSequence s) {
    if (s == null || s.toString().trim().isEmpty()) return 0.0;
    try {
      return Double.parseDouble(s.toString().trim());
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  private void hideResult() {
    cardResult.setVisibility(View.GONE);
    lastSharedText = "";
  }

  private void closeKeyboard() {
    View view = getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
