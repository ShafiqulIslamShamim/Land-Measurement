package com.shamim.landmeasurement.activity;

import android.app.Activity;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.MenuItem;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.exception_catcher.*;
// import com.shamim.landmeasurement.fragment.*;
import com.shamim.landmeasurement.preference.*;
import com.shamim.landmeasurement.recycle_view.adapter.*;
import com.shamim.landmeasurement.recycle_view.model.*;
import com.shamim.landmeasurement.update_checker.*;
import com.shamim.landmeasurement.util.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

  private static final String TAG = "MainActivity";
  public static MainActivity ActivityContext;

  private ActivityResultLauncher<Intent> folderPickerLauncher;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences prefs = getSharedPreferences("intro_pref", MODE_PRIVATE);
    if (!prefs.getBoolean("intro_shown", false)) {
      startActivity(new Intent(this, IntroActivity.class));
      finish();
      return;
    }

    setContentView(R.layout.activity_main);

    ActivityContext = this;
    OTAUpdateHelper.checkForUpdatesIfDue(this);

    boolean logcat = SharedPrefValues.getValue("enable_logcat", false);

    if (logcat) {

      if (StoragePermissionHelper.isPermissionGranted(this)) {
        LogcatSaver.RunLog(this);
      }

      folderPickerLauncher =
          registerForActivityResult(
              new ActivityResultContracts.StartActivityForResult(),
              result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                  Intent data = result.getData();
                  StoragePermissionHelper.handleFolderPickerResult(this, data);
                  if (StoragePermissionHelper.isPermissionGranted(this)) {
                    LogcatSaver.RunLog(this);
                  }
                }
              });

      StoragePermissionHelper.checkAndRequestStoragePermission(this, folderPickerLauncher);
    }

    MaterialToolbar toolbar = findViewById(R.id.topAppBar);
    setSupportActionBar(toolbar);

    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    List<ItemModel> items = new ArrayList<>();

    // Headers
    items.add(new ItemModel(getString(R.string.header_land_shape)));

    // Land Shape Items
    items.add(
        new ItemModel(1, R.drawable.rectangle_24px, getString(R.string.item_quadrilateral), null));
    items.add(new ItemModel(11, R.drawable.circle_24px, getString(R.string.item_circular), null));
    items.add(
        new ItemModel(
            12, R.drawable.change_history_24px, getString(R.string.item_triangular), null));

    // Conversion Items
    items.add(new ItemModel(getString(R.string.header_conversion)));
    items.add(
        new ItemModel(
            2, R.drawable.swap_horizontal_circle_24px, getString(R.string.item_measurement), null));

    ItemAdapter adapter =
        new ItemAdapter(
            items,
            id -> {
              if (id == 1) {
                startActivity(new Intent(this, QuadrilateralItemsActivity.class));
              } else if (id == 11) {
                startActivity(new Intent(this, CircularLandActivity.class));
              } else if (id == 12) {
                startActivity(new Intent(this, TriangularLandActivity.class));
              } else if (id == 2) {
                startActivity(new Intent(this, ConversionActivity.class));
              }
            });

    recyclerView.setAdapter(adapter);
  }

  // === Menu Code (Unchanged) ===
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    int menuId = getResources().getIdentifier("main_menu", "menu", getPackageName());
    if (menuId == 0) {
      Log.e(TAG, "Menu resource 'main_menu' not found");
      return false;
    }
    getMenuInflater().inflate(menuId, menu);

    // Reset
    MenuItem resetItem = menu.findItem(R.id.action_reset);
    View resetView = resetItem.getActionView();
    View resetIcon = resetView.findViewById(R.id.icon_image);
    resetIcon.setOnClickListener(
        v -> {
          Intent intent = new Intent(this, MainActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
          finish();
          startActivity(intent);
        });

    // Settings
    MenuItem settingsItem = menu.findItem(R.id.settings);
    View settingsView = settingsItem.getActionView();
    View settingsIcon = settingsView.findViewById(R.id.icon_image);
    settingsIcon.setOnClickListener(
        v -> {
          startActivity(new Intent(this, SettingsActivity.class));
        });

    return true;
  }
}
