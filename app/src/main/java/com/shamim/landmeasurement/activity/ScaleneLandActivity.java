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

public class ScaleneLandActivity extends BaseActivity {

  private LandResultManager resultManager;
  private LandTwoEditTextCardView lengthInput;
  private LandTwoEditTextCardView widthInput;
  private ScrollView scrollView;
  private MaterialToolbar toolbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scalene_land);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getString(R.string.item_scalene));
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
    double FirstLength = lengthInput.getFirstValueInFeet();
    double SecondLength = lengthInput.getSecondValueInFeet();
    double FirstWidth = widthInput.getFirstValueInFeet();
    double SecondWidth = widthInput.getSecondValueInFeet();

    if (!lengthInput.hasValidInput(FirstLength)
        || !lengthInput.hasValidInput(SecondLength)
        || !widthInput.hasValidInput(FirstWidth)
        || !widthInput.hasValidInput(SecondWidth)) {
      Toast.makeText(this, getString(R.string.item_scalene_input_error), Toast.LENGTH_LONG).show();
      resultManager.hideResult();
      return;
    }

    double averageLength = (FirstLength + SecondLength) / 2;
    double averageWidth = (FirstWidth + SecondWidth) / 2;

    double areaSqFt = averageLength * averageWidth;

    resultManager.showResultWithScroll(areaSqFt, "", scrollView);

    String shareText = resultManager.buildShareableText(areaSqFt, sharedTextHeading());

    resultManager.setLastSharedText(shareText);
  }

  private String sharedTextHeading() {

    String format1 = getString(R.string.et_double_first_hind);
    String format2 = getString(R.string.et_double_second_hind);

    return String.format(
            format1, getString(R.string.card_length_title), lengthInput.getSelectedUnit())
        + " : "
        + lengthInput.getFirstValueAsString()
        + "\n"
        + String.format(format1, getString(R.string.card_width_title), widthInput.getSelectedUnit())
        + " : "
        + widthInput.getFirstValueAsString()
        + "\n"
        + String.format(
            format2, getString(R.string.card_length_title), lengthInput.getSelectedUnit())
        + " : "
        + lengthInput.getSecondValueAsString()
        + "\n"
        + String.format(format2, getString(R.string.card_width_title), widthInput.getSelectedUnit())
        + " : "
        + widthInput.getSecondValueAsString();
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
