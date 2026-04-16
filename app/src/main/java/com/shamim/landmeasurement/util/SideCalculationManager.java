package com.shamim.landmeasurement.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.recycle_view.NewsAdapter;
import java.io.File;
import java.io.FileWriter;
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
    if (lastSharedText.isEmpty()) {
      Toast.makeText(context, R.string.no_result_to_share, Toast.LENGTH_SHORT).show();
      return;
    }

    String[] titles = {
      context.getString(R.string.share_as_text), context.getString(R.string.share_as_file)
    };
    int[] icons = {R.drawable.text_ad_24px, R.drawable.file_export_24px};

    NewsAdapter adapter =
        new NewsAdapter(
            titles,
            icons,
            position -> {
              if (position == 0) shareAsPlainText();
              else if (position == 1) shareAsTextFile();
            });

    androidx.recyclerview.widget.RecyclerView recyclerView =
        new androidx.recyclerview.widget.RecyclerView(context);
    recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(context));
    recyclerView.setAdapter(adapter);
    recyclerView.setPadding(30, 30, 30, 30);

    new MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.share_choose_option))
        .setView(recyclerView)
        .setPositiveButton(R.string.close, null)
        .show();
  }

  public void shareAsPlainText() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_title));
    shareIntent.putExtra(Intent.EXTRA_TEXT, lastSharedText);
    context.startActivity(
        Intent.createChooser(shareIntent, context.getString(R.string.share_title)));
  }

  public void shareAsTextFile() {
    try {
      File file = new File(context.getCacheDir(), "side_measurement_result.txt");
      try (FileWriter writer = new FileWriter(file)) {
        writer.write(lastSharedText);
      }

      Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.setType("text/plain");
      shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
      shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

      context.startActivity(
          Intent.createChooser(shareIntent, context.getString(R.string.share_title)));
    } catch (Exception e) {
      Toast.makeText(
              context,
              context.getString(R.string.error_cannot_create_file) + ": " + e.getMessage(),
              Toast.LENGTH_SHORT)
          .show();
    }
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
