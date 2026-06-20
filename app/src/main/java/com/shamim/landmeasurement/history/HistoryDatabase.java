package com.shamim.landmeasurement.history;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
    entities = {HistoryEntry.class},
    version = 2,
    exportSchema = false)
public abstract class HistoryDatabase extends RoomDatabase {
  private static volatile HistoryDatabase INSTANCE;

  public abstract HistoryDao historyDao();

  public static HistoryDatabase getDatabase(final Context context) {
    if (INSTANCE == null) {
      synchronized (HistoryDatabase.class) {
        if (INSTANCE == null) {
          INSTANCE =
              Room.databaseBuilder(
                      context.getApplicationContext(), HistoryDatabase.class, "history_database")
                  .fallbackToDestructiveMigration()
                  .build();
        }
      }
    }
    return INSTANCE;
  }
}
