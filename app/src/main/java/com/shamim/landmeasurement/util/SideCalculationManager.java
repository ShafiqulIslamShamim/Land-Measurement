package com.shamim.landmeasurement.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.shamim.landmeasurement.R;
import java.util.ArrayList;
import java.util.List;

public class SideCalculationManager {

  private final Context context;
  private final LayoutInflater inflater;

  // UI Components
  private MaterialButton btnCalculate;
  private MaterialButton btnShare;
  private MaterialCardView cardResult;
  private ViewGroup containerResultUnits;
  private MaterialTextView sideTitle;

  private String lastSharedText = "";

  public SideCalculationManager(Context context) {
    this.context = context;
    this.inflater = LayoutInflater.from(context);
  }

  // ================== Result Section Setup ==================
  public void setupResultSection(View resultSection) {
    btnCalculate = resultSection.findViewById(R.id.btn_calculate);
    btnShare = resultSection.findViewById(R.id.btn_share);
    cardResult = resultSection.findViewById(R.id.card_result);
    containerResultUnits = resultSection.findViewById(R.id.container_length_units);
    sideTitle = resultSection.findViewById(R.id.side_title);
  }

  public void setTitle(String title) {
    sideTitle.setText(title);
  }

  public void setCalculateClickListener(Runnable onCalculate) {
    if (btnCalculate != null) {
      btnCalculate.setOnClickListener(v -> onCalculate.run());
    }
  }

  public void setShareClickListener(Runnable onShare) {
    if (btnShare != null) {
      btnShare.setOnClickListener(v -> onShare.run());
    }
  }

  // ================== Area Conversion ==================
  public double getAreaInSqFt(double area, int areaUnitResId) {
    if (area <= 0) return 0;
    return UnitConverter.convertArea(area, areaUnitResId, R.string.unit_sqft);
  }

  // ================== Result Display ==================
  public void showResult(double otherSideInFeet) {
    populateResult(otherSideInFeet);
    if (cardResult != null) cardResult.setVisibility(View.VISIBLE);
    closeKeyboard();
  }

  public void hideResult() {
    if (cardResult != null) {
      cardResult.setVisibility(View.GONE);
    }
    lastSharedText = "";
  }

  private void populateResult(double otherSideInFeet) {
    if (containerResultUnits == null) return;
    containerResultUnits.removeAllViews();

    for (UnitValue uv : getAllUnitValues(otherSideInFeet)) {
      addUnitRow(containerResultUnits, uv.unit, formatValue(uv.value));
    }
  }

  /** Returns all length units */
  public List<UnitValue> getAllUnitValues(double lengthInFeet) {
    List<UnitValue> units = new ArrayList<>();

    for (UnitConverter.LengthUnit unit : UnitConverter.LengthUnit.values()) {
      double convertedValue = unit.fromFeet(lengthInFeet);
      String unitName = context.getString(unit.getResId());
      units.add(new UnitValue(unitName, convertedValue));
    }

    return units;
  }

  private void addUnitRow(ViewGroup parent, String label, String value) {
    View row = inflater.inflate(R.layout.item_unit_row, parent, false);
    MaterialTextView tvLabel = row.findViewById(R.id.tv_unit_name);
    MaterialTextView tvValue = row.findViewById(R.id.tv_unit_value);
    tvLabel.setText(label);
    tvValue.setText(value);
    parent.addView(row);
  }

  // ================== Share Logic ==================
  public void showShareOptionsDialog() {
    ShareUtils.showShareOptionsDialog(context, lastSharedText, "Side_Measurement_Result");
  }

  public void setLastSharedText(String text) {
    this.lastSharedText = text;
  }

  public String buildShareableText(StringBuilder sb, double otherSideInFeet) {

    for (UnitValue uv : getAllUnitValues(otherSideInFeet)) {
      sb.append("• ").append(uv.unit).append(" : ").append(formatValue(uv.value)).append("\n");
    }
    sb.append("\n").append(context.getString(R.string.share_footer));
    return sb.toString();
  }

  private String formatArea(double value) {
    return value >= 100 ? String.format("%.2f", value) : String.format("%.3f", value);
  }

  private String formatValue(double value) {
    if (value >= 10000 || value < 0.001) return String.format("%.4f", value);
    if (value >= 100) return String.format("%.2f", value);
    return String.format("%.3f", value);
  }

  private void closeKeyboard() {
    if (!(context instanceof android.app.Activity)) return;
    android.app.Activity activity = (android.app.Activity) context;
    View view = activity.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm =
          (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
      if (imm != null) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
      }
    }
  }

  public double parseDoubleOrZero(String s) {
    if (s == null || s.isEmpty()) return 0.0;
    try {
      return Double.parseDouble(s);
    } catch (Exception e) {
      return 0.0;
    }
  }

  // ===================== Inner Class =====================
  public static class UnitValue {
    public final String unit;
    public final double value;

    public UnitValue(String unit, double value) {
      this.unit = unit;
      this.value = value;
    }
  }
}
