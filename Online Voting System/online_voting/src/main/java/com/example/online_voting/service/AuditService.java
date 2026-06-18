package com.example.online_voting.service;

import com.example.online_voting.model.AuditLog;
import com.example.online_voting.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public AuditLog logAction(String userId, String userRole, String action, String details, String ipAddress) {
        AuditLog log = new AuditLog(userId, userRole, action, details, ipAddress);
        return auditLogRepository.save(log);
    }
    
    public List<AuditLog> getAuditLogsByUser(String userId) {
        return auditLogRepository.findByUserId(userId);
    }
    
    public List<AuditLog> getAuditLogsByRole(String userRole) {
        return auditLogRepository.findByUserRole(userRole);
    }
    
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }
    
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
    
    public List<AuditLog> getRecentVotingActivities(int limit) {
        List<AuditLog> allLogs = auditLogRepository.findAllByOrderByTimestampDesc();
        return allLogs.stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }
}
