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
package com.shamim.landmeasurement.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.UnitConverter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

  private final List<HistoryEntry> list;
  private final OnHistoryItemClickListener listener;

  public interface OnHistoryItemClickListener {
    void onDeleteClick(HistoryEntry entry);

    void onItemClick(HistoryEntry entry);
  }

  /**
   * History adapter.
   *
   * @param list the list
   * @param listener the listener
   */
  public HistoryAdapter(List<HistoryEntry> list, OnHistoryItemClickListener listener) {
    this.list = list;
    this.listener = listener;
  }

  /**
   * On create view holder.
   *
   * @param parent the parent
   * @param viewType the viewType
   * @return the result of the operation
   */
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_card, parent, false);
    return new ViewHolder(view);
  }

  /**
   * On bind view holder.
   *
   * @param holder the holder
   * @param position the position
   */
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    HistoryEntry entry = list.get(position);
    Context context = holder.itemView.getContext();

    holder.tvTitle.setText(getLocalizedTitle(context, entry));

    // Localized Date & Time
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
    holder.tvDate.setText(df.format(new Date(entry.getTimestamp())));

    // Display inputs as beautiful multiline text
    holder.tvSubtitle.setText(getLocalizedSubtitle(context, entry).trim());

    // Primary Area representation
    holder.tvPrimaryArea.setVisibility(View.VISIBLE);
    holder.tvPrimaryArea.setText(getLocalizedPrimaryArea(context, entry));

    // Click on entire card (excluding separate interactive parts) to load the shape calculation
    holder.itemView.setOnClickListener(
        v -> {
          if (listener != null) {
            listener.onItemClick(entry);
          }
        });

    // Delete button listener
    holder.btnDelete.setOnClickListener(
        v -> {
          if (listener != null) {
            listener.onDeleteClick(entry);
          }
        });
  }

  /**
   * Get item count.
   *
   * @return the result of the operation
   */
  @Override
  public int getItemCount() {
    return list.size();
  }

  /**
   * Get localized title.
   *
   * @param context the context
   * @param entry the entry
   * @return the result of the operation
   */
  public static String getLocalizedTitle(Context context, HistoryEntry entry) {
    String className = entry.getActivityClassName();
    if (className == null) {
      return entry.getShapeTitle(); // Fallback
    }
    if (className.contains("TriangularLandActivityHeight")) {
      return context.getString(R.string.history_title_triangular_height);
    } else if (className.contains("TriangularLandActivityArm")) {
      return context.getString(R.string.history_title_triangular_side);
    } else if (className.contains("RectangularLandActivity")) {
      return context.getString(R.string.history_title_rectangular);
    } else if (className.contains("SquareLandActivity")) {
      return context.getString(R.string.history_title_square);
    } else if (className.contains("ScaleneLandActivity")) {
      return context.getString(R.string.history_title_scalene);
    } else if (className.contains("CircularLandActivity")) {
      return context.getString(R.string.history_title_circular);
    } else if (className.contains("AreaUnitsConversionActivity")) {
      return context.getString(R.string.history_title_conversion_area);
    } else if (className.contains("LinearUnitsConversionActivity")) {
      return context.getString(R.string.history_title_conversion_linear);
    } else if (className.contains("TriangleSideCalculatorActivity")) {
      return context.getString(R.string.history_title_side_triangular);
    } else if (className.contains("RectangleSideCalculatorActivity")) {
      return context.getString(R.string.history_title_side_rectangular);
    } else if (className.contains("CircleSideCalculatorActivity")) {
      return context.getString(R.string.history_title_side_circular);
    }
    return entry.getShapeTitle();
  }

  /**
   * Parse double or zero.
   *
   * @param s the s
   * @return the result of the operation
   */
  private static double parseDoubleOrZero(String s) {
    if (s == null || s.trim().isEmpty()) return 0.0;
    try {
      return Double.parseDouble(s.trim());
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  /**
   * Get localized unit string.
   *
   * @param context the context
   * @param unitResId the unitResId
   * @return the result of the operation
   */
  private static String getLocalizedUnitString(Context context, int unitResId) {
    if (unitResId == R.string.unit_foot) {
      return context.getString(R.string.history_unit_foot);
    } else if (unitResId == R.string.unit_inch) {
      return context.getString(R.string.history_unit_inch);
    } else if (unitResId == R.string.unit_meter) {
      return context.getString(R.string.history_unit_meter);
    } else if (unitResId == R.string.unit_centimeter) {
      return context.getString(R.string.history_unit_centimeter);
    } else if (unitResId == R.string.unit_millimeter) {
      return context.getString(R.string.history_unit_millimeter);
    } else if (unitResId == R.string.unit_yard) {
      return context.getString(R.string.history_unit_yard);
    } else if (unitResId == R.string.unit_hath) {
      return context.getString(R.string.history_unit_hath);
    } else {
      String name = context.getString(unitResId);
      if (name.isEmpty()) return "";
      return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
  }

  /**
   * Get single card feet value.
   *
   * @param serializedState the serializedState
   * @return the result of the operation
   */
  private static double getSingleCardFeetValue(String serializedState) {
    if (serializedState == null || serializedState.isEmpty()) return 0.0;
    String[] parts = serializedState.split(",", -1);
    if (parts.length >= 3) {
      double mainVal = parseDoubleOrZero(parts[0]);
      double inchVal = parseDoubleOrZero(parts[1]);
      int unitResId = R.string.unit_foot;
      try {
        unitResId = Integer.parseInt(parts[2]);
      } catch (NumberFormatException e) {
        // fallback
      }
      double val = mainVal;
      if (unitResId == R.string.unit_foot) {
        val = mainVal + (inchVal / 12.0);
      }
      return UnitConverter.convertLength(val, unitResId, R.string.unit_foot);
    }
    return 0.0;
  }

  /**
   * Get single card string.
   *
   * @param context the context
   * @param serializedState the serializedState
   * @return the result of the operation
   */
  private static String getSingleCardString(Context context, String serializedState) {
    if (serializedState == null || serializedState.isEmpty()) return "";
    String[] parts = serializedState.split(",", -1);
    if (parts.length >= 3) {
      double mainVal = parseDoubleOrZero(parts[0]);
      double inchVal = parseDoubleOrZero(parts[1]);
      int unitResId = R.string.unit_foot;
      try {
        unitResId = Integer.parseInt(parts[2]);
      } catch (NumberFormatException e) {
        // fallback
      }
      if (unitResId == R.string.unit_foot) {
        return String.format("%.2f", mainVal)
            + " "
            + getLocalizedUnitString(context, R.string.unit_foot)
            + " "
            + String.format("%.2f", inchVal)
            + " "
            + getLocalizedUnitString(context, R.string.unit_inch);
      } else {
        return String.format("%.2f", mainVal) + " " + getLocalizedUnitString(context, unitResId);
      }
    }
    return "";
  }

  /**
   * Get two card string.
   *
   * @param context the context
   * @param title the title
   * @param serializedState the serializedState
   * @return the result of the operation
   */
  private static String getTwoCardString(Context context, String title, String serializedState) {
    if (serializedState == null || serializedState.isEmpty()) return "";
    String[] parts = serializedState.split(",", -1);
    if (parts.length >= 5) {
      double firstVal = parseDoubleOrZero(parts[0]);
      double firstIn = parseDoubleOrZero(parts[1]);
      double secondVal = parseDoubleOrZero(parts[2]);
      double secondIn = parseDoubleOrZero(parts[3]);
      int unitResId = R.string.unit_foot;
      try {
        unitResId = Integer.parseInt(parts[4]);
      } catch (NumberFormatException e) {
        // fallback
      }

      String format1 = context.getString(R.string.et_double_first_hind);
      String format2 = context.getString(R.string.et_double_second_hind);
      String isFeetStr = getLocalizedUnitString(context, unitResId);

      String firstFormatted;
      String secondFormatted;
      if (unitResId == R.string.unit_foot) {
        firstFormatted =
            String.format("%.2f", firstVal)
                + " "
                + getLocalizedUnitString(context, R.string.unit_foot)
                + " "
                + String.format("%.2f", firstIn)
                + " "
                + getLocalizedUnitString(context, R.string.unit_inch);

        secondFormatted =
            String.format("%.2f", secondVal)
                + " "
                + getLocalizedUnitString(context, R.string.unit_foot)
                + " "
                + String.format("%.2f", secondIn)
                + " "
                + getLocalizedUnitString(context, R.string.unit_inch);
      } else {
        firstFormatted = String.format("%.2f", firstVal) + " " + isFeetStr;
        secondFormatted = String.format("%.2f", secondVal) + " " + isFeetStr;
      }

      String sep = context.getString(R.string.history_separator);
      return String.format(format1, title, isFeetStr)
          + sep
          + firstFormatted
          + "\n"
          + String.format(format2, title, isFeetStr)
          + sep
          + secondFormatted;
    }
    return "";
  }

  /**
   * Get localized subtitle.
   *
   * @param context the context
   * @param entry the entry
   * @return the result of the operation
   */
  public static String getLocalizedSubtitle(Context context, HistoryEntry entry) {
    String className = entry.getActivityClassName();
    String data = entry.getSerializedInputs();
    String rawSubtitle = "";

    if (className == null || data == null || data.isEmpty()) {
      rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
    } else {
      String sep = context.getString(R.string.history_separator);

      try {
        if (className.contains("SquareLandActivity")) {
          rawSubtitle =
              context.getString(R.string.card_length_title)
                  + sep
                  + getSingleCardString(context, data);
        } else if (className.contains("CircularLandActivity")) {
          rawSubtitle =
              context.getString(R.string.card_radius_title)
                  + sep
                  + getSingleCardString(context, data);
        } else if (className.contains("RectangularLandActivity")) {
          String[] parts = data.split(";");
          if (parts.length >= 2) {
            rawSubtitle =
                context.getString(R.string.card_length_title)
                    + sep
                    + getSingleCardString(context, parts[0])
                    + "\n"
                    + context.getString(R.string.card_width_title)
                    + sep
                    + getSingleCardString(context, parts[1]);
          } else {
            rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
          }
        } else if (className.contains("ScaleneLandActivity")) {
          String[] parts = data.split(";");
          if (parts.length >= 2) {
            rawSubtitle =
                getTwoCardString(context, context.getString(R.string.card_length_title), parts[0])
                    + "\n"
                    + getTwoCardString(
                        context, context.getString(R.string.card_width_title), parts[1]);
          } else {
            rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
          }
        } else if (className.contains("TriangularLandActivityHeight")) {
          String[] parts = data.split(";");
          if (parts.length >= 2) {
            rawSubtitle =
                context.getString(R.string.card_base_title)
                    + sep
                    + getSingleCardString(context, parts[0])
                    + "\n"
                    + context.getString(R.string.card_height_title)
                    + sep
                    + getSingleCardString(context, parts[1]);
          } else {
            rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
          }
        } else if (className.contains("TriangularLandActivityArm")) {
          String[] parts = data.split(";");
          if (parts.length >= 3) {
            rawSubtitle =
                context.getString(R.string.first_arm_title)
                    + sep
                    + getSingleCardString(context, parts[0])
                    + "\n"
                    + context.getString(R.string.second_arm_title)
                    + sep
                    + getSingleCardString(context, parts[1])
                    + "\n"
                    + context.getString(R.string.third_arm_title)
                    + sep
                    + getSingleCardString(context, parts[2]);
          } else {
            rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
          }
        } else if (className.contains("AreaUnitsConversionActivity")) {
          String[] parts = data.split(";");
          if (parts.length >= 2) {
            double val = parseDoubleOrZero(parts[0]);
            int unitResId = R.string.unit_sqft;
            try {
              unitResId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
              // ignore
            }
            rawSubtitle =
                context.getString(R.string.amount_of_land_title)
                    + sep
                    + val
                    + " "
                    + getLocalizedUnitString(context, unitResId);
          } else {
            rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
          }
        } else if (className.contains("LinearUnitsConversionActivity")) {
          String[] parts = data.split(";");
          if (parts.length >= 3) {
            int unitResId = R.string.unit_foot;
            try {
              unitResId = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
              // ignore
            }
            StringBuilder sb = new StringBuilder();
            sb.append(context.getString(R.string.label_convert_length)).append(sep);
            if (unitResId == R.string.unit_foot) {
              double mainFt = parseDoubleOrZero(parts[0]);
              double inch = parseDoubleOrZero(parts[1]);
              sb.append(String.format("%.2f", mainFt))
                  .append(" ")
                  .append(getLocalizedUnitString(context, R.string.unit_foot))
                  .append(" ")
                  .append(String.format("%.2f", inch))
                  .append(" ")
                  .append(getLocalizedUnitString(context, R.string.unit_inch));
            } else {
              double mainVal = parseDoubleOrZero(parts[0]);
              sb.append(String.format("%.2f", mainVal))
                  .append(" ")
                  .append(getLocalizedUnitString(context, unitResId));
            }
            rawSubtitle = sb.toString();
          } else {
            rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
          }
        } else if (className.contains("RectangleSideCalculatorActivity")) {
          String[] parts = data.split(";");
          if (parts.length >= 3) {
            double areaVal = parseDoubleOrZero(parts[0]);
            int unitResId = R.string.unit_sqft;
            try {
              unitResId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {
            }
            String areaStr =
                String.format("%.2f", areaVal) + " " + getLocalizedUnitString(context, unitResId);
            rawSubtitle =
                context.getString(R.string.amount_of_land_title)
                    + sep
                    + areaStr
                    + "\n"
                    + context.getString(R.string.first_arm_title)
                    + sep
                    + getSingleCardString(context, parts[2]);
          } else {
            rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
          }
        } else if (className.contains("CircleSideCalculatorActivity")) {
          String[] parts = data.split(";");
          if (parts.length >= 2) {
            double areaVal = parseDoubleOrZero(parts[0]);
            int unitResId = R.string.unit_sqft;
            try {
              unitResId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {
            }
            String areaStr =
                String.format("%.2f", areaVal) + " " + getLocalizedUnitString(context, unitResId);
            rawSubtitle = context.getString(R.string.amount_of_land_title) + sep + areaStr;
          } else {
            rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
          }
        } else if (className.contains("TriangleSideCalculatorActivity")) {
          String[] parts = data.split(";");
          if (parts.length >= 3) {
            double areaVal = parseDoubleOrZero(parts[0]);
            int unitResId = R.string.unit_sqft;
            try {
              unitResId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {
            }
            String areaStr =
                String.format("%.2f", areaVal) + " " + getLocalizedUnitString(context, unitResId);
            rawSubtitle =
                context.getString(R.string.amount_of_land_title)
                    + sep
                    + areaStr
                    + "\n"
                    + context.getString(R.string.unknown_side_title)
                    + sep
                    + getSingleCardString(context, parts[2]);
          } else {
            rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
          }
        } else {
          rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
        }
      } catch (Exception e) {
        e.printStackTrace();
        rawSubtitle = entry.getInputs() != null ? entry.getInputs() : "";
      }
    }

    if (rawSubtitle == null) {
      return "";
    }
    return rawSubtitle.replaceAll("\\s*:\\s*", " : ");
  }

  /**
   * Get localized primary area.
   *
   * @param context the context
   * @param entry the entry
   * @return the result of the operation
   */
  public static String getLocalizedPrimaryArea(Context context, HistoryEntry entry) {
    String className = entry.getActivityClassName();
    if (className == null) {
      return "";
    }
    String serialized = entry.getSerializedInputs();

    try {
      if (className.contains("LinearUnitsConversionActivity")) {
        if (serialized != null && !serialized.isEmpty()) {
          String[] parts = serialized.split(";");
          if (parts.length >= 3) {
            double mainVal = parseDoubleOrZero(parts[0]);
            double inchVal = parseDoubleOrZero(parts[1]);
            int unitResId = R.string.unit_foot;
            try {
              unitResId = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
              // fallback
            }
            double valueInSelectedUnit = mainVal;
            if (unitResId == R.string.unit_foot) {
              valueInSelectedUnit = mainVal + (inchVal / 12.0);
            }
            int targetUnitResId;
            if (unitResId == R.string.unit_meter
                || unitResId == R.string.unit_centimeter
                || unitResId == R.string.unit_millimeter) {
              targetUnitResId = R.string.unit_foot;
            } else {
              targetUnitResId = R.string.unit_meter;
            }
            double convertedValue =
                UnitConverter.convertLength(valueInSelectedUnit, unitResId, targetUnitResId);
            String targetUnitName = getLocalizedUnitString(context, targetUnitResId);
            return context.getString(
                R.string.history_primary_converted_length, convertedValue, targetUnitName);
          }
        }
      }

      if (className.contains("RectangleSideCalculatorActivity")) {
        if (serialized != null && !serialized.isEmpty()) {
          String[] parts = serialized.split(";");
          if (parts.length >= 3) {
            double firstSideFeet = getSingleCardFeetValue(parts[2]);
            double areaSqFt = entry.getAreaSqFt();
            if (firstSideFeet > 0) {
              double otherSideVal = areaSqFt / firstSideFeet;
              return context.getString(R.string.history_primary_other_side, otherSideVal);
            }
          }
        }
      }

      if (className.contains("TriangleSideCalculatorActivity")) {
        if (serialized != null && !serialized.isEmpty()) {
          String[] parts = serialized.split(";");
          if (parts.length >= 3) {
            double firstSideFeet = getSingleCardFeetValue(parts[2]);
            double areaSqFt = entry.getAreaSqFt();
            if (firstSideFeet > 0) {
              double otherSideVal = (areaSqFt * 2) / firstSideFeet;
              return context.getString(R.string.history_primary_unknown_side, otherSideVal);
            }
          }
        }
      }

      if (className.contains("CircleSideCalculatorActivity")) {
        double areaSqFt = entry.getAreaSqFt();
        double radius = Math.sqrt(areaSqFt / Math.PI);
        return context.getString(R.string.history_primary_radius, radius);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    // Default: Area representation
    return context.getString(
        R.string.history_primary_area, entry.getAreaSqFt(), context.getString(R.string.unit_sqft));
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    final TextView tvTitle;
    final TextView tvDate;
    final TextView tvSubtitle;
    final TextView tvPrimaryArea;
    final View btnDelete;

    /**
     * View holder.
     *
     * @param itemView the itemView
     */
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      tvTitle = itemView.findViewById(R.id.entry_title);
      tvDate = itemView.findViewById(R.id.entry_date);
      tvSubtitle = itemView.findViewById(R.id.entry_subtitle);
      tvPrimaryArea = itemView.findViewById(R.id.entry_primary_area);
      btnDelete = itemView.findViewById(R.id.btn_delete_entry);
    }
  }
}
