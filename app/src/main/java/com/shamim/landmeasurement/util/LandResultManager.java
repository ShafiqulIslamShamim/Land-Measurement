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
import com.shamim.landmeasurement.recycle_view.NewsAdapter;
import java.io.File;
import java.io.FileWriter;

public class LandResultManager {

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

  public void setOnCalculateClickListener(OnCalculateClickListener listener) {
    this.listener = listener;
    btnCalculate.setOnClickListener(
        v -> {
          if (listener != null) listener.onCalculateClicked();
        });
  }

  // ================== Show Result (Main Method) ==================
  public void showResult(double areaSqFt, String selectedUnit) {
    clearContainers();

    // International Units
    addInternationalUnits(areaSqFt, selectedUnit);

    // Regional Standard Units
    addRegionalStandardUnits(areaSqFt, selectedUnit);

    // Regional Localized Units
    addRegionalLocalizedUnits(areaSqFt, selectedUnit);

    closeKeyboard();
    cardResult.setVisibility(View.VISIBLE);
  }

  public void showResultWithScroll(double areaSqFt, String selectedUnit, ScrollView scrollView) {
    showResult(areaSqFt, selectedUnit);

    cardResult.post(
        () -> {
          cardResult.requestFocus();
          cardResult.requestRectangleOnScreen(
              new Rect(0, 0, cardResult.getWidth(), cardResult.getHeight()), true);
        });
  }

  public void hideResult() {
    cardResult.setVisibility(View.GONE);
    lastSharedText = "";
  }

  private void clearContainers() {
    containerInternational.removeAllViews();
    containerRegionalStandard.removeAllViews();
    containerRegionalLocalized.removeAllViews();
  }

  // ================== Unit Addition ==================
  private void addInternationalUnits(double areaSqFt, String excludeUnit) {
    addUnitIfNotSelected(containerInternational, R.string.unit_sqft, areaSqFt, excludeUnit, "%.4f");
    addUnitIfNotSelected(containerInternational, R.string.unit_sqm, areaSqFt, excludeUnit, "%.4f");
    addUnitIfNotSelected(containerInternational, R.string.unit_acre, areaSqFt, excludeUnit, "%.4f");
    addUnitIfNotSelected(
        containerInternational, R.string.unit_hectare, areaSqFt, excludeUnit, "%.4f");
  }

  private void addRegionalStandardUnits(double areaSqFt, String excludeUnit) {
    addUnitIfNotSelected(
        containerRegionalStandard, R.string.unit_shotok, areaSqFt, excludeUnit, "%.3f");
    addUnitIfNotSelected(
        containerRegionalStandard, R.string.unit_katha, areaSqFt, excludeUnit, "%.3f");
    addUnitIfNotSelected(
        containerRegionalStandard, R.string.unit_bigha, areaSqFt, excludeUnit, "%.3f");
  }

  private void addRegionalLocalizedUnits(double areaSqFt, String excludeUnit) {
    double shotok = UnitConverter.convertArea(areaSqFt, R.string.unit_sqft, R.string.unit_shotok);

    addUnitIfNotSelected(
        containerRegionalLocalized, R.string.unit_kora, shotok, excludeUnit, "%.3f");
    addUnitIfNotSelected(
        containerRegionalLocalized, R.string.unit_joistho, shotok, excludeUnit, "%.3f");
    addUnitIfNotSelected(
        containerRegionalLocalized, R.string.unit_kani, shotok, excludeUnit, "%.3f");
  }

  private void addUnitIfNotSelected(
      ViewGroup container, int unitResId, double areaSqFt, String excludeUnit, String format) {
    String unitName = context.getString(unitResId);
    if (unitName.equals(excludeUnit)) return;

    double converted = UnitConverter.convertArea(areaSqFt, R.string.unit_sqft, unitResId);
    addUnitRow(container, unitName, String.format(format, converted));
  }

