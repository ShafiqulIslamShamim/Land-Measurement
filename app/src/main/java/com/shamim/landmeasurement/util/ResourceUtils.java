package com.shamim.landmeasurement.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.preference.*;
import com.shamim.landmeasurement.recycle_view.*;
import com.shamim.landmeasurement.update_checker.*;

public class ResourceUtils {

  // Common resource types
  public enum ResourceType {
    DRAWABLE("drawable"),
    STRING("string"),
    LAYOUT("layout"),
    ID("id"),
    COLOR("color"),
    DIMEN("dimen"),
    STYLE("style"),
    STYLEABLE("styleable"),
    ARRAY("array"),
    RAW("raw"),
    MIPMAP("mipmap"),
    ANIM("anim"),
    MENU("menu"),
    XML("xml"),
    ATTR("attr"), // Added attribute type
    BOOL("bool"), // Added boolean type
    INTEGER("integer"), // Added integer type
    FONT("font"), // Added font type
    NAVIGATION("navigation"); // Added navigation type

    private final String typeName;

    ResourceType(String typeName) {
      this.typeName = typeName;
    }

    public String getTypeName() {
      return typeName;
    }
  }

  /** Get resource ID with enum type */
  public static int getResourceId(Context context, String name, ResourceType type) {
    return getResourceId(context, name, type.getTypeName());
  }

  /** Get resource ID with string type */
  public static int getResourceId(Context context, String name, String type) {
    if (context == null || name == null || type == null) {
      return 0;
    }

    Resources resources = context.getResources();
    String packageName = context.getPackageName();

    int resId = resources.getIdentifier(name, type, packageName);

    if (resId == 0) {
      // Try alternative: remove underscores, try lowercase, etc.
      String normalized = name.toLowerCase().replace("_", "");
      resId = resources.getIdentifier(normalized, type, packageName);
    }

    return resId;
  }

  /** Find attribute resource ID */
  public static int getAttrId(Context context, String attrName) {
    return getResourceId(context, attrName, ResourceType.ATTR);
  }

  /** Find styleable resource ID (int array) */
  public static int getStyleableId(Context context, String styleableName) {
    return getResourceId(context, styleableName, ResourceType.STYLEABLE);
  }

  /** Get styleable array resource ID Styleable resources are special - they return int arrays */
  public static int[] getStyleableArray(Context context, String styleableName) {
    if (context == null || styleableName == null) {
      return null;
    }

    Resources resources = context.getResources();
    String packageName = context.getPackageName();

    int resId = resources.getIdentifier(styleableName, "styleable", packageName);

    if (resId != 0) {
      return resources.getIntArray(resId);
    }

    return null;
  }

  /** Get individual styleable attribute ID from styleable */
  public static int getStyleableAttrId(Context context, String styleableName, String attrName) {
    // Styleable attributes are typically named as StyleableName_AttributeName
    String fullName = styleableName + "_" + attrName;
    return getResourceId(context, fullName, ResourceType.STYLEABLE);
  }

  /** Find drawable resource ID */
  public static int getDrawableId(Context context, String drawableName) {
    return getResourceId(context, drawableName, ResourceType.DRAWABLE);
  }

  /** Find string resource ID */
  public static int getStringId(Context context, String stringName) {
    return getResourceId(context, stringName, ResourceType.STRING);
  }

  /** Find layout resource ID */
  public static int getLayoutId(Context context, String layoutName) {
    return getResourceId(context, layoutName, ResourceType.LAYOUT);
  }

  /** Find color resource ID */
  public static int getColorId(Context context, String colorName) {
    return getResourceId(context, colorName, ResourceType.COLOR);
  }

  /** Find dimension resource ID */
  public static int getDimenId(Context context, String dimenName) {
    return getResourceId(context, dimenName, ResourceType.DIMEN);
  }

  /** Find boolean resource ID */
  public static int getBoolId(Context context, String boolName) {
    return getResourceId(context, boolName, ResourceType.BOOL);
  }

  /** Find integer resource ID */
  public static int getIntegerId(Context context, String integerName) {
    return getResourceId(context, integerName, ResourceType.INTEGER);
  }

  /** Find animation resource ID */
  public static int getAnimId(Context context, String animName) {
    return getResourceId(context, animName, ResourceType.ANIM);
  }

  /** Find menu resource ID */
  public static int getMenuId(Context context, String menuName) {
    return getResourceId(context, menuName, ResourceType.MENU);
  }

  /** Bulk find multiple resources */
  public static int[] getResourceIds(Context context, String[] names, ResourceType type) {
    if (names == null) return new int[0];

    int[] ids = new int[names.length];
    for (int i = 0; i < names.length; i++) {
      ids[i] = getResourceId(context, names[i], type);
    }
    return ids;
  }

  /** Get attribute value from theme */
  public static int getThemeAttribute(Context context, String attrName) {
    int attrId = getAttrId(context, attrName);
    if (attrId == 0) {
      return 0;
    }

    TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[] {attrId});
    int value = typedArray.getResourceId(0, 0);
    typedArray.recycle();

    return value;
  }

  /** Get attribute value from style */
  public static int getAttributeValue(Context context, int styleResId, String attrName) {
    int attrId = getAttrId(context, attrName);
    if (attrId == 0) {
      return 0;
    }

    TypedArray typedArray =
        context.getTheme().obtainStyledAttributes(styleResId, new int[] {attrId});
    int value = typedArray.getResourceId(0, 0);
    typedArray.recycle();

    return value;
  }

  /** Get attribute integer value */
  public static int getAttributeIntValue(
      Context context, int styleResId, String attrName, int defaultValue) {
    int attrId = getAttrId(context, attrName);
    if (attrId == 0) {
      return defaultValue;
    }

    TypedArray typedArray =
        context.getTheme().obtainStyledAttributes(styleResId, new int[] {attrId});
    int value = typedArray.getInt(0, defaultValue);
    typedArray.recycle();

    return value;
  }

  /** Get attribute boolean value */
  public static boolean getAttributeBooleanValue(
      Context context, int styleResId, String attrName, boolean defaultValue) {
    int attrId = getAttrId(context, attrName);
    if (attrId == 0) {
      return defaultValue;
    }

    TypedArray typedArray =
        context.getTheme().obtainStyledAttributes(styleResId, new int[] {attrId});
    boolean value = typedArray.getBoolean(0, defaultValue);
    typedArray.recycle();

    return value;
  }

  /** Get attribute string value */
  public static String getAttributeStringValue(Context context, int styleResId, String attrName) {
    int attrId = getAttrId(context, attrName);
    if (attrId == 0) {
      return null;
    }

    TypedArray typedArray =
        context.getTheme().obtainStyledAttributes(styleResId, new int[] {attrId});
    String value = typedArray.getString(0);
    typedArray.recycle();

    return value;
  }
}
