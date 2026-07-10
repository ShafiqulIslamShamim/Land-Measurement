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
import android.content.Intent;
import android.net.Uri;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.preference.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;

public class IntentUtils {
  /**
   * Open url.
   *
   * @param context the context
   * @param url the url
   * @return the result of the operation
   */
  public static Intent openUrl(Context context, String url) {
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    return intent;
  }
}
