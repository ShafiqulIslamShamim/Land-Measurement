package com.shamim.landmeasurement.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.preference.SharedPrefValues;
import com.google.android.material.materialswitch.MaterialSwitch;

public class StyleActivity extends BaseActivity {

  private ImageView checkDynamic;
  private ImageView ringEmerald, ringBlossom, ringOcean, ringAmber, ringCoral;
  private TextView segmentSystem, segmentLight, segmentDark;
  private MaterialSwitch switchAmoled;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    applyLocalTheme(); // Apply theme before super.onCreate loads views
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_style);

    // Bind Views
    checkDynamic = findViewById(R.id.check_dynamic);
    ringEmerald = findViewById(R.id.ring_emerald);
    ringBlossom = findViewById(R.id.ring_blossom);
    ringOcean = findViewById(R.id.ring_ocean);
    ringAmber = findViewById(R.id.ring_amber);
    ringCoral = findViewById(R.id.ring_coral);

    segmentSystem = findViewById(R.id.segment_system);
    segmentLight = findViewById(R.id.segment_light);
    segmentDark = findViewById(R.id.segment_dark);
    switchAmoled = findViewById(R.id.switch_amoled);

    // Initial setups
    setupInitialStates();

    // Click Listeners
    findViewById(R.id.btn_close).setOnClickListener(v -> finish());

    // Row: Dynamic theme selector
    findViewById(R.id.row_dynamic_theme).setOnClickListener(v -> {
      SharedPrefValues.putValue("app_theme_preference", "0");
      recreateWithAnimation();
    });

    // Color Swatches
    findViewById(R.id.swatch_container_emerald).setOnClickListener(v -> {
      SharedPrefValues.putValue("app_theme_preference", "1");
      recreateWithAnimation();
    });

    findViewById(R.id.swatch_container_blossom).setOnClickListener(v -> {
      SharedPrefValues.putValue("app_theme_preference", "2");
      recreateWithAnimation();
    });

    findViewById(R.id.swatch_container_ocean).setOnClickListener(v -> {
      SharedPrefValues.putValue("app_theme_preference", "3");
      recreateWithAnimation();
    });

    findViewById(R.id.swatch_container_amber).setOnClickListener(v -> {
      SharedPrefValues.putValue("app_theme_preference", "4");
      recreateWithAnimation();
    });

    findViewById(R.id.swatch_container_coral).setOnClickListener(v -> {
      SharedPrefValues.putValue("app_theme_preference", "5");
      recreateWithAnimation();
    });

    // Theme Mode Segmented Controls
    segmentSystem.setOnClickListener(v -> {
      SharedPrefValues.putValue("theme_preference", "0");
      recreateWithAnimation();
    });

    segmentLight.setOnClickListener(v -> {
      SharedPrefValues.putValue("theme_preference", "3");
      recreateWithAnimation();
    });

    segmentDark.setOnClickListener(v -> {
      SharedPrefValues.putValue("theme_preference", "2");
      recreateWithAnimation();
    });

    // AMOLED Switch Change Listener
    switchAmoled.setOnCheckedChangeListener((buttonView, isChecked) -> {
      SharedPrefValues.putValue("amoled_black_mode", isChecked ? "1" : "0");
      recreateWithAnimation();
    });
  }

  private void setupInitialStates() {
    String currentAppTheme = SharedPrefValues.getValue("app_theme_preference", "0");
    String currentThemeMode = SharedPrefValues.getValue("theme_preference", "0");
    boolean currentAmoled = SharedPrefValues.getValue("amoled_black_mode", false);

    // 1. Accent Swatches Selection States
    checkDynamic.setImageResource(currentAppTheme.equals("0") ? R.drawable.ic_check : R.drawable.circle_24px);
    
    ringEmerald.setVisibility(currentAppTheme.equals("1") ? View.VISIBLE : View.INVISIBLE);
    ringBlossom.setVisibility(currentAppTheme.equals("2") ? View.VISIBLE : View.INVISIBLE);
    ringOcean.setVisibility(currentAppTheme.equals("3") ? View.VISIBLE : View.INVISIBLE);
    ringAmber.setVisibility(currentAppTheme.equals("4") ? View.VISIBLE : View.INVISIBLE);
    ringCoral.setVisibility(currentAppTheme.equals("5") ? View.VISIBLE : View.INVISIBLE);

    // Dynamic Active Theme Badge Text Update
    TextView activeThemeBadge = findViewById(R.id.tv_active_theme_badge);
    if (activeThemeBadge != null) {
      switch (currentAppTheme) {
        case "1":
          activeThemeBadge.setText("Emerald Active");
          break;
        case "2":
          activeThemeBadge.setText("Blossom Active");
          break;
        case "3":
          activeThemeBadge.setText("Ocean Active");
          break;
        case "4":
          activeThemeBadge.setText("Amber Active");
          break;
        case "5":
          activeThemeBadge.setText("Coral Active");
          break;
        default:
          activeThemeBadge.setText("Dynamic Active");
          break;
      }
    }

    // 2. Segment Highlights
    updateSegmentHighlights(currentThemeMode);

    // 3. AMOLED Switch State
    switchAmoled.setChecked(currentAmoled);
  }

  private void updateSegmentHighlights(String mode) {
    // Determine active colors dynamically
    int activeBg = R.drawable.segment_selected_bg;
    int transparentBg = android.R.color.transparent;

    int onColor = ContextCompat.getColor(this, android.R.color.white);
    
    // Resolve secondary text color dynamically from the theme
    int offColor;
    try {
      android.util.TypedValue typedValue = new android.util.TypedValue();
      getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurfaceVariant, typedValue, true);
      offColor = typedValue.data;
    } catch (Exception e) {
      offColor = ContextCompat.getColor(this, android.R.color.darker_gray);
    }

    // Reset segments
    segmentSystem.setBackgroundResource(transparentBg);
    segmentSystem.setTextColor(offColor);
    segmentLight.setBackgroundResource(transparentBg);
    segmentLight.setTextColor(offColor);
    segmentDark.setBackgroundResource(transparentBg);
    segmentDark.setTextColor(offColor);

    // Set highlighted segment
    if (mode.equals("2")) { // Dark Mode
      segmentDark.setBackgroundResource(activeBg);
      segmentDark.setTextColor(onColor);
    } else if (mode.equals("3")) { // Light Mode
      segmentLight.setBackgroundResource(activeBg);
      segmentLight.setTextColor(onColor);
    } else { // System Follow
      segmentSystem.setBackgroundResource(activeBg);
      segmentSystem.setTextColor(onColor);
    }
  }

  @SuppressWarnings("deprecation")
  private void recreateWithAnimation() {
    recreate();
    if (android.os.Build.VERSION.SDK_INT >= 34) {
      overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out);
    } else {
      overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
  }
}
