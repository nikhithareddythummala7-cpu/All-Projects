package com.example.online_voting.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "candidates")
public class Candidate {
    
    @Id
    private String id;
    
    private String name;
    
    private String party;
    
    private String description;
    
    private String imageUrl;
    
    private String symbol;
    
    private String electionId;
    
    private long voteCount;
    
    private LocalDateTime createdDate;
    
    public Candidate() {
        this.voteCount = 0;
        this.createdDate = LocalDateTime.now();
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
    
    public String getParty() {
        return party;
    }
    
    public void setParty(String party) {
        this.party = party;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getElectionId() {
        return electionId;
    }
    
    public void setElectionId(String electionId) {
        this.electionId = electionId;
    }
    
    public long getVoteCount() {
        return voteCount;
    }
    
    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public void incrementVoteCount() {
        this.voteCount++;
    }
}
