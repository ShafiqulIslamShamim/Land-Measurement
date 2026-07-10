/*
 * Copyright (c) 2026 Shafiqul Islam Shamim
 * GitHub: https://github.com/ShafiqulIslamShamim/Land-Measurement
 *
 * All Rights Reserved.
 *
 * This source code is made publicly available solely for viewing, collaboration,
 * educational reference, and submitting pull requests to the official repository.
 *
 * No permission is granted to copy, modify, redistribute, sublicense, or use
 * this source code, in whole or in part, for personal, commercial, or any other
 * purpose without the prior written permission of the copyright holder.
 */
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
  /**
   * Item model.
   *
   * @param categoryTitle the categoryTitle
   */
  public ItemModel(String categoryTitle) {
    this.viewType = TYPE_CATEGORY;
    this.categoryTitle = categoryTitle;
  }

  // Item constructor
  /**
   * Item model.
   *
   * @param id the id
   * @param iconRes the iconRes
   * @param title the title
   * @param subtitle the subtitle
   */
  public ItemModel(int id, int iconRes, String title, String subtitle) {
    this.viewType = TYPE_ITEM;
    this.id = id;
    this.iconRes = iconRes;
    this.title = title;
    this.subtitle = subtitle;
  }
}