  // ================== Share Logic ==================
  public String buildShareableText(double areaSqFt, String title) {
    StringBuilder sb = new StringBuilder();

    sb.append(getString(R.string.card_conversion_hind)).append("\n\n");
    sb.append(title).append("\n");

    sb.append(getString(R.string.share_area_label))
        .append(String.format("%.2f", areaSqFt))
        .append(" ")
        .append(getString(R.string.unit_sqft))
        .append("\n\n");

    sb.append("──────────────────────────────\n\n");

    appendInternationalSection(sb, areaSqFt);
    appendRegionalStandardSection(sb, areaSqFt);
    appendRegionalLocalizedSection(sb, areaSqFt);

    sb.append(getString(R.string.share_footer));
    return sb.toString();
  }

  private void appendInternationalSection(StringBuilder sb, double areaSqFt) {
    sb.append(getString(R.string.result_item_international)).append(":\n");
    appendUnitToShare(sb, R.string.unit_sqm, areaSqFt, "%.4f");
    appendUnitToShare(sb, R.string.unit_acre, areaSqFt, "%.4f");
    appendUnitToShare(sb, R.string.unit_hectare, areaSqFt, "%.4f");
    sb.append("\n");
  }

  private void appendRegionalStandardSection(StringBuilder sb, double areaSqFt) {
    sb.append(getString(R.string.result_item_regional_standard)).append(":\n");
    appendUnitToShare(sb, R.string.unit_shotok, areaSqFt, "%.3f");
    appendUnitToShare(sb, R.string.unit_katha, areaSqFt, "%.3f");
    appendUnitToShare(sb, R.string.unit_bigha, areaSqFt, "%.3f");
    sb.append("\n");
  }

  private void appendRegionalLocalizedSection(StringBuilder sb, double areaSqFt) {
    sb.append(getString(R.string.result_item_regional_localized)).append(":\n");
    double shotok = UnitConverter.convertArea(areaSqFt, R.string.unit_sqft, R.string.unit_shotok);

    appendConvertedUnit(sb, R.string.unit_kora, shotok, "%.3f");
    appendConvertedUnit(sb, R.string.unit_joistho, shotok, "%.3f");
    appendConvertedUnit(sb, R.string.unit_kani, shotok, "%.3f");
    sb.append("\n");
  }

  private void appendUnitToShare(StringBuilder sb, int unitResId, double areaSqFt, String format) {
    double value = UnitConverter.convertArea(areaSqFt, R.string.unit_sqft, unitResId);
    sb.append("• ")
        .append(getString(unitResId))
        .append(" : ")
        .append(String.format(format, value))
        .append("\n");
  }

  private void appendConvertedUnit(
      StringBuilder sb, int unitResId, double shotokValue, String format) {
    double value = UnitConverter.convertArea(shotokValue, R.string.unit_shotok, unitResId);
    sb.append("• ")
        .append(getString(unitResId))
        .append(" : ")
        .append(String.format(format, value))
        .append("\n");
  }

  // ================== Helper Methods ==================
  private void addUnitRow(ViewGroup parent, String label, String value) {
    View row = LayoutInflater.from(context).inflate(R.layout.item_unit_row, parent, false);
    MaterialTextView tvLabel = row.findViewById(R.id.tv_unit_name);
    MaterialTextView tvValue = row.findViewById(R.id.tv_unit_value);
    tvLabel.setText(label);
    tvValue.setText(value);
    parent.addView(row);
  }

  private void showShareOptionsDialog() {
    if (lastSharedText.isEmpty()) {
      Toast.makeText(context, R.string.no_result_to_share, Toast.LENGTH_SHORT).show();
      return;
    }

    String[] titles = {getString(R.string.share_as_text), getString(R.string.share_as_file)};
    int[] icons = {R.drawable.text_ad_24px, R.drawable.file_export_24px};

    NewsAdapter adapter =
        new NewsAdapter(
            titles,
            icons,
            pos -> {
              if (pos == 0) shareAsPlainText();
              else if (pos == 1) shareAsTextFile();
            });

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

  public void setLastSharedText(String text) {
    this.lastSharedText = text;
  }

  public void closeKeyboard() {
    if (context == null || rootView == null) return;
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }
  }

  private String getString(int resId) {
    return context.getString(resId);
  }
}
