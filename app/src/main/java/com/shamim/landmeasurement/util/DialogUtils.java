package com.shamim.landmeasurement.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.preference.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;

public class DialogUtils {

  public static TextView createStyledDialogTitle(Context context, CharSequence titleText) {
    TextView customTitle = new TextView(context);
    customTitle.setTextAppearance(
        com.google.android.material.R.style.TextAppearance_Material3_TitleLarge);
    customTitle.setText(titleText);

    // Full width for alignment with dialog content
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    customTitle.setLayoutParams(params);

    // Balanced padding (top > bottom)
    int padding =
        (int)
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24, context.getResources().getDisplayMetrics());
    customTitle.setPadding(padding, padding, padding, padding / 3);

    customTitle.setTextAlignment(TextView.TEXT_ALIGNMENT_VIEW_START);
    customTitle.setIncludeFontPadding(false); // important
    customTitle.setLineSpacing(0, 1f); // optional

    // Apply colorPrimary
    TypedValue typedValue = new TypedValue();
    if (context
        .getTheme()
        .resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)) {
      customTitle.setTextColor(typedValue.data);
    }

    return customTitle;
  }
}
