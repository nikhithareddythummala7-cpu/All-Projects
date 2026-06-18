package com.streamy.controller;

import com.streamy.model.WatchHistory;
import com.streamy.service.WatchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class WatchHistoryController {
    private final WatchHistoryService watchHistoryService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<WatchHistory>> getUserHistory(@PathVariable String userId) {
        return ResponseEntity.ok(watchHistoryService.getUserHistory(userId));
    }

    @PostMapping
    public ResponseEntity<WatchHistory> saveProgress(@RequestBody WatchHistory history) {
        return ResponseEntity.ok(watchHistoryService.saveProgress(history));
    }
}
