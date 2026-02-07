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

public class QuadrilateralItemsActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_quadrilateral_items);

    MaterialToolbar toolbar = findViewById(R.id.topAppBar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getString(R.string.item_quadrilateral));
    }

    RecyclerView recyclerView = findViewById(R.id.quadrilateral_items);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    List<ItemModel> items = new ArrayList<>();

    items.add(
        new ItemModel(1, R.drawable.rectangle_24px, getString(R.string.item_rectangular), null));
    items.add(new ItemModel(2, R.drawable.rectangle_24px, getString(R.string.item_square), null));
    items.add(new ItemModel(3, R.drawable.rectangle_24px, getString(R.string.item_scalene), null));

    ItemAdapter adapter =
        new ItemAdapter(
            items,
            id -> {
              if (id == 1) {
                startActivity(new Intent(this, RectangularLandActivity.class));
              } else if (id == 3) {
                startActivity(new Intent(this, ScaleneLandActivity.class));
              } else if (id == 2) {
                startActivity(new Intent(this, SquareLandActivity.class));
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
