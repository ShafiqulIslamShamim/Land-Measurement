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
import com.shamim.landmeasurement.util.UnitConverter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

public class LinearUnitsConversionActivity extends BaseActivity {

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

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_linear_units_conversion);

    initViews();
    setupToolbar();
    setupChipGroup();
    setupListeners();
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

    double valueInFeet =
        UnitConverter.convertLength(inputValue, selectedUnitResId, R.string.unit_foot);

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

    closeKeyboard();
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

    // Input value in selected unit
    double inputValue = UnitConverter.convertLength(feet, R.string.unit_foot, selectedUnitResId);

    sb.append(getString(R.string.label_converted_values)).append("\n\n");
    sb.append(getString(R.string.label_convert_length))
        .append(": ")
        .append(formatValue(inputValue))
        .append(" ")
        .append(selectedUnit)
        .append("\n\n");

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

  // ================== Share Logic ==================
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

  private double parseDoubleOrZero(String s) {
    if (s == null || s.isEmpty()) return 0.0;
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      return 0.0;
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

  private void hideResult() {
    cardResult.setVisibility(View.GONE);
    lastSharedText = "";
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
