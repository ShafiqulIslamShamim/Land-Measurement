package com.shamim.landmeasurement.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.recycle_view.NewsAdapter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinearUnitsConversionActivity extends BaseActivity {

  private static final double INCHES_PER_FOOT = 12.0;
  private static final double FEET_PER_YARD = 0.333333; // 1/3
  private static final double HATH_PER_FOOT = 0.6666666666666666666; // ≈ 2/3
  private static final double METERS_PER_FOOT = 0.3048;
  private static final double CENTIMETERS_PER_FOOT = 30.48;
  private static final double MILLIMETERS_PER_FOOT = 304.8;

  // Derived / consistent with original repeating style
  private static final double YARDS_PER_FOOT = 0.333333;
  private static final double FEET_PER_HATH = 1.5; // 1 / 0.666...
  private static final double HATH_PER_FOOT_ORIGINAL = 0.6666666666666666666;

  private MaterialToolbar toolbar;
  private TextInputEditText etInputLength;
  private ChipGroup chipGroupUnits;
  private ScrollView scrollView;
  private MaterialButton btnCalculate;
  private MaterialButton btnShare;
  private MaterialCardView cardResult;
  private ViewGroup containerLengthUnits;

  private int selectedUnitResId = R.string.unit_foot;
  private String lastSharedText = "";

  // unit resource id → how many feet = 1 of this unit
  private final Map<Integer, Double> feetPerUnit = new HashMap<>();

  {
    feetPerUnit.put(R.string.unit_inch, 1.0 / INCHES_PER_FOOT); // 1 inch = 1/12 ft
    feetPerUnit.put(R.string.unit_foot, 1.0);
    feetPerUnit.put(R.string.unit_yard, 1.0 / FEET_PER_YARD); // 1 yard ≈ 3 ft
    feetPerUnit.put(R.string.unit_hath, FEET_PER_HATH); // 1 hath ≈ 1.5 ft
    feetPerUnit.put(R.string.unit_meter, 1.0 / METERS_PER_FOOT);
    feetPerUnit.put(R.string.unit_centimeter, 1.0 / CENTIMETERS_PER_FOOT);
    feetPerUnit.put(R.string.unit_millimeter, 1.0 / MILLIMETERS_PER_FOOT);
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
    btnShare = findViewById(R.id.btn_share);
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
    btnShare.setOnClickListener(v -> showShareOptionsDialog());
  }

  private void calculate() {
    String inputStr = etInputLength.getText().toString().trim();
    double inputValue = parseDoubleOrZero(inputStr);

    if (inputValue <= 0) {
      Toast.makeText(this, R.string.conversation_length_error_invalid_value, Toast.LENGTH_SHORT)
          .show();
      hideResult();
      return;
    }

    Double feetFactor = feetPerUnit.get(selectedUnitResId);
    if (feetFactor == null) {
      Toast.makeText(this, R.string.conversation_length_error_unit_not_found, Toast.LENGTH_SHORT)
          .show();
      hideResult();
      return;
    }

    double valueInFeet = inputValue * feetFactor;
    showResult(valueInFeet, getString(selectedUnitResId));
  }

  private void showResult(double feet, String selectedUnit) {
    containerLengthUnits.removeAllViews();

    List<UnitValue> units = new ArrayList<>();
    units.add(new UnitValue(getString(R.string.unit_inch), feet * INCHES_PER_FOOT));
    units.add(new UnitValue(getString(R.string.unit_foot), feet));
    units.add(new UnitValue(getString(R.string.unit_yard), feet * YARDS_PER_FOOT));
    units.add(new UnitValue(getString(R.string.unit_hath), feet * HATH_PER_FOOT_ORIGINAL));
    units.add(new UnitValue(getString(R.string.unit_meter), feet * METERS_PER_FOOT));
    units.add(new UnitValue(getString(R.string.unit_centimeter), feet * CENTIMETERS_PER_FOOT));
    units.add(new UnitValue(getString(R.string.unit_millimeter), feet * MILLIMETERS_PER_FOOT));

    for (UnitValue uv : units) {
      if (!uv.unit.equals(selectedUnit)) {
        String formatted = formatValue(uv.value);
        addUnitRow(containerLengthUnits, uv.unit, formatted);
      }
    }

    lastSharedText = buildShareableText(feet, selectedUnit);

    closeKeyboard();
    cardResult.setVisibility(View.VISIBLE);

    cardResult.post(
        () -> {
          cardResult.requestFocus();
          cardResult.requestRectangleOnScreen(
              new android.graphics.Rect(0, 0, cardResult.getWidth(), cardResult.getHeight()), true);
        });
  }

  private String buildShareableText(double feet, String selectedUnit) {
    StringBuilder sb = new StringBuilder();

    Double feetFactor = feetPerUnit.get(selectedUnitResId);
    double inputValue = (feetFactor != null) ? feet / feetFactor : 0;

    sb.append(getString(R.string.label_converted_values)).append("\n\n");
    sb.append(getString(R.string.label_convert_length));
    sb.append(": ").append(formatValue(inputValue)).append(" ").append(selectedUnit).append("\n\n");
    sb.append("──────────────────────────────\n\n");

    sb.append(getString(R.string.label_converted_values));
    sb.append(":\n");
    sb.append("• ")
        .append(getString(R.string.unit_inch))
        .append(" : ")
        .append(formatValue(feet * INCHES_PER_FOOT))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_foot))
        .append(" : ")
        .append(formatValue(feet))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_yard))
        .append(" : ")
        .append(formatValue(feet * YARDS_PER_FOOT))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_hath))
        .append(" : ")
        .append(formatValue(feet * HATH_PER_FOOT_ORIGINAL))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_meter))
        .append(" : ")
        .append(formatValue(feet * METERS_PER_FOOT))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_centimeter))
        .append(" : ")
        .append(formatValue(feet * CENTIMETERS_PER_FOOT))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_millimeter))
        .append(" : ")
        .append(formatValue(feet * MILLIMETERS_PER_FOOT))
        .append("\n\n");

    sb.append(getString(R.string.share_footer));

    return sb.toString();
  }

  private void showShareOptionsDialog() {
    if (lastSharedText.isEmpty()) {
      Toast.makeText(this, R.string.no_result_to_share, Toast.LENGTH_SHORT).show();
      return;
    }

    String[] titles = {getString(R.string.share_as_text), getString(R.string.share_as_file)};

    int[] icons = {R.drawable.text_ad_24px, R.drawable.file_export_24px};

    NewsAdapter adapter =
        new NewsAdapter(
            titles,
            icons,
            position -> {
              if (position == 0) shareAsPlainText();
              else if (position == 1) shareAsTextFile();
            });

    RecyclerView recyclerView = new RecyclerView(this);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);
    recyclerView.setPadding(30, 30, 30, 30);

    new MaterialAlertDialogBuilder(this)
        .setTitle(getString(R.string.share_choose_option))
        .setView(recyclerView)
        .setPositiveButton(R.string.close, null)
        .show();
  }

  private void shareAsPlainText() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_title));
    shareIntent.putExtra(Intent.EXTRA_TEXT, lastSharedText);
    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
  }

  private void shareAsTextFile() {
    try {
      File file = new File(getCacheDir(), "linear_conversion_result.txt");
      try (FileWriter writer = new FileWriter(file)) {
        writer.write(lastSharedText);
      }

      Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.setType("text/plain");
      shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
      shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

      startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
    } catch (Exception e) {
      Toast.makeText(
              this,
              getString(R.string.error_cannot_create_file) + e.getMessage(),
              Toast.LENGTH_SHORT)
          .show();
    }
  }

  public void closeKeyboard() {
    View view = getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      if (imm != null) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
      }
    }
  }

  private String formatValue(double value) {
    if (value >= 10000 || value < 0.001) return String.format("%.4f", value);
    if (value >= 100) return String.format("%.2f", value);
    return String.format("%.3f", value);
  }

  private void hideResult() {
    cardResult.setVisibility(View.GONE);
    lastSharedText = "";
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
    final String unit;
    final double value;

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
