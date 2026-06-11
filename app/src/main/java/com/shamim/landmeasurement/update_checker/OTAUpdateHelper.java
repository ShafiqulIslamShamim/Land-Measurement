package com.shamim.landmeasurement.update_checker;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class OTAUpdateHelper {

  public static void hookPreference(Context context) {
    checkForUpdatesFromPlayStore(context);
  }

  public static void checkForUpdatesIfDue(Context context) {
    // Left as a non-intrusive no-op to support existing callers in MainActivity without breaking the contract
  }

  public static void checkForUpdatesFromPlayStore(Context context) {
    String packageName = context.getPackageName();
    Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
    playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    try {
      context.startActivity(playStoreIntent);
    } catch (ActivityNotFoundException e) {
      Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
      webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      try {
        context.startActivity(webIntent);
      } catch (Exception ex) {
        Toast.makeText(context, "Play Store is not available.", Toast.LENGTH_SHORT).show();
      }
    }
  }
}
