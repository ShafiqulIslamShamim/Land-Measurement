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

public class CircleSideCalculatorActivity extends BaseActivity {

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
    widthInput.setVisibility(View.GONE);

    View resultSection = findViewById(R.id.result_section);
    manager.setupResultSection(resultSection);
  }

  private void setupToolbar() {
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(R.string.item_quadrilateral);
    }
  }

  private void setupTitle() {

    manager.setTitle(getString(R.string.card_radius_title));
  }

  private void setupListeners() {

    manager.setCalculateClickListener(this::calculate);
    manager.setShareClickListener(this::showShareOptionsDialog);
  }

  private void calculate() {
    double area = areaInputCard.getAreaValue();

    if (area <= 0) {
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

    double radius = Math.sqrt(areaFactor / Math.PI);

    manager.showResult(radius);

    // Share text তৈরি
    String shareText = manager.buildShareableText(sharedTextHeadingBuilder(), radius);

    manager.setLastSharedText(shareText);
  }

  private StringBuilder sharedTextHeadingBuilder() {

    StringBuilder sb = new StringBuilder();
    sb.append(getString(R.string.amount_of_land_title))
        .append(" : ")
        .append(areaInputCard.getAreaValueAsString())
        .append("\n\n");
    sb.append("──────────────────────────────\n\n");
    sb.append(getString(R.string.card_radius_title)).append(" :\n\n");

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
