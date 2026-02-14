package com.shamim.landmeasurement.activity;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.*;
import com.shamim.landmeasurement.view.*;

public class TriangularLandActivityHeight extends BaseActivity {

  private LandResultManager resultManager;
  private LandSingleEditTextCardView baseInput;
  private LandSingleEditTextCardView heightInput;
  private ScrollView scrollView;
  private MaterialToolbar toolbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_triangular_land_height);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getString(R.string.item_rectangular_height_based));
    }

    baseInput = findViewById(R.id.base_input);
    heightInput = findViewById(R.id.height_input);
    scrollView = findViewById(R.id.scroll_view);

    baseInput.setTitle(getString(R.string.card_base_title));
    heightInput.setTitle(getString(R.string.card_height_title));

    resultManager = new LandResultManager(findViewById(android.R.id.content), this);
    resultManager.setOnCalculateClickListener(this::calculate);
  }

  private void calculate() {
    double Base = baseInput.getValueInFeet();
    double Height = heightInput.getValueInFeet();

    if (!baseInput.hasValidInput() || !heightInput.hasValidInput()) {
      Toast.makeText(
              this, getString(R.string.item_triangular_height_input_error), Toast.LENGTH_LONG)
          .show();
      resultManager.hideResult();
      return;
    }

    double areaSqFt = 0.5 * Base * Height;

    resultManager.showResult(areaSqFt, "");

    // Scroll to bottom
    scrollView.postDelayed(
        () -> {
          int bottom = scrollView.getChildAt(0).getBottom();
          scrollView.smoothScrollTo(0, bottom);
        },
        150);
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
