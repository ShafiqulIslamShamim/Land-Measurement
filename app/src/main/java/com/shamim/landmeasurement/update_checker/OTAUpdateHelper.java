package com.shamim.landmeasurement.update_checker;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.tasks.Task;

public class OTAUpdateHelper {

  private static final int UPDATE_REQUEST_CODE = 9911;

  public static void hookPreference(Context context) {
    checkForUpdatesFromPlayStoreCore(context);
  }

  public static void checkForUpdatesIfDue(Context context) {
    // Left as a non-intrusive no-op to support existing callers in MainActivity without breaking the contract
  }

  public static void checkForUpdatesFromPlayStoreCore(Context context) {
    Toast.makeText(context, "Checking for updates...", Toast.LENGTH_SHORT).show();
    
    AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
    Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

    appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
      if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
        if (context instanceof Activity && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
          try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                (Activity) context,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                UPDATE_REQUEST_CODE);
          } catch (Exception e) {
            // Fallback to standard Play Store intent
            launchPlayStoreIntent(context);
          }
        } else {
          launchPlayStoreIntent(context);
        }
      } else {
        Toast.makeText(context, "App is up to date!", Toast.LENGTH_SHORT).show();
      }
    });

    appUpdateInfoTask.addOnFailureListener(e -> {
      // Sideloaded build, emulator, or no Play store - open play store page directly as fallback
      launchPlayStoreIntent(context);
    });
  }

  public static void launchPlayStoreIntent(Context context) {
    String packageName = context.getPackageName();
    Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
    playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
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
