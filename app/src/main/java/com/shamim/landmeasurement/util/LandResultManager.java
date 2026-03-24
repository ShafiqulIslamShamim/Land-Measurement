package com.shamim.landmeasurement.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.recycle_view.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LandResultManager {

  // Conversion constants (sq ft → other unit)
  private static final double SQFT_TO_SQM = 1.0 / 10.7639;
  private static final double SQFT_TO_ACRE = 1.0 / 43560.0;
  private static final double SQFT_TO_HECTARE = 1.0 / 107639.0;
  private static final double SQFT_TO_SHOTOK = 1.0 / 435.6;
  private static final double SQFT_TO_KATHA = 1.0 / 720.0;
  private static final double SQFT_TO_BIGHA = 1.0 / 14400.0;

  // For localized sub-units (based on Shotok)
  private static final double SHOTOK_TO_KORA = 1.0 / 2.0;
  private static final double KORA_TO_JOISTHO = 1.0 / 10.0;
  private static final double KORA_TO_KANI = 1.0 / 80.0;

  private final Context context;
  private final MaterialButton btnCalculate;
  private final MaterialButton btnShare;
  private final MaterialCardView cardResult;

  private final ViewGroup containerInternational;
  private final ViewGroup containerRegionalStandard;
  private final ViewGroup containerRegionalLocalized;

  private OnCalculateClickListener listener;
  private final View section;
  private final View rootView;

  private String lastSharedText = "";

  public interface OnCalculateClickListener {
    void onCalculateClicked();
  }

  public LandResultManager(@NonNull View rootView, Context context) {
    this.context = context;
    this.rootView = rootView;

    this.section = rootView.findViewById(R.id.result_section);
    if (section == null) {
      throw new IllegalStateException(
          "Result section not found. Make sure layout has id='result_section'");
    }

    this.btnCalculate = section.findViewById(R.id.btn_calculate);
    this.btnShare = section.findViewById(R.id.btn_share);
    this.cardResult = section.findViewById(R.id.card_result);

    this.containerInternational = section.findViewById(R.id.container_international_units);
    this.containerRegionalStandard = section.findViewById(R.id.container_regional_standard_units);
    this.containerRegionalLocalized = section.findViewById(R.id.container_regional_localized_units);

    if (containerInternational == null
        || containerRegionalStandard == null
        || containerRegionalLocalized == null) {
      throw new IllegalStateException("One or more result containers not found in layout");
    }

    btnShare.setOnClickListener(v -> showShareOptionsDialog());
  }

  public View sectionLayout() {
    return section;
  }

  public void closeKeyboard() {
    if (context == null || rootView == null) return;
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }
  }

  public void setOnCalculateClickListener(OnCalculateClickListener listener) {
    this.listener = listener;
    btnCalculate.setOnClickListener(
        v -> {
          if (listener != null) {
            listener.onCalculateClicked();
          }
        });
  }

  public void showResult(double areaSqFt, String selectedUnit, Map<String, Double> inputMap) {
    // Clear previous content
    containerInternational.removeAllViews();
    containerRegionalStandard.removeAllViews();
    containerRegionalLocalized.removeAllViews();

    // ─────────────────────────────────────────────
    // International / Common units
    // ─────────────────────────────────────────────
    List<UnitValue> international = new ArrayList<>();
    international.add(new UnitValue(getString(R.string.unit_sqft), areaSqFt));
    international.add(new UnitValue(getString(R.string.unit_sqm), areaSqFt * SQFT_TO_SQM));
    international.add(new UnitValue(getString(R.string.unit_acre), areaSqFt * SQFT_TO_ACRE));
    international.add(new UnitValue(getString(R.string.unit_hectare), areaSqFt * SQFT_TO_HECTARE));

    addUnitsExcludingSelected(containerInternational, international, selectedUnit, "%.4f");

    // ─────────────────────────────────────────────
    // Regional Standard units
    // ─────────────────────────────────────────────
    List<UnitValue> regionalStandard = new ArrayList<>();
    regionalStandard.add(new UnitValue(getString(R.string.unit_shotok), areaSqFt * SQFT_TO_SHOTOK));
    regionalStandard.add(new UnitValue(getString(R.string.unit_katha), areaSqFt * SQFT_TO_KATHA));
    regionalStandard.add(new UnitValue(getString(R.string.unit_bigha), areaSqFt * SQFT_TO_BIGHA));

    addUnitsExcludingSelected(containerRegionalStandard, regionalStandard, selectedUnit, "%.3f");

    // ─────────────────────────────────────────────
    // Regional Localized units (derived from Shotok)
    // ─────────────────────────────────────────────
    double shotok = areaSqFt * SQFT_TO_SHOTOK;
    List<UnitValue> regionalLocalized = new ArrayList<>();
    regionalLocalized.add(new UnitValue(getString(R.string.unit_kora), shotok * SHOTOK_TO_KORA));
    regionalLocalized.add(
        new UnitValue(getString(R.string.unit_joistho), shotok * SHOTOK_TO_KORA * KORA_TO_JOISTHO));
    regionalLocalized.add(
        new UnitValue(getString(R.string.unit_kani), shotok * SHOTOK_TO_KORA * KORA_TO_KANI));

    addUnitsExcludingSelected(containerRegionalLocalized, regionalLocalized, selectedUnit, "%.3f");

    // Prepare shareable content
    lastSharedText = buildShareableText(areaSqFt, selectedUnit, inputMap);

    closeKeyboard();
    cardResult.setVisibility(View.VISIBLE);
  }

  public void hideResult() {
    cardResult.setVisibility(View.GONE);
  }

  private void addUnitsExcludingSelected(
      ViewGroup container, List<UnitValue> units, String excludeUnit, String format) {
    for (UnitValue uv : units) {
      if (!uv.unit.equals(excludeUnit)) {
        addUnitRow(container, uv.unit, String.format(format, uv.value));
      }
    }
  }

  public void showResultWithScroll(
      double areaSqFt, String selectedUnit, Map<String, Double> inputMap, ScrollView scrollView) {
    showResult(areaSqFt, selectedUnit, inputMap);

    /*
        cardResult.post(() -> {
        Rect rect = new Rect(0, 0, cardResult.getWidth(), cardResult.getHeight());

        // View-এর relative rect কে parent (ScrollView)-এ convert করে
        cardResult.getDrawingRect(rect);
        scrollView.offsetDescendantRectToMyCoords(cardResult, rect);

        int scrollY = scrollView.getScrollY();
        int scrollViewHeight = scrollView.getHeight();

        int targetScrollY;

        if (rect.top < scrollY) {
            targetScrollY = rect.top;
        } else if (rect.bottom > scrollY + scrollViewHeight) {
            targetScrollY = rect.bottom - scrollViewHeight;
        } else {
            return; // already visible
        }

        scrollView.smoothScrollTo(0, targetScrollY);
    });

    */

    cardResult.post(
        () -> {
          cardResult.requestFocus();
          cardResult.requestRectangleOnScreen(
              new Rect(0, 0, cardResult.getWidth(), cardResult.getHeight()), true);
        });
  }

  private String buildShareableText(
      double areaSqFt, String selectedUnit, Map<String, Double> inputMap) {
    StringBuilder sb = new StringBuilder();

    sb.append(getString(R.string.card_conversion_hind)).append("\n\n");
    sb.append(parseMapDimensions(inputMap)).append("\n");
    sb.append(getString(R.string.share_area_label))
        .append(String.format("%.2f", areaSqFt))
        .append(" ")
        .append(getString(R.string.unit_sqft))
        .append("\n");

    if (!selectedUnit.isEmpty() && !selectedUnit.equals(getString(R.string.unit_sqft))) {
      sb.append(getString(R.string.share_selected_unit)).append(selectedUnit).append("\n");
    }
    sb.append("──────────────────────────────\n\n");

    // International
    sb.append(getString(R.string.result_item_international)).append(":\n");
    sb.append("• ")
        .append(getString(R.string.unit_sqm))
        .append(" : ")
        .append(String.format("%.4f", areaSqFt * SQFT_TO_SQM))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_acre))
        .append(" : ")
        .append(String.format("%.4f", areaSqFt * SQFT_TO_ACRE))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_hectare))
        .append(" : ")
        .append(String.format("%.4f", areaSqFt * SQFT_TO_HECTARE))
        .append("\n\n");

    // Regional Standard
    double shotok = areaSqFt * SQFT_TO_SHOTOK;
    sb.append(getString(R.string.result_item_regional_standard)).append(":\n");
    sb.append("• ")
        .append(getString(R.string.unit_shotok))
        .append(" : ")
        .append(String.format("%.3f", shotok))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_katha))
        .append(" : ")
        .append(String.format("%.3f", areaSqFt * SQFT_TO_KATHA))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_bigha))
        .append(" : ")
        .append(String.format("%.3f", areaSqFt * SQFT_TO_BIGHA))
        .append("\n\n");

    // Regional Localized
    sb.append(getString(R.string.result_item_regional_localized)).append(":\n");
    sb.append("• ")
        .append(getString(R.string.unit_kora))
        .append(" : ")
        .append(String.format("%.3f", shotok * SHOTOK_TO_KORA))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_joistho))
        .append(" : ")
        .append(String.format("%.3f", shotok * SHOTOK_TO_KORA * KORA_TO_JOISTHO))
        .append("\n");
    sb.append("• ")
        .append(getString(R.string.unit_kani))
        .append(" : ")
        .append(String.format("%.3f", shotok * SHOTOK_TO_KORA * KORA_TO_KANI))
        .append("\n\n");

    sb.append(getString(R.string.share_footer));
    return sb.toString();
  }

  public String parseMapDimensions(Map<String, Double> inputMap) {
    if (inputMap == null || inputMap.isEmpty()) {
      return getString(R.string.error_map_empty);
    }

    List<String> keys = new ArrayList<>(inputMap.keySet());
    List<Double> values = new ArrayList<>(inputMap.values());
    int size = keys.size();

    if (size == 4) {
      return getString(R.string.share_lengths_label)
          + " "
          + keys.get(0)
          + ": "
          + ft(values.get(0))
          + ", "
          + keys.get(1)
          + ": "
          + ft(values.get(1))
          + "\n"
          + getString(R.string.share_widths_label)
          + " "
          + keys.get(2)
          + ": "
          + ft(values.get(2))
          + ", "
          + keys.get(3)
          + ": "
          + ft(values.get(3));
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < size; i++) {
      if (i > 0) sb.append(", ");
      sb.append(keys.get(i)).append(": ").append(ft(values.get(i)));
    }

    return sb.toString();
  }

  private String ft(Double v) {
    return context.getString(R.string.value_with_unit, v, context.getString(R.string.unit_foot));
  }

  private void showShareOptionsDialog() {
    if (lastSharedText.isEmpty()) return;

    String[] titles = {getString(R.string.share_as_text), getString(R.string.share_as_file)};

    int[] icons = {R.drawable.text_ad_24px, R.drawable.file_export_24px};

    // RecyclerView Adapter
    NewsAdapter adapter =
        new NewsAdapter(
            titles,
            icons,
            pos -> {
              if (pos == 0) {
                shareAsPlainText();
              } else if (pos == 1) {
                shareAsTextFile();
              }
            });

    // RecyclerView Layout
    RecyclerView recyclerView = new RecyclerView(context);
    recyclerView.setLayoutManager(new LinearLayoutManager(context));
    recyclerView.setAdapter(adapter);
    recyclerView.setPadding(30, 30, 30, 30);

    new MaterialAlertDialogBuilder(context)
        .setTitle(getString(R.string.share_choose_option))
        .setView(recyclerView)
        .setPositiveButton(R.string.close, null)
        .show();
  }

  // ─────────────────────────────────────────────
  // Share implementations (unchanged)
  // ─────────────────────────────────────────────

  private void shareAsPlainText() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_title));
    shareIntent.putExtra(Intent.EXTRA_TEXT, lastSharedText);

    context.startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
  }

  private void shareAsTextFile() {
    try {
      File file = new File(context.getCacheDir(), "land_measurement_result.txt");
      try (FileWriter writer = new FileWriter(file)) {
        writer.write(lastSharedText);
      }

      Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.setType("text/plain");
      shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
      shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

      context.startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
    } catch (Exception e) {
      Toast.makeText(
              context,
              getString(R.string.error_cannot_create_file) + e.getMessage(),
              Toast.LENGTH_SHORT)
          .show();
    }
  }

  private void addUnitRow(ViewGroup parent, String label, String value) {
    View row = LayoutInflater.from(context).inflate(R.layout.item_unit_row, parent, false);
    MaterialTextView tvLabel = row.findViewById(R.id.tv_unit_name);
    MaterialTextView tvValue = row.findViewById(R.id.tv_unit_value);
    tvLabel.setText(label);
    tvValue.setText(value);
    parent.addView(row);
  }

  private String getString(int resId) {
    return context.getString(resId);
  }

  private static class UnitValue {
    final String unit;
    final double value;

    UnitValue(String unit, double value) {
      this.unit = unit;
      this.value = value;
    }
  }
}
