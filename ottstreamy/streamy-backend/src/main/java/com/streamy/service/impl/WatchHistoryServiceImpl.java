package com.streamy.service.impl;

import com.streamy.model.WatchHistory;
import com.streamy.repository.WatchHistoryRepository;
import com.streamy.service.WatchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchHistoryServiceImpl implements WatchHistoryService {
    private final WatchHistoryRepository watchHistoryRepository;

    @Override
    public List<WatchHistory> getUserHistory(String userId) {
        return watchHistoryRepository.findByUserId(userId);
    }

    @Override
    public WatchHistory saveProgress(WatchHistory history) {
        return watchHistoryRepository.save(history);
    }
}
