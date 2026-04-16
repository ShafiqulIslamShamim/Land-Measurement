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

public class SquareLandActivity extends BaseActivity {

  private LandResultManager resultManager;
  private LandSingleEditTextCardView lengthInput;
  private ScrollView scrollView;
  private MaterialToolbar toolbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_square_land);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getString(R.string.item_square));
    }

    lengthInput = findViewById(R.id.length_input);
    scrollView = findViewById(R.id.scroll_view);

    lengthInput.setTitle(getString(R.string.card_length_title));

    resultManager = new LandResultManager(findViewById(android.R.id.content), this);
    resultManager.setOnCalculateClickListener(this::calculate);
  }

  private void calculate() {
    double Length = lengthInput.getValueInFeet();

    if (!lengthInput.hasValidInput()) {
      Toast.makeText(this, getString(R.string.item_square_input_error), Toast.LENGTH_LONG).show();
      resultManager.hideResult();
      return;
    }

    double areaSqFt = Math.pow(Length, 2);

    resultManager.showResultWithScroll(areaSqFt, "", scrollView);

    String shareText = resultManager.buildShareableText(areaSqFt, sharedTextHeading());

    resultManager.setLastSharedText(shareText);
  }

  private String sharedTextHeading() {

    return getString(R.string.card_length_title) + " : " + lengthInput.getValueAsString();
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
