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
import com.shamim.landmeasurement.activity.AreaUnitsConversionActivity;
import com.shamim.landmeasurement.activity.CircularLandActivity;
import com.shamim.landmeasurement.activity.RectangularLandActivity;
import com.shamim.landmeasurement.activity.ScaleneLandActivity;
import com.shamim.landmeasurement.activity.SquareLandActivity;
import com.shamim.landmeasurement.activity.TriangularLandActivityArm;
import com.shamim.landmeasurement.activity.TriangularLandActivityHeight;
import com.shamim.landmeasurement.history.HistoryDatabase;
import com.shamim.landmeasurement.history.HistoryEntry;

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

  /**
   * Land result manager.
   *
   * @param rootView the rootView
   * @param context the context
   */
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

  /**
   * Section layout.
   *
   * @return the result of the operation
   */
  public View sectionLayout() {
    return section;
  }

  /**
   * Set on calculate click listener.
   *
   * @param listener the listener
   */
  public void setOnCalculateClickListener(OnCalculateClickListener listener) {
    this.listener = listener;
    btnCalculate.setOnClickListener(
        v -> {
          if (listener != null) listener.onCalculateClicked();
        });
  }

  // ================== Show Result (Main Method) ==================
  /**
   * Show result.
   *
   * @param areaSqFt the areaSqFt
   * @param selectedUnit the selectedUnit
   */
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

  /**
   * Show result with scroll.
   *
   * @param areaSqFt the areaSqFt
   * @param selectedUnit the selectedUnit
   * @param scrollView the scrollView
   */
  public void showResultWithScroll(double areaSqFt, String selectedUnit, ScrollView scrollView) {
    showResult(areaSqFt, selectedUnit);

    cardResult.post(
        () -> {
          cardResult.requestFocus();
          cardResult.requestRectangleOnScreen(
              new Rect(0, 0, cardResult.getWidth(), cardResult.getHeight()), true);
        });
  }

  /** Hide result. */
  public void hideResult() {
    cardResult.setVisibility(View.GONE);
    lastSharedText = "";
  }

  /** Clear containers. */
  private void clearContainers() {
    containerInternational.removeAllViews();
    containerRegionalStandard.removeAllViews();
    containerRegionalLocalized.removeAllViews();
  }

  // ================== Unit Addition ==================
  /**
   * Add international units.
   *
   * @param areaSqFt the areaSqFt
   * @param excludeUnit the excludeUnit
   */
  private void addInternationalUnits(double areaSqFt, String excludeUnit) {
    addUnitIfNotSelected(containerInternational, R.string.unit_sqft, areaSqFt, excludeUnit, "%.4f");
    addUnitIfNotSelected(containerInternational, R.string.unit_sqm, areaSqFt, excludeUnit, "%.4f");
    addUnitIfNotSelected(containerInternational, R.string.unit_acre, areaSqFt, excludeUnit, "%.4f");
    addUnitIfNotSelected(
        containerInternational, R.string.unit_hectare, areaSqFt, excludeUnit, "%.4f");
  }

  /**
   * Add regional standard units.
   *
   * @param areaSqFt the areaSqFt
   * @param excludeUnit the excludeUnit
   */
  private void addRegionalStandardUnits(double areaSqFt, String excludeUnit) {
    addUnitIfNotSelected(
        containerRegionalStandard, R.string.unit_shotok, areaSqFt, excludeUnit, "%.3f");
    addUnitIfNotSelected(
        containerRegionalStandard, R.string.unit_katha, areaSqFt, excludeUnit, "%.3f");
    addUnitIfNotSelected(
        containerRegionalStandard, R.string.unit_bigha, areaSqFt, excludeUnit, "%.3f");
  }

  /**
   * Add regional localized units.
   *
   * @param areaSqFt the areaSqFt
   * @param excludeUnit the excludeUnit
   */
  private void addRegionalLocalizedUnits(double areaSqFt, String excludeUnit) {

    addUnitIfNotSelected(
        containerRegionalLocalized, R.string.unit_kora, areaSqFt, excludeUnit, "%.3f");
    addUnitIfNotSelected(
        containerRegionalLocalized, R.string.unit_joistho, areaSqFt, excludeUnit, "%.3f");
    addUnitIfNotSelected(
        containerRegionalLocalized, R.string.unit_kani, areaSqFt, excludeUnit, "%.3f");
  }

  /**
   * Add unit if not selected.
   *
   * @param container the container
   * @param unitResId the unitResId
   * @param areaSqFt the areaSqFt
   * @param excludeUnit the excludeUnit
   * @param format the format
   */
  private void addUnitIfNotSelected(
      ViewGroup container, int unitResId, double areaSqFt, String excludeUnit, String format) {
    String unitName = context.getString(unitResId);
    if (unitName.equals(excludeUnit)) return;

    double converted = UnitConverter.convertArea(areaSqFt, R.string.unit_sqft, unitResId);
    addUnitRow(container, unitName, String.format(format, converted));
  }

  // ================== Share Logic ==================
  /**
   * Build shareable text.
   *
   * @param areaSqFt the areaSqFt
   * @param title the title
   * @return the result of the operation
   */
  public String buildShareableText(double areaSqFt, String title) {
    saveToHistory(areaSqFt, title);

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

  /**
   * Save to history.
   *
   * @param areaSqFt the areaSqFt
   * @param inputs the inputs
   */
  private void saveToHistory(double areaSqFt, String inputs) {
    if (context instanceof com.shamim.landmeasurement.history.HistoryItemSupport) {
      if (context instanceof android.app.Activity) {
        if (((android.app.Activity) context)
            .getIntent()
            .getBooleanExtra("skip_history_save", false)) {
          return;
        }
      }
      String tempTitle = null;
      if (context instanceof TriangularLandActivityHeight) {
        tempTitle =
            context.getString(R.string.item_triangular)
                + " ("
                + context.getString(R.string.item_rectangular_height_based)
                + ")";
      } else if (context instanceof TriangularLandActivityArm) {
        tempTitle =
            context.getString(R.string.item_triangular)
                + " ("
                + context.getString(R.string.item_rectangular_arm_based)
                + ")";
      } else if (context instanceof RectangularLandActivity) {
        tempTitle =
            context.getString(R.string.item_quadrilateral)
                + " ("
                + context.getString(R.string.item_rectangular)
                + ")";
      } else if (context instanceof SquareLandActivity) {
        tempTitle =
            context.getString(R.string.item_quadrilateral)
                + " ("
                + context.getString(R.string.item_square)
                + ")";
      } else if (context instanceof ScaleneLandActivity) {
        tempTitle =
            context.getString(R.string.item_quadrilateral)
                + " ("
                + context.getString(R.string.item_scalene)
                + ")";
      } else if (context instanceof CircularLandActivity) {
        tempTitle = context.getString(R.string.item_circular);
      } else if (context instanceof AreaUnitsConversionActivity) {
        tempTitle = context.getString(R.string.history_title_area_conversion);
      } else if (context instanceof androidx.appcompat.app.AppCompatActivity) {
        androidx.appcompat.app.ActionBar actionBar =
            ((androidx.appcompat.app.AppCompatActivity) context).getSupportActionBar();
        if (actionBar != null && actionBar.getTitle() != null) {
          tempTitle = actionBar.getTitle().toString();
        }
      }
      if (tempTitle == null && context instanceof android.app.Activity) {
        CharSequence title = ((android.app.Activity) context).getTitle();
        if (title != null) {
          tempTitle = title.toString();
        }
      }
      if (tempTitle == null || tempTitle.isEmpty()) {
        tempTitle = "Land Measurement";
      }
      final String shapeTitle = tempTitle;

      final String className = context.getClass().getName();
      final String serialized =
          ((com.shamim.landmeasurement.history.HistoryItemSupport) context).getSerializedInputs();

      new Thread(
              () -> {
                try {
                  HistoryEntry entry =
                      new HistoryEntry(
                          shapeTitle,
                          inputs,
                          areaSqFt,
                          System.currentTimeMillis(),
                          className,
                          serialized);
                  HistoryDatabase.getDatabase(context).historyDao().insert(entry);
                } catch (Exception e) {
                  android.util.Log.e("LandResultManager", "Error saving history", e);
                }
              })
          .start();
    }
  }

  /**
   * Append international section.
   *
   * @param sb the sb
   * @param areaSqFt the areaSqFt
   */
  private void appendInternationalSection(StringBuilder sb, double areaSqFt) {
    sb.append(getString(R.string.result_item_international)).append(":\n");
    appendUnitToShare(sb, R.string.unit_sqm, areaSqFt, "%.4f");
    appendUnitToShare(sb, R.string.unit_acre, areaSqFt, "%.4f");
    appendUnitToShare(sb, R.string.unit_hectare, areaSqFt, "%.4f");
    sb.append("\n");
  }

  /**
   * Append regional standard section.
   *
   * @param sb the sb
   * @param areaSqFt the areaSqFt
   */
  private void appendRegionalStandardSection(StringBuilder sb, double areaSqFt) {
    sb.append(getString(R.string.result_item_regional_standard)).append(":\n");
    appendUnitToShare(sb, R.string.unit_shotok, areaSqFt, "%.3f");
    appendUnitToShare(sb, R.string.unit_katha, areaSqFt, "%.3f");
    appendUnitToShare(sb, R.string.unit_bigha, areaSqFt, "%.3f");
    sb.append("\n");
  }

  /**
   * Append regional localized section.
   *
   * @param sb the sb
   * @param areaSqFt the areaSqFt
   */
  private void appendRegionalLocalizedSection(StringBuilder sb, double areaSqFt) {
    sb.append(getString(R.string.result_item_regional_localized)).append(":\n");

    appendUnitToShare(sb, R.string.unit_kora, areaSqFt, "%.3f");
    appendUnitToShare(sb, R.string.unit_joistho, areaSqFt, "%.3f");
    appendUnitToShare(sb, R.string.unit_kani, areaSqFt, "%.3f");
    sb.append("\n");
  }

  /**
   * Append unit to share.
   *
   * @param sb the sb
   * @param unitResId the unitResId
   * @param areaSqFt the areaSqFt
   * @param format the format
   */
  private void appendUnitToShare(StringBuilder sb, int unitResId, double areaSqFt, String format) {
    double value = UnitConverter.convertArea(areaSqFt, R.string.unit_sqft, unitResId);
    sb.append("• ")
        .append(getString(unitResId))
        .append(" : ")
        .append(String.format(format, value))
        .append("\n");
  }

  // ================== Helper Methods ==================
  /**
   * Add unit row.
   *
   * @param parent the parent
   * @param label the label
   * @param value the value
   */
  private void addUnitRow(ViewGroup parent, String label, String value) {
    View row = LayoutInflater.from(context).inflate(R.layout.item_unit_row, parent, false);
    MaterialTextView tvLabel = row.findViewById(R.id.tv_unit_name);
    MaterialTextView tvValue = row.findViewById(R.id.tv_unit_value);
    tvLabel.setText(label);
    tvValue.setText(value);
    parent.addView(row);
  }

  /**
   * Set last shared text.
   *
   * @param text the text
   */
  public void setLastSharedText(String text) {
    this.lastSharedText = text;
  }

  /** Close keyboard. */
  public void closeKeyboard() {
    if (context == null || rootView == null) return;
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }
  }

  /**
   * Get string.
   *
   * @param resId the resId
   * @return the result of the operation
   */
  private String getString(int resId) {
    return context.getString(resId);
  }
}
