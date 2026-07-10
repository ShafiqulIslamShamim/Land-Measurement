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

  /**
   * History dao.
   *
   * @return the result of the operation
   */
  public abstract HistoryDao historyDao();

  /**
   * Get database.
   *
   * @param context the context
   * @return the result of the operation
   */
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
