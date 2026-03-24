package com.shamim.landmeasurement.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.preference.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;

public class StoragePermissionHelper {

  public static final String PREF_LOG_FOLDER_URI = "log_folder_uri";

  public static void checkAndRequestStoragePermission(
      final AppCompatActivity activity, ActivityResultLauncher<Intent> folderPickerLauncher) {

    if (Build.VERSION.SDK_INT < 23) return;

    if (Build.VERSION.SDK_INT >= 30) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
      String folderUriStr = prefs.getString(PREF_LOG_FOLDER_URI, null);

      if (folderUriStr == null) {
        showFolderPermissionDialog(activity, folderPickerLauncher);
      }
    } else {
      if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED
          || activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED) {

        activity.requestPermissions(
            new String[] {
              Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            },
            1001);
      }
    }
  }

  private static void showFolderPermissionDialog(
      AppCompatActivity activity, ActivityResultLauncher<Intent> launcher) {

    new MaterialAlertDialogBuilder(activity)
        .setCustomTitle(
            DialogUtils.createStyledDialogTitle(
                activity, activity.getString(R.string.dialog_folder_permission_title)))
        .setMessage(R.string.dialog_folder_permission_message)
        .setPositiveButton(
            R.string.dialog_folder_permission_positive, (d, w) -> openFolderPicker(launcher))
        .setNegativeButton(R.string.dialog_folder_permission_negative, null)
        .show();
  }

  private static void openFolderPicker(ActivityResultLauncher<Intent> launcher) {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    intent.addFlags(
        Intent.FLAG_GRANT_READ_URI_PERMISSION
            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
    launcher.launch(intent);
  }

  public static void handleFolderPickerResult(Activity activity, Intent data) {
    if (data == null) return;

    Uri treeUri = data.getData();
    if (treeUri == null) return;

    activity
        .getContentResolver()
        .takePersistableUriPermission(
            treeUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    prefs.edit().putString(PREF_LOG_FOLDER_URI, treeUri.toString()).apply();
  }

  public static boolean isPermissionGranted(AppCompatActivity activity) {
    if (Build.VERSION.SDK_INT >= 30) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
      return prefs.getString(PREF_LOG_FOLDER_URI, null) != null;
    } else {
      return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
              == PackageManager.PERMISSION_GRANTED
          && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
              == PackageManager.PERMISSION_GRANTED;
    }
  }
}
