/*
 * Copyright (c) 2026 Shafiqul Islam Shamim
 * GitHub: https://github.com/ShafiqulIslamShamim/Land-Measurement
 *
 * All Rights Reserved.
 *
 * This source code is made publicly available solely for viewing, collaboration,
 * educational reference, and submitting pull requests to the official repository.
 *
 * No permission is granted to copy, modify, redistribute, sublicense, or use
 * this source code, in whole or in part, for personal, commercial, or any other
 * purpose without the prior written permission of the copyright holder.
 */
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

  /**
   * Side calculation manager.
   *
   * @param context the context
   */
  public SideCalculationManager(Context context) {
    this.context = context;
    this.inflater = LayoutInflater.from(context);
  }

  // ================== Result Section Setup ==================
  /**
   * Setup result section.
   *
   * @param resultSection the resultSection
   */
  public void setupResultSection(View resultSection) {
    btnCalculate = resultSection.findViewById(R.id.btn_calculate);
    btnShare = resultSection.findViewById(R.id.btn_share);
    cardResult = resultSection.findViewById(R.id.card_result);
    containerResultUnits = resultSection.findViewById(R.id.container_length_units);
    sideTitle = resultSection.findViewById(R.id.side_title);
  }

  /**
   * Set title.
   *
   * @param title the title
   */
  public void setTitle(String title) {
    sideTitle.setText(title);
  }

  /**
   * Set calculate click listener.
   *
   * @param onCalculate the onCalculate
   */
  public void setCalculateClickListener(Runnable onCalculate) {
    if (btnCalculate != null) {
      btnCalculate.setOnClickListener(v -> onCalculate.run());
    }
  }

  /**
   * Set share click listener.
   *
   * @param onShare the onShare
   */
  public void setShareClickListener(Runnable onShare) {
    if (btnShare != null) {
      btnShare.setOnClickListener(v -> onShare.run());
    }
  }

  // ================== Area Conversion ==================
  /**
   * Get area in sq ft.
   *
   * @param area the area
   * @param areaUnitResId the areaUnitResId
   * @return the result of the operation
   */
  public double getAreaInSqFt(double area, int areaUnitResId) {
    if (area <= 0) return 0;
    return UnitConverter.convertArea(area, areaUnitResId, R.string.unit_sqft);
  }

  // ================== Result Display ==================
  /**
   * Show result.
   *
   * @param otherSideInFeet the otherSideInFeet
   */
  public void showResult(double otherSideInFeet) {
    populateResult(otherSideInFeet);
    if (cardResult != null) cardResult.setVisibility(View.VISIBLE);
    closeKeyboard();
  }

  /** Hide result. */
  public void hideResult() {
    if (cardResult != null) {
      cardResult.setVisibility(View.GONE);
    }
    lastSharedText = "";
  }

  /**
   * Populate result.
   *
   * @param otherSideInFeet the otherSideInFeet
   */
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

  /**
   * Add unit row.
   *
   * @param parent the parent
   * @param label the label
   * @param value the value
   */
  private void addUnitRow(ViewGroup parent, String label, String value) {
    View row = inflater.inflate(R.layout.item_unit_row, parent, false);
    MaterialTextView tvLabel = row.findViewById(R.id.tv_unit_name);
    MaterialTextView tvValue = row.findViewById(R.id.tv_unit_value);
    tvLabel.setText(label);
    tvValue.setText(value);
    parent.addView(row);
  }

  // ================== Share Logic ==================
  /** Show share options dialog. */
  public void showShareOptionsDialog() {
    ShareUtils.showShareOptionsDialog(context, lastSharedText, "Side_Measurement_Result");
  }

  /**
   * Set last shared text.
   *
   * @param text the text
   */
  public void setLastSharedText(String text) {
    this.lastSharedText = text;
  }

  /**
   * Build shareable text.
   *
   * @param sb the sb
   * @param otherSideInFeet the otherSideInFeet
   * @return the result of the operation
   */
  public String buildShareableText(StringBuilder sb, double otherSideInFeet) {

    for (UnitValue uv : getAllUnitValues(otherSideInFeet)) {
      sb.append("• ").append(uv.unit).append(" : ").append(formatValue(uv.value)).append("\n");
    }
    sb.append("\n").append(context.getString(R.string.share_footer));
    return sb.toString();
  }

  /**
   * Format area.
   *
   * @param value the value
   * @return the result of the operation
   */
  private String formatArea(double value) {
    return value >= 100 ? String.format("%.2f", value) : String.format("%.3f", value);
  }

  /**
   * Format value.
   *
   * @param value the value
   * @return the result of the operation
   */
  private String formatValue(double value) {
    if (value >= 10000 || value < 0.001) return String.format("%.4f", value);
    if (value >= 100) return String.format("%.2f", value);
    return String.format("%.3f", value);
  }

  /** Close keyboard. */
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

  /**
   * Parse double or zero.
   *
   * @param s the s
   * @return the result of the operation
   */
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

    /**
     * Unit value.
     *
     * @param unit the unit
     * @param value the value
     */
    public UnitValue(String unit, double value) {
      this.unit = unit;
      this.value = value;
    }
  }
}
