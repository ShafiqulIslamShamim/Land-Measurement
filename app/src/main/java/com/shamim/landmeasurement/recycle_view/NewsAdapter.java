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
package com.shamim.landmeasurement.recycle_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.activity.*;
import com.shamim.landmeasurement.exception_catcher.*;
import com.shamim.landmeasurement.preference.*;
import com.shamim.landmeasurement.update_checker.*;
import com.shamim.landmeasurement.util.*;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

  private final String[] titles;
  private final int[] icons;
  private final OnItemClickListener listener;

  public interface OnItemClickListener {
    void onItemClick(int position);
  }

  /**
   * News adapter.
   *
   * @param titles the titles
   * @param icons the icons
   * @param listener the listener
   */
  public NewsAdapter(String[] titles, int[] icons, OnItemClickListener listener) {
    this.titles = titles;
    this.icons = icons;
    this.listener = listener;
  }

  /**
   * On create view holder.
   *
   * @param parent the parent
   * @param viewType the viewType
   * @return the result of the operation
   */
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_option, parent, false);
    return new ViewHolder(view);
  }

  /**
   * On bind view holder.
   *
   * @param holder the holder
   * @param position the position
   */
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.title.setText(titles[position]);
    holder.icon.setImageResource(icons[position]);

    holder.itemView.setOnClickListener(
        v -> {
          if (listener != null) listener.onItemClick(position);
        });
  }

  /**
   * Get item count.
   *
   * @return the result of the operation
   */
  @Override
  public int getItemCount() {
    return titles.length;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView title;

    /**
     * View holder.
     *
     * @param itemView the itemView
     */
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      icon = itemView.findViewById(R.id.item_icon);
      title = itemView.findViewById(R.id.item_title);
    }
  }
}
