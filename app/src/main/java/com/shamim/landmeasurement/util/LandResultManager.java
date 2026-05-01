package com.shamim.landmeasurement.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.shamim.landmeasurement.R;

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

    btnShare.setOnClickListener(
        v -> ShareUtils.showShareOptionsDialog(context, lastSharedText, "Measurement_Result"));
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

    addUnitIfNotSelected(
        containerRegionalLocalized, R.string.unit_kora, areaSqFt, excludeUnit, "%.3f");
    addUnitIfNotSelected(
        containerRegionalLocalized, R.string.unit_joistho, areaSqFt, excludeUnit, "%.3f");
    addUnitIfNotSelected(
        containerRegionalLocalized, R.string.unit_kani, areaSqFt, excludeUnit, "%.3f");
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

    appendUnitToShare(sb, R.string.unit_kora, areaSqFt, "%.3f");
    appendUnitToShare(sb, R.string.unit_joistho, areaSqFt, "%.3f");
    appendUnitToShare(sb, R.string.unit_kani, areaSqFt, "%.3f");
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

  // ================== Helper Methods ==================
  private void addUnitRow(ViewGroup parent, String label, String value) {
    View row = LayoutInflater.from(context).inflate(R.layout.item_unit_row, parent, false);
    MaterialTextView tvLabel = row.findViewById(R.id.tv_unit_name);
    MaterialTextView tvValue = row.findViewById(R.id.tv_unit_value);
    tvLabel.setText(label);
    tvValue.setText(value);
    parent.addView(row);
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
