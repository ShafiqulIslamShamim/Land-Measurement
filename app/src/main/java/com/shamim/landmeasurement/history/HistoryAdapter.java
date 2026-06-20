package com.shamim.landmeasurement.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shamim.landmeasurement.R;
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

  public HistoryAdapter(List<HistoryEntry> list, OnHistoryItemClickListener listener) {
    this.list = list;
    this.listener = listener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_card, parent, false);
    return new ViewHolder(view);
  }

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
    if (entry.getActivityClassName() != null
        && entry.getActivityClassName().contains("LinearUnitsConversionActivity")) {
      holder.tvPrimaryArea.setVisibility(View.GONE);
    } else {
      holder.tvPrimaryArea.setVisibility(View.VISIBLE);
      String areaLabel = context.getString(R.string.share_area_label);
      String sqFtSuffix = context.getString(R.string.unit_sqft);
      String formattedArea = String.format("%.2f", entry.getAreaSqFt());
      holder.tvPrimaryArea.setText(areaLabel + " " + formattedArea + " " + sqFtSuffix);
    }

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

  @Override
  public int getItemCount() {
    return list.size();
  }

  public static String getLocalizedTitle(Context context, HistoryEntry entry) {
    String className = entry.getActivityClassName();
    if (className == null) {
      return entry.getShapeTitle(); // Fallback
    }
    if (className.contains("TriangularLandActivityHeight")) {
      return context.getString(R.string.item_triangular)
          + " ("
          + context.getString(R.string.item_rectangular_height_based)
          + ")";
    } else if (className.contains("TriangularLandActivityArm")) {
      return context.getString(R.string.item_triangular)
          + " ("
          + context.getString(R.string.item_rectangular_arm_based)
          + ")";
    } else if (className.contains("RectangularLandActivity")) {
      return context.getString(R.string.item_quadrilateral)
          + " ("
          + context.getString(R.string.item_rectangular)
          + ")";
    } else if (className.contains("SquareLandActivity")) {
      return context.getString(R.string.item_quadrilateral)
          + " ("
          + context.getString(R.string.item_square)
          + ")";
    } else if (className.contains("ScaleneLandActivity")) {
      return context.getString(R.string.item_quadrilateral)
          + " ("
          + context.getString(R.string.item_scalene)
          + ")";
    } else if (className.contains("CircularLandActivity")) {
      return context.getString(R.string.item_circular);
    } else if (className.contains("AreaUnitsConversionActivity")) {
      return context.getString(R.string.history_title_area_conversion);
    } else if (className.contains("LinearUnitsConversionActivity")) {
      return context.getString(R.string.history_title_linear_conversion);
    } else if (className.contains("TriangleSideCalculatorActivity")) {
      return context.getString(R.string.header_side_calculation)
          + " ("
          + context.getString(R.string.item_triangular)
          + ")";
    } else if (className.contains("RectangleSideCalculatorActivity")) {
      return context.getString(R.string.header_side_calculation)
          + " ("
          + context.getString(R.string.item_quadrilateral)
          + " - "
          + context.getString(R.string.item_rectangular)
          + ")";
    } else if (className.contains("CircleSideCalculatorActivity")) {
      return context.getString(R.string.header_side_calculation)
          + " ("
          + context.getString(R.string.item_circular)
          + ")";
    }
    return entry.getShapeTitle();
  }

  private static double parseDoubleOrZero(String s) {
    if (s == null || s.trim().isEmpty()) return 0.0;
    try {
      return Double.parseDouble(s.trim());
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

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
            + context.getString(R.string.unit_foot)
            + " "
            + String.format("%.2f", inchVal)
            + " "
            + context.getString(R.string.unit_inch);
      } else {
        return String.format("%.2f", mainVal) + " " + context.getString(unitResId);
      }
    }
    return "";
  }

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
      String isFeetStr = context.getString(unitResId);

      String firstFormatted;
      String secondFormatted;
      if (unitResId == R.string.unit_foot) {
        firstFormatted =
            String.format("%.2f", firstVal)
                + " "
                + context.getString(R.string.unit_foot)
                + " "
                + String.format("%.2f", firstIn)
                + " "
                + context.getString(R.string.unit_inch);

        secondFormatted =
            String.format("%.2f", secondVal)
                + " "
                + context.getString(R.string.unit_foot)
                + " "
                + String.format("%.2f", secondIn)
                + " "
                + context.getString(R.string.unit_inch);
      } else {
        firstFormatted = String.format("%.2f", firstVal) + " " + isFeetStr;
        secondFormatted = String.format("%.2f", secondVal) + " " + isFeetStr;
      }

      return String.format(format1, title, isFeetStr)
          + " : "
          + firstFormatted
          + "\n"
          + String.format(format2, title, isFeetStr)
          + " : "
          + secondFormatted;
    }
    return "";
  }

  public static String getLocalizedSubtitle(Context context, HistoryEntry entry) {
    String className = entry.getActivityClassName();
    String data = entry.getSerializedInputs();

    if (className == null || data == null || data.isEmpty()) {
      return entry.getInputs() != null ? entry.getInputs() : "";
    }

    try {
      if (className.contains("SquareLandActivity")) {
        return context.getString(R.string.card_length_title)
            + " : "
            + getSingleCardString(context, data);
      }

      if (className.contains("CircularLandActivity")) {
        return context.getString(R.string.card_radius_title)
            + " : "
            + getSingleCardString(context, data);
      }

      if (className.contains("RectangularLandActivity")) {
        String[] parts = data.split(";");
        if (parts.length >= 2) {
          return context.getString(R.string.card_length_title)
              + " : "
              + getSingleCardString(context, parts[0])
              + "\n"
              + context.getString(R.string.card_width_title)
              + " : "
              + getSingleCardString(context, parts[1]);
        }
      }

      if (className.contains("ScaleneLandActivity")) {
        String[] parts = data.split(";");
        if (parts.length >= 2) {
          return getTwoCardString(context, context.getString(R.string.card_length_title), parts[0])
              + "\n"
              + getTwoCardString(context, context.getString(R.string.card_width_title), parts[1]);
        }
      }

      if (className.contains("TriangularLandActivityHeight")) {
        String[] parts = data.split(";");
        if (parts.length >= 2) {
          return context.getString(R.string.card_base_title)
              + " : "
              + getSingleCardString(context, parts[0])
              + "\n"
              + context.getString(R.string.card_height_title)
              + " : "
              + getSingleCardString(context, parts[1]);
        }
      }

      if (className.contains("TriangularLandActivityArm")) {
        String[] parts = data.split(";");
        if (parts.length >= 3) {
          return context.getString(R.string.first_arm_title)
              + " : "
              + getSingleCardString(context, parts[0])
              + "\n"
              + context.getString(R.string.second_arm_title)
              + " : "
              + getSingleCardString(context, parts[1])
              + "\n"
              + context.getString(R.string.third_arm_title)
              + " : "
              + getSingleCardString(context, parts[2]);
        }
      }

      if (className.contains("AreaUnitsConversionActivity")) {
        String[] parts = data.split(";");
        if (parts.length >= 2) {
          double val = parseDoubleOrZero(parts[0]);
          int unitResId = R.string.unit_sqft;
          try {
            unitResId = Integer.parseInt(parts[1]);
          } catch (NumberFormatException e) {
            // ignore
          }
          return context.getString(R.string.amount_of_land_title)
              + " : "
              + val
              + " "
              + context.getString(unitResId);
        }
      }

      if (className.contains("LinearUnitsConversionActivity")) {
        String[] parts = data.split(";");
        if (parts.length >= 3) {
          int unitResId = R.string.unit_foot;
          try {
            unitResId = Integer.parseInt(parts[2]);
          } catch (NumberFormatException e) {
            // ignore
          }
          StringBuilder sb = new StringBuilder();
          sb.append(context.getString(R.string.label_convert_length)).append(": ");
          if (unitResId == R.string.unit_foot) {
            double mainFt = parseDoubleOrZero(parts[0]);
            double inch = parseDoubleOrZero(parts[1]);
            sb.append(String.format("%.2f", mainFt))
                .append(" ")
                .append(context.getString(R.string.unit_foot))
                .append(" ")
                .append(String.format("%.2f", inch))
                .append(" ")
                .append(context.getString(R.string.unit_inch));
          } else {
            double mainVal = parseDoubleOrZero(parts[0]);
            sb.append(String.format("%.2f", mainVal))
                .append(" ")
                .append(context.getString(unitResId));
          }
          return sb.toString();
        }
      }

      if (className.contains("RectangleSideCalculatorActivity")) {
        String[] parts = data.split(";");
        if (parts.length >= 3) {
          double areaVal = parseDoubleOrZero(parts[0]);
          int unitResId = R.string.unit_sqft;
          try {
            unitResId = Integer.parseInt(parts[1]);
          } catch (NumberFormatException ignored) {
          }
          String areaStr = String.format("%.2f", areaVal) + " " + context.getString(unitResId);
          return context.getString(R.string.amount_of_land_title)
              + ": "
              + areaStr
              + "\n"
              + context.getString(R.string.first_arm_title)
              + ": "
              + getSingleCardString(context, parts[2]);
        }
      }

      if (className.contains("CircleSideCalculatorActivity")) {
        String[] parts = data.split(";");
        if (parts.length >= 2) {
          double areaVal = parseDoubleOrZero(parts[0]);
          int unitResId = R.string.unit_sqft;
          try {
            unitResId = Integer.parseInt(parts[1]);
          } catch (NumberFormatException ignored) {
          }
          String areaStr = String.format("%.2f", areaVal) + " " + context.getString(unitResId);
          return context.getString(R.string.amount_of_land_title) + ": " + areaStr;
        }
      }

      if (className.contains("TriangleSideCalculatorActivity")) {
        String[] parts = data.split(";");
        if (parts.length >= 3) {
          double areaVal = parseDoubleOrZero(parts[0]);
          int unitResId = R.string.unit_sqft;
          try {
            unitResId = Integer.parseInt(parts[1]);
          } catch (NumberFormatException ignored) {
          }
          String areaStr = String.format("%.2f", areaVal) + " " + context.getString(unitResId);
          return context.getString(R.string.amount_of_land_title)
              + ": "
              + areaStr
              + "\n"
              + context.getString(R.string.unknown_side_title)
              + ": "
              + getSingleCardString(context, parts[2]);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return entry.getInputs() != null ? entry.getInputs() : "";
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    final TextView tvTitle;
    final TextView tvDate;
    final TextView tvSubtitle;
    final TextView tvPrimaryArea;
    final ImageButton btnDelete;

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
