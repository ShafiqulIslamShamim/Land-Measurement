package com.shamim.landmeasurement.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.shamim.landmeasurement.R;
import java.util.ArrayList;
import java.util.List;

public class LandResultManager {

  private final Context context;
  private final MaterialButton btnCalculate;
  private final MaterialCardView cardResult;

  private final ViewGroup containerInternational;
  private final ViewGroup containerRegionalStandard;
  private final ViewGroup containerRegionalLocalized;

  private OnCalculateClickListener listener;
  private final View section;

  public interface OnCalculateClickListener {
    void onCalculateClicked();
  }

  public LandResultManager(@NonNull View rootView, Context context) {
    this.context = context;

    this.section = rootView.findViewById(R.id.result_section);
    if (section == null) {
      throw new IllegalStateException(
          "Result section not found. Make sure the included layout has id='result_section'");
    }

    this.btnCalculate = section.findViewById(R.id.btn_calculate);
    this.cardResult = section.findViewById(R.id.card_result);

    this.containerInternational = section.findViewById(R.id.container_international_units);
    this.containerRegionalStandard = section.findViewById(R.id.container_regional_standard_units);
    this.containerRegionalLocalized = section.findViewById(R.id.container_regional_localized_units);

    if (containerInternational == null
        || containerRegionalStandard == null
        || containerRegionalLocalized == null) {
      throw new IllegalStateException("One or more result containers not found in layout");
    }
  }

  public View sectionLayout() {
    return section;
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

  public void showResult(double areaSqFt, String selectedUnit) {
    // Clear all containers first
    containerInternational.removeAllViews();
    containerRegionalStandard.removeAllViews();
    containerRegionalLocalized.removeAllViews();

    // ─────────────────────────────────────────────
    // International / Common units
    // ─────────────────────────────────────────────
    List<UnitValue> international = new ArrayList<>();
    international.add(new UnitValue(context.getString(R.string.unit_sqft), areaSqFt));
    international.add(new UnitValue(context.getString(R.string.unit_sqm), areaSqFt / 10.7639));
    international.add(new UnitValue(context.getString(R.string.unit_acre), areaSqFt / 43560.0));
    international.add(new UnitValue(context.getString(R.string.unit_hectare), areaSqFt / 107639.0));

    for (UnitValue uv : international) {
      if (!uv.unit.equals(selectedUnit)) {
        addUnitRow(containerInternational, uv.unit, String.format("%.4f", uv.value));
      }
    }

    // ─────────────────────────────────────────────
    // Regional Standard units ─────────────────────────────────────────────
    List<UnitValue> regionalStandard = new ArrayList<>();

    regionalStandard.add(new UnitValue(context.getString(R.string.unit_shotok), areaSqFt / 435.6));
    regionalStandard.add(new UnitValue(context.getString(R.string.unit_katha), areaSqFt / 720.0));
    regionalStandard.add(new UnitValue(context.getString(R.string.unit_bigha), areaSqFt / 14400.0));

    for (UnitValue uv : regionalStandard) {
      if (!uv.unit.equals(selectedUnit)) {
        addUnitRow(containerRegionalStandard, uv.unit, String.format("%.3f", uv.value));
      }
    }

    // ─────────────────────────────────────────────
    // Regional Localized units ─────────────────────────────────────────────
    List<UnitValue> regionalLocalized = new ArrayList<>();

    double shotok = areaSqFt / 435.6;
    double kora = shotok * 2;
    double joistho = kora * 10;
    double kani = kora * 80;

    regionalLocalized.add(new UnitValue(context.getString(R.string.unit_kora), kora));
    regionalLocalized.add(new UnitValue(context.getString(R.string.unit_joistho), joistho));
    regionalLocalized.add(new UnitValue(context.getString(R.string.unit_kani), kani));

    for (UnitValue uv : regionalLocalized) {
      if (!uv.unit.equals(selectedUnit)) {
        addUnitRow(containerRegionalLocalized, uv.unit, String.format("%.3f", uv.value));
      }
    }

    cardResult.setVisibility(View.VISIBLE);
  }

  public void hideResult() {
    cardResult.setVisibility(View.GONE);
  }

  private void addUnitRow(ViewGroup parent, String label, String value) {
    View row = LayoutInflater.from(context).inflate(R.layout.item_unit_row, parent, false);
    MaterialTextView tvLabel = row.findViewById(R.id.tv_unit_name);
    MaterialTextView tvValue = row.findViewById(R.id.tv_unit_value);
    tvLabel.setText(label);
    tvValue.setText(value);
    parent.addView(row);
  }

  private static class UnitValue {
    String unit;
    double value;

    UnitValue(String unit, double value) {
      this.unit = unit;
      this.value = value;
    }
  }
}
