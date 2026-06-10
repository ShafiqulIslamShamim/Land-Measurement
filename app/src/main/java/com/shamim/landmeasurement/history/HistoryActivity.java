package com.shamim.landmeasurement.history;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

    private void loadHistory() {
        new Thread(() -> {
            try {
                final List<HistoryEntry> entries = HistoryDatabase.getDatabase(this).historyDao().getAllHistory();
                runOnUiThread(() -> {
                    historyList.clear();
                    if (entries != null) {
                        historyList.addAll(entries);
                    }
                    updateUIState();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateUIState() {
        if (historyList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            
            adapter = new HistoryAdapter(historyList, new HistoryAdapter.OnHistoryItemClickListener() {
                @Override
                public void onDeleteClick(HistoryEntry entry) {
                    confirmDeleteEntry(entry);
                }

                @Override
                public void onItemClick(HistoryEntry entry) {
                    launchShapeActivity(entry);
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

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

    private void confirmDeleteEntry(HistoryEntry entry) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_entry_confirmation_title)
                .setMessage(R.string.delete_entry_confirmation_msg)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                    deleteEntry(entry);
                })
                .show();
    }

    private void deleteEntry(HistoryEntry entry) {
        new Thread(() -> {
            try {
                HistoryDatabase.getDatabase(this).historyDao().delete(entry);
                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.history_deleted_toast), Toast.LENGTH_SHORT).show();
                    loadHistory();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void confirmClearAll() {
        if (historyList.isEmpty()) return;
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_all_confirmation_title)
                .setMessage(R.string.delete_all_confirmation_msg)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                    clearAllHistory();
                })
                .show();
    }

    private void clearAllHistory() {
        new Thread(() -> {
            try {
                HistoryDatabase.getDatabase(this).historyDao().deleteAll();
                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.history_cleared_toast), Toast.LENGTH_SHORT).show();
                    loadHistory();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear_all) {
            confirmClearAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
