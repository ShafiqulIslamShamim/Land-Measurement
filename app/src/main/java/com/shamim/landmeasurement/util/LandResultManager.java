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
  private final ViewGroup containerCommonUnits;
  private final ViewGroup containerRegionalUnits;

  private OnCalculateClickListener listener;

  private final View section;

  public interface OnCalculateClickListener {
    void onCalculateClicked();
  }

  public LandResultManager(@NonNull View rootView, Context context) {
    this.context = context;

    this.section = rootView.findViewById(R.id.result_section);
    if (section == null) {
      throw new IllegalStateException("include_result_section not found");
    }

    this.btnCalculate = section.findViewById(R.id.btn_calculate);
    this.cardResult = section.findViewById(R.id.card_result);
    this.containerCommonUnits = section.findViewById(R.id.container_common_units);
    this.containerRegionalUnits = section.findViewById(R.id.container_regional_units);
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

  public void showResult(double areaSqFt) {

    // Common units
    containerCommonUnits.removeAllViews();
    List<UnitValue> common = new ArrayList<>();
    common.add(new UnitValue(context.getString(R.string.unit_shotok), areaSqFt / 435.6));
    common.add(new UnitValue(context.getString(R.string.unit_sqft), areaSqFt));
    common.add(new UnitValue(context.getString(R.string.unit_sqm), areaSqFt / 10.7639));
    common.add(new UnitValue(context.getString(R.string.unit_katha), areaSqFt / 720.0));
    common.add(new UnitValue(context.getString(R.string.unit_bigha), areaSqFt / 14400.0));
    common.add(new UnitValue(context.getString(R.string.unit_acre), areaSqFt / 43560.0));
    common.add(new UnitValue(context.getString(R.string.unit_hectare), areaSqFt / 107639.0));

    for (UnitValue uv : common) {
      addUnitRow(containerCommonUnits, uv.unit, String.format("%.4f", uv.value));
    }

    // Regional units
    containerRegionalUnits.removeAllViews();
    List<UnitValue> regional = new ArrayList<>();
    double shatak = areaSqFt / 435.6;
    double kora = shatak / 2;
    double joistho = kora / 10;
    double kani = kora / 80;

    regional.add(new UnitValue(context.getString(R.string.unit_kora), kora));
    regional.add(new UnitValue(context.getString(R.string.unit_joistho), joistho));
    regional.add(new UnitValue(context.getString(R.string.unit_kani), kani));

    for (UnitValue uv : regional) {
      addUnitRow(containerRegionalUnits, uv.unit, String.format("%.3f", uv.value));
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
