package com.example.floweandgift.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class AdminActivityLogService {

    private static final Logger logger = LoggerFactory.getLogger(AdminActivityLogService.class);
    private final ConcurrentLinkedQueue<AdminActivity> activityLog = new ConcurrentLinkedQueue<>();

    public void logActivity(String adminUsername, String action, String details) {
        AdminActivity activity = new AdminActivity(adminUsername, action, details, LocalDateTime.now());
        activityLog.add(activity);
        logger.info("Admin Activity: {} - {} - {}", adminUsername, action, details);

        // Keep only last 1000 activities to prevent memory issues
        while (activityLog.size() > 1000) {
            activityLog.poll();
        }
    }

    public ConcurrentLinkedQueue<AdminActivity> getRecentActivities() {
        return new ConcurrentLinkedQueue<>(activityLog);
    }

    public static class AdminActivity {
        private String adminUsername;
        private String action;
        private String details;
        private LocalDateTime timestamp;

        public AdminActivity(String adminUsername, String action, String details, LocalDateTime timestamp) {
            this.adminUsername = adminUsername;
            this.action = action;
            this.details = details;
            this.timestamp = timestamp;
        }

        // Getters
        public String getAdminUsername() { return adminUsername; }
        public String getAction() { return action; }
        public String getDetails() { return details; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
