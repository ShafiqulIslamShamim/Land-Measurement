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
package com.shamim.landmeasurement.history;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.activity.BaseActivity;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {

  private RecyclerView recyclerView;
  private View emptyStateView;
  private HistoryAdapter adapter;
  private List<HistoryEntry> historyList = new ArrayList<>();

  /**
   * On create.
   *
   * @param savedInstanceState the savedInstanceState
   */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_history);

    MaterialToolbar toolbar = findViewById(R.id.history_toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

    recyclerView = findViewById(R.id.history_recycler_view);
    emptyStateView = findViewById(R.id.empty_state_view);

    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    loadHistory();
  }

  /** Load history. */
  private void loadHistory() {
    new Thread(
            () -> {
              try {
                final List<HistoryEntry> entries =
                    HistoryDatabase.getDatabase(this).historyDao().getAllHistory();
                runOnUiThread(
                    () -> {
                      historyList.clear();
                      if (entries != null) {
                        historyList.addAll(entries);
                      }
                      updateUIState();
                    });
              } catch (Exception e) {
                e.printStackTrace();
              }
            })
        .start();
  }

  /** Update uistate. */
  private void updateUIState() {
    if (historyList.isEmpty()) {
      recyclerView.setVisibility(View.GONE);
      emptyStateView.setVisibility(View.VISIBLE);
    } else {
      emptyStateView.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);

      adapter =
          new HistoryAdapter(
              historyList,
              new HistoryAdapter.OnHistoryItemClickListener() {
                /**
                 * On delete click.
                 *
                 * @param entry the entry
                 */
                @Override
                public void onDeleteClick(HistoryEntry entry) {
                  confirmDeleteEntry(entry);
                }

                /**
                 * On item click.
                 *
                 * @param entry the entry
                 */
                @Override
                public void onItemClick(HistoryEntry entry) {
                  launchShapeActivity(entry);
                }
              });
      recyclerView.setAdapter(adapter);
    }
  }

  /**
   * Launch shape activity.
   *
   * @param entry the entry
   */
  private void launchShapeActivity(HistoryEntry entry) {
    if (entry.getActivityClassName() == null || entry.getActivityClassName().isEmpty()) return;
    try {
      Class<?> clazz = Class.forName(entry.getActivityClassName());
      android.content.Intent intent = new android.content.Intent(this, clazz);
      intent.putExtra("serialized_inputs", entry.getSerializedInputs());
      startActivity(intent);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Confirm delete entry.
   *
   * @param entry the entry
   */
  private void confirmDeleteEntry(HistoryEntry entry) {
    new MaterialAlertDialogBuilder(this)
        .setTitle(R.string.delete_entry_confirmation_title)
        .setMessage(R.string.delete_entry_confirmation_msg)
        .setNegativeButton(R.string.dialog_cancel, null)
        .setPositiveButton(
            R.string.dialog_confirm,
            (dialog, which) -> {
              deleteEntry(entry);
            })
        .show();
  }

  /**
   * Delete entry.
   *
   * @param entry the entry
   */
  private void deleteEntry(HistoryEntry entry) {
    new Thread(
            () -> {
              try {
                HistoryDatabase.getDatabase(this).historyDao().delete(entry);
                runOnUiThread(
                    () -> {
                      Toast.makeText(
                              this, getString(R.string.history_deleted_toast), Toast.LENGTH_SHORT)
                          .show();
                      loadHistory();
                    });
              } catch (Exception e) {
                e.printStackTrace();
              }
            })
        .start();
  }

  /** Confirm clear all. */
  private void confirmClearAll() {
    if (historyList.isEmpty()) return;
    new MaterialAlertDialogBuilder(this)
        .setTitle(R.string.delete_all_confirmation_title)
        .setMessage(R.string.delete_all_confirmation_msg)
        .setNegativeButton(R.string.dialog_cancel, null)
        .setPositiveButton(
            R.string.dialog_confirm,
            (dialog, which) -> {
              clearAllHistory();
            })
        .show();
  }

  /** Clear all history. */
  private void clearAllHistory() {
    new Thread(
            () -> {
              try {
                HistoryDatabase.getDatabase(this).historyDao().deleteAll();
                runOnUiThread(
                    () -> {
                      Toast.makeText(
                              this, getString(R.string.history_cleared_toast), Toast.LENGTH_SHORT)
                          .show();
                      loadHistory();
                    });
              } catch (Exception e) {
                e.printStackTrace();
              }
            })
        .start();
  }

  /**
   * On create options menu.
   *
   * @param menu the menu
   * @return the result of the operation
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.history_menu, menu);
    return true;
  }

  /**
   * On options item selected.
   *
   * @param item the item
   * @return the result of the operation
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_clear_all) {
      confirmClearAll();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
