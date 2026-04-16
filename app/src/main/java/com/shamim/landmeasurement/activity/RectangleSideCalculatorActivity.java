package com.shamim.landmeasurement.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.SideCalculationManager;
import com.shamim.landmeasurement.view.AreaInputCardView;
import com.shamim.landmeasurement.view.LandSingleEditTextCardView;

public class RectangleSideCalculatorActivity extends BaseActivity {

  private MaterialToolbar toolbar;
  private AreaInputCardView areaInputCard;
  private LandSingleEditTextCardView widthInput;
  private ScrollView scrollView;

  private SideCalculationManager manager;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_base_side);

    manager = new SideCalculationManager(this);

    findViews();
    setupTitle();
    setupToolbar();
    setupListeners();
  }

  private void findViews() {
    toolbar = findViewById(R.id.toolbar);
    scrollView = findViewById(R.id.scroll_view);

    areaInputCard = findViewById(R.id.area_input_card);
    widthInput = findViewById(R.id.width_input);

    View resultSection = findViewById(R.id.result_section);
    manager.setupResultSection(resultSection);
  }

  private void setupTitle() {
    widthInput.setTitle(getString(R.string.first_arm_title));
    manager.setTitle(getString(R.string.other_side_title));
  }

  private void setupToolbar() {
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(R.string.item_quadrilateral);
    }
  }

  private void setupListeners() {

    manager.setCalculateClickListener(this::calculate);
    manager.setShareClickListener(this::showShareOptionsDialog);
  }

  private void calculate() {
    double area = areaInputCard.getAreaValue();
    double oneSide = widthInput.getValueInFeet();

    if (area <= 0 || oneSide <= 0) {
      Toast.makeText(this, R.string.error_invalid_conversion_land_amount, Toast.LENGTH_SHORT)
          .show();
      manager.hideResult();
      return;
    }

    int selectedAreaUnitResId = areaInputCard.getSelectedUnitResId();

    double areaFactor = manager.getAreaInSqFt(area, selectedAreaUnitResId);

    if (areaFactor <= 0) {
      manager.hideResult();
      return;
    }

    double otherSideInFeet = areaFactor / oneSide;

    manager.showResult(otherSideInFeet);

    // Share text তৈরি
    String shareText = manager.buildShareableText(sharedTextHeadingBuilder(), otherSideInFeet);

    manager.setLastSharedText(shareText);
  }

  private StringBuilder sharedTextHeadingBuilder() {

    StringBuilder sb = new StringBuilder();
    sb.append(getString(R.string.amount_of_land_title))
        .append(" : ")
        .append(areaInputCard.getAreaValueAsString())
        .append("\n");
    sb.append(getString(R.string.first_arm_title))
        .append(" : ")
        .append(widthInput.getValueAsString())
        .append("\n\n");
    sb.append("──────────────────────────────\n\n");
    sb.append(getString(R.string.other_side_title)).append(" :\n\n");

    return sb;
  }

  private void showShareOptionsDialog() {
    manager.showShareOptionsDialog();
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }
}
