package com.streamy.service;

import com.streamy.model.WatchHistory;
import java.util.List;

public interface WatchHistoryService {
    List<WatchHistory> getUserHistory(String userId);
    WatchHistory saveProgress(WatchHistory history);
}
