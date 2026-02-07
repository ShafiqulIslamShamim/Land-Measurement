package com.shamim.landmeasurement.recycle_view.model;

public class ItemModel {

  public static final int TYPE_CATEGORY = 0;
  public static final int TYPE_ITEM = 1;

  public final int viewType;

  // Category
  public String categoryTitle;

  // Item
  public int id;
  public int iconRes;
  public String title;
  public String subtitle;

  // Category constructor
  public ItemModel(String categoryTitle) {
    this.viewType = TYPE_CATEGORY;
    this.categoryTitle = categoryTitle;
  }

  // Item constructor
  public ItemModel(int id, int iconRes, String title, String subtitle) {
    this.viewType = TYPE_ITEM;
    this.id = id;
    this.iconRes = iconRes;
    this.title = title;
    this.subtitle = subtitle;
  }
}
