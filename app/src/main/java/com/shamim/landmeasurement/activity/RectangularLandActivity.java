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

public class RectangularLandActivity extends BaseActivity
    implements com.shamim.landmeasurement.history.HistoryItemSupport {

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

    String serialized = getIntent().getStringExtra("serialized_inputs");
    if (serialized != null) {
      restoreSerializedInputs(serialized);
    }
  }

  @Override
  public String getSerializedInputs() {
    return lengthInput.getSerializedState() + ";" + widthInput.getSerializedState();
  }

  @Override
  public void restoreSerializedInputs(String data) {
    if (data == null || data.isEmpty()) return;
    String[] parts = data.split(";");
    if (parts.length >= 2) {
      getIntent().putExtra("skip_history_save", true);
      lengthInput.restoreState(parts[0]);
      widthInput.restoreState(parts[1]);
      calculate();
      getIntent().putExtra("skip_history_save", false);
    }
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

    resultManager.showResultWithScroll(areaSqFt, "", scrollView);

    String shareText = resultManager.buildShareableText(areaSqFt, sharedTextHeading());

    resultManager.setLastSharedText(shareText);
  }

  private String sharedTextHeading() {

    return getString(R.string.card_length_title)
        + " : "
        + lengthInput.getValueAsString()
        + "\n"
        + getString(R.string.card_width_title)
        + " : "
        + widthInput.getValueAsString();
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
