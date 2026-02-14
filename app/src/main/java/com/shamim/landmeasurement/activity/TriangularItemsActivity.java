package com.shamim.landmeasurement.activity;

import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.preference.*;
import com.shamim.landmeasurement.recycle_view.adapter.*;
import com.shamim.landmeasurement.recycle_view.model.*;
import com.shamim.landmeasurement.update_checker.*;
import com.shamim.landmeasurement.util.*;
import java.util.ArrayList;
import java.util.List;

public class TriangularItemsActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_triangular_items);

    MaterialToolbar toolbar = findViewById(R.id.topAppBar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getString(R.string.item_triangular));
    }

    RecyclerView recyclerView = findViewById(R.id.triangular_items);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    List<ItemModel> items = new ArrayList<>();

    items.add(
        new ItemModel(
            1,
            R.drawable.change_history_24px,
            getString(R.string.item_rectangular_arm_based),
            null));
    items.add(
        new ItemModel(
            2,
            R.drawable.change_history_24px,
            getString(R.string.item_rectangular_height_based),
            null));

    ItemAdapter adapter =
        new ItemAdapter(
            items,
            id -> {
              if (id == 1) {
                startActivity(new Intent(this, TriangularLandActivityArm.class));
              } else if (id == 2) {
                startActivity(new Intent(this, TriangularLandActivityHeight.class));
              }
            });

    recyclerView.setAdapter(adapter);
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
