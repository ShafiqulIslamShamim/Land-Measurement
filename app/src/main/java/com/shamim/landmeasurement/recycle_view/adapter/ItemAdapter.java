package com.shamim.landmeasurement.recycle_view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shamim.landmeasurement.*;
import com.shamim.landmeasurement.recycle_view.model.*;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public interface OnItemClickListener {
    void onItemClick(int id);
  }

  private final List<ItemModel> list;
  private final OnItemClickListener listener;

  public ItemAdapter(List<ItemModel> list, OnItemClickListener listener) {
    this.list = list;
    this.listener = listener;
  }

  @Override
  public int getItemViewType(int position) {
    return list.get(position).viewType;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    LayoutInflater inflater = LayoutInflater.from(parent.getContext());

    if (viewType == ItemModel.TYPE_CATEGORY) {
      View view = inflater.inflate(R.layout.items_category_land_calculation, parent, false);
      return new CategoryVH(view);
    } else {
      View view = inflater.inflate(R.layout.items_screen_land_calculation, parent, false);
      return new ItemVH(view);
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    ItemModel item = list.get(position);

    if (holder instanceof CategoryVH) {
      ((CategoryVH) holder).title.setText(item.categoryTitle);
    } else {
      ItemVH vh = (ItemVH) holder;
      vh.icon.setImageResource(item.iconRes);
      vh.title.setText(item.title);

      if (item.subtitle != null && !item.subtitle.isEmpty()) {
        vh.subtitle.setText(item.subtitle);
        vh.subtitle.setVisibility(View.VISIBLE);
      } else {
        vh.subtitle.setVisibility(View.GONE);
      }

      vh.itemView.setOnClickListener(v -> listener.onItemClick(item.id));
    }
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  static class CategoryVH extends RecyclerView.ViewHolder {
    TextView title;

    CategoryVH(@NonNull View itemView) {
      super(itemView);
      title = itemView.findViewById(R.id.tvCategory);
    }
  }

  static class ItemVH extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView title, subtitle;

    ItemVH(@NonNull View itemView) {
      super(itemView);
      icon = itemView.findViewById(R.id.icon);
      title = itemView.findViewById(R.id.title);
      subtitle = itemView.findViewById(R.id.subtitle);
    }
  }
}
