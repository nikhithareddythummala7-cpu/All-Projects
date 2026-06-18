package com.example.online_voting.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;
    
    @Indexed(unique = true)
    private String voterId;
    
    private String aadhaarId;
    
    private String firstName;
    
    private String lastName;
    
    private String phoneNumber;
    
    private String address;
    
    private String state;
    
    private String district;
    
    private UserRole role;
    
    private boolean approved;
    
    private LocalDateTime registrationDate;
    
    private LocalDateTime lastLoginDate;
    
    private boolean active;
    
    private List<String> votedElectionIds;
    
    public User() {
        this.votedElectionIds = new ArrayList<>();
        this.approved = false;
        this.active = true;
        this.registrationDate = LocalDateTime.now();
        this.role = UserRole.VOTER;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getVoterId() {
        return voterId;
    }
    
    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }
    
    public String getAadhaarId() {
        return aadhaarId;
    }
    
    public void setAadhaarId(String aadhaarId) {
        this.aadhaarId = aadhaarId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getDistrict() {
        return district;
    }
    
    public void setDistrict(String district) {
        this.district = district;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public boolean isApproved() {
        return approved;
    }
    
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }
    
    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public List<String> getVotedElectionIds() {
        return votedElectionIds;
    }
    
    public void setVotedElectionIds(List<String> votedElectionIds) {
        this.votedElectionIds = votedElectionIds;
    }
    
    public boolean hasVoted(String electionId) {
        return votedElectionIds.contains(electionId);
    }
    
    public void addVotedElection(String electionId) {
        if (!votedElectionIds.contains(electionId)) {
            votedElectionIds.add(electionId);
        }
    }
}
