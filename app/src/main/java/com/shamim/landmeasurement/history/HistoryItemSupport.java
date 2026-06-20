package com.shamim.landmeasurement.history;

public interface HistoryItemSupport {
  String getSerializedInputs();

  void restoreSerializedInputs(String data);
}
