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
package com.shamim.landmeasurement.preference;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;
import com.shamim.landmeasurement.util.*;

public class MaterialListPreference extends ListPreference {
  private int mClickedDialogEntryIndex;

  /**
   * Material list preference.
   *
   * @param context the context
   * @param attrs the attrs
   * @param defStyleAttr the defStyleAttr
   * @param defStyleRes the defStyleRes
   * @return the result of the operation
   */
  public MaterialListPreference(
      @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  /**
   * Material list preference.
   *
   * @param context the context
   * @param attrs the attrs
   */
  public MaterialListPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  /**
   * Material list preference.
   *
   * @param context the context
   */
  public MaterialListPreference(@NonNull Context context) {
    super(context);
  }

  /** On click. */
  @Override
  protected void onClick() {
    // If no entries or not enabled/persisted, don't show dialog
    if (getEntries() == null || getEntryValues() == null || !isEnabled() || !isPersistent()) {
      return;
    }

    // Find the index of current value
    mClickedDialogEntryIndex = findIndexOfValue(getValue());

    // Create Material dialog
    MaterialAlertDialogBuilder builder =
        new MaterialAlertDialogBuilder(getContext())

            // .setTitle(getDialogTitle())
            .setCustomTitle(DialogUtils.createStyledDialogTitle(getContext(), getDialogTitle()))
            .setSingleChoiceItems(
                getEntries(),
                mClickedDialogEntryIndex,
                (dialog, which) -> {
                  mClickedDialogEntryIndex = which;
                  // Update value when item is clicked
                  if (callChangeListener(getEntryValues()[which].toString())) {
                    setValueIndex(which);
                  }
                  dialog.dismiss();
                })
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());

    // Optional: Add positive button if needed
    // builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
    //     if (mClickedDialogEntryIndex >= 0 && getEntryValues() != null) {
    //         String value = getEntryValues()[mClickedDialogEntryIndex].toString();
    //         if (callChangeListener(value)) {
    //             setValue(value);
    //         }
    //     }
    // });

    builder.show();
  }
}
