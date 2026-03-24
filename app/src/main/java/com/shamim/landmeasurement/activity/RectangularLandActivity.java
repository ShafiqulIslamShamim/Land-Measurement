package com.shamim.landmeasurement.activity;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.*;
import com.shamim.landmeasurement.view.*;
import java.util.*;

public class RectangularLandActivity extends BaseActivity {

  private LandResultManager resultManager;
  private LandSingleEditTextCardView lengthInput;
  private LandSingleEditTextCardView widthInput;
  private ScrollView scrollView;
  private MaterialToolbar toolbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rectangular_land);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getString(R.string.item_rectangular));
    }

    lengthInput = findViewById(R.id.length_input);
    widthInput = findViewById(R.id.width_input);
    scrollView = findViewById(R.id.scroll_view);

    lengthInput.setTitle(getString(R.string.card_length_title));
    widthInput.setTitle(getString(R.string.card_width_title));

    resultManager = new LandResultManager(findViewById(android.R.id.content), this);
    resultManager.setOnCalculateClickListener(this::calculate);
  }

  private void calculate() {
    double Length = lengthInput.getValueInFeet();
    double Width = widthInput.getValueInFeet();

    if (!lengthInput.hasValidInput() || !widthInput.hasValidInput()) {
      Toast.makeText(this, getString(R.string.item_rectangular_input_error), Toast.LENGTH_LONG)
          .show();
      resultManager.hideResult();
      return;
    }

    double areaSqFt = Length * Width;

    Map<String, Double> InputMap = new LinkedHashMap<>();
    InputMap.put(getString(R.string.card_length_title), Length);
    InputMap.put(getString(R.string.card_width_title), Width);

    resultManager.showResultWithScroll(areaSqFt, "", InputMap, scrollView);
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
