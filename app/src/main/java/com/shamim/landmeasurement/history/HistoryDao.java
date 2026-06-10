package com.shamim.landmeasurement.history;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM history_entries ORDER BY timestamp DESC")
    List<HistoryEntry> getAllHistory();

    @Insert
    void insert(HistoryEntry entry);

    @Delete
    void delete(HistoryEntry entry);

    @Query("DELETE FROM history_entries")
    void deleteAll();
}
