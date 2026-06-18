package com.example.online_voting.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "votes")
public class Vote {
    
    @Id
    private String id;
    
    private String voterId;
    
    private String electionId;
    
    private String candidateId;
    
    private LocalDateTime castDateTime;
    
    private String ipAddress;
    
    private String deviceInfo;
    
    private boolean verified;
    
    public Vote() {
        this.castDateTime = LocalDateTime.now();
        this.verified = false;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getVoterId() {
        return voterId;
    }
    
    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }
    
    public String getElectionId() {
        return electionId;
    }
    
    public void setElectionId(String electionId) {
        this.electionId = electionId;
    }
    
    public String getCandidateId() {
        return candidateId;
    }
    
    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }
    
    public LocalDateTime getCastDateTime() {
        return castDateTime;
    }
    
    public void setCastDateTime(LocalDateTime castDateTime) {
        this.castDateTime = castDateTime;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getDeviceInfo() {
        return deviceInfo;
    }
    
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
    
    public boolean isVerified() {
        return verified;
    }
    
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
