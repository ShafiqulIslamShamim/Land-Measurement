package com.shamim.landmeasurement.activity;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.*;
import com.shamim.landmeasurement.view.*;

public class CircularLandActivity extends BaseActivity {

  private LandResultManager resultManager;
  private LandSingleEditTextCardView radiusInput;
  private ScrollView scrollView;
  private MaterialToolbar toolbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_circular_land);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getString(R.string.item_circular));
    }

    radiusInput = findViewById(R.id.radius_input);
    scrollView = findViewById(R.id.scroll_view);

    radiusInput.setTitle(getString(R.string.card_radius_title));

    resultManager = new LandResultManager(findViewById(android.R.id.content), this);
    resultManager.setOnCalculateClickListener(this::calculate);
  }

  private void calculate() {
    double Radius = radiusInput.getValueInFeet();

    if (!radiusInput.hasValidInput()) {
      Toast.makeText(this, getString(R.string.item_circular_input_error), Toast.LENGTH_LONG).show();
      resultManager.hideResult();
      return;
    }

    double areaSqFt = Math.PI * Math.pow(Radius, 2);
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
