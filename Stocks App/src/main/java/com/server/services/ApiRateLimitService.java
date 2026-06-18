package com.server.services;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ApiRateLimitService {
    
    private final ConcurrentHashMap<String, ApiCallInfo> apiCalls = new ConcurrentHashMap<>();
    
    private static class ApiCallInfo {
        private final AtomicInteger callCount = new AtomicInteger(0);
        private LocalDateTime lastReset = LocalDateTime.now();
        
        public int getCallCount() {
            return callCount.get();
        }
        
        public void incrementCallCount() {
            callCount.incrementAndGet();
        }
        
        public void resetIfNeeded() {
            LocalDateTime now = LocalDateTime.now();
            if (ChronoUnit.MINUTES.between(lastReset, now) >= 1) {
                callCount.set(0);
                lastReset = now;
            }
        }
    }
    
    public boolean canMakeApiCall(String apiName, int maxCallsPerMinute) {
        ApiCallInfo info = apiCalls.computeIfAbsent(apiName, k -> new ApiCallInfo());
        info.resetIfNeeded();
        
        return info.getCallCount() < maxCallsPerMinute;
    }
    
    public void recordApiCall(String apiName) {
        ApiCallInfo info = apiCalls.computeIfAbsent(apiName, k -> new ApiCallInfo());
        info.incrementCallCount();
    }
    
    public int getRemainingCalls(String apiName, int maxCallsPerMinute) {
        ApiCallInfo info = apiCalls.get(apiName);
        if (info == null) {
            return maxCallsPerMinute;
        }
        info.resetIfNeeded();
        return Math.max(0, maxCallsPerMinute - info.getCallCount());
    }
}
