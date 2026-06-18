package com.example.online_voting.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Document(collection = "elections")
public class Election {
    
    @Id
    private String id;
    
    private String name;
    
    private String description;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private List<String> candidateIds;
    
    private ElectionStatus status;
    
    private long totalVotesCast;
    
    private String createdBy;
    
    private LocalDateTime createdDate;
    
    private String resultsDeclaredBy;
    
    private LocalDateTime resultsDeclaredDate;
    
    private boolean locked;
    
    public Election() {
        this.candidateIds = new ArrayList<>();
        this.status = ElectionStatus.DRAFT;
        this.totalVotesCast = 0;
        this.createdDate = LocalDateTime.now();
        this.locked = false;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public List<String> getCandidateIds() {
        return candidateIds;
    }
    
    public void setCandidateIds(List<String> candidateIds) {
        this.candidateIds = candidateIds;
    }
    
    public ElectionStatus getStatus() {
        return status;
    }
    
    public void setStatus(ElectionStatus status) {
        this.status = status;
    }
    
    public long getTotalVotesCast() {
        return totalVotesCast;
    }
    
    public void setTotalVotesCast(long totalVotesCast) {
        this.totalVotesCast = totalVotesCast;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getResultsDeclaredBy() {
        return resultsDeclaredBy;
    }
    
    public void setResultsDeclaredBy(String resultsDeclaredBy) {
        this.resultsDeclaredBy = resultsDeclaredBy;
    }
    
    public LocalDateTime getResultsDeclaredDate() {
        return resultsDeclaredDate;
    }
    
    public void setResultsDeclaredDate(LocalDateTime resultsDeclaredDate) {
        this.resultsDeclaredDate = resultsDeclaredDate;
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        if (startDate == null || endDate == null) {
            return status == ElectionStatus.ACTIVE;
        }
        // inclusive check: active when now is between startDate and endDate (inclusive)
        return status == ElectionStatus.ACTIVE && !now.isBefore(startDate) && !now.isAfter(endDate);
    }
    
    public void addCandidate(String candidateId) {
        if (!candidateIds.contains(candidateId)) {
            candidateIds.add(candidateId);
        }
    }
}
