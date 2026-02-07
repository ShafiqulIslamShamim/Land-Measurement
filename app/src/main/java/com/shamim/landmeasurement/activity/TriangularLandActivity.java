package com.shamim.landmeasurement.activity;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.util.*;
import com.shamim.landmeasurement.view.*;

public class TriangularLandActivity extends BaseActivity {

  private LandResultManager resultManager;
  private LandSingleEditTextCardView FirstArmInput;
  private LandSingleEditTextCardView SecondArmInput;
  private LandSingleEditTextCardView ThirdArmInput;
  private ScrollView scrollView;
  private MaterialToolbar toolbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_triangular_land);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getString(R.string.item_triangular));
    }

    FirstArmInput = findViewById(R.id.first_arm_input);
    SecondArmInput = findViewById(R.id.second_arm_input);
    ThirdArmInput = findViewById(R.id.third_arm_input);
    scrollView = findViewById(R.id.scroll_view);

    FirstArmInput.setTitle(getString(R.string.first_arm_title));
    SecondArmInput.setTitle(getString(R.string.second_arm_title));
    ThirdArmInput.setTitle(getString(R.string.third_arm_title));

    resultManager = new LandResultManager(findViewById(android.R.id.content), this);
    resultManager.setOnCalculateClickListener(this::calculate);
  }

  private void calculate() {
    double FirstArm = FirstArmInput.getValueInFeet();
    double SecondArm = SecondArmInput.getValueInFeet();
    double ThirdArm = ThirdArmInput.getValueInFeet();

    if (!FirstArmInput.hasValidInput()
        || !SecondArmInput.hasValidInput()
        || !ThirdArmInput.hasValidInput()) {
      Toast.makeText(this, getString(R.string.item_triangular_input_error), Toast.LENGTH_LONG)
          .show();
      resultManager.hideResult();
      return;
    }

    // অর্ধ-পরিসীমা হিসাব
    double s = (FirstArm + SecondArm + ThirdArm) / 2.0;

    double areaSqFt = Math.sqrt(s * (s - FirstArm) * (s - SecondArm) * (s - ThirdArm));

    resultManager.showResult(areaSqFt);

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
