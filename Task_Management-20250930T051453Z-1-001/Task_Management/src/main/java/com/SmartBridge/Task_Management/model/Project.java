package com.SmartBridge.Task_Management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "projects")
public class Project {
    @Id
    private String id;
    private String projectName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // e.g., Planned, In Progress, Completed

    private String createdByUserId;
    private List<String> teamMemberUserIds = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(String createdByUserId) { this.createdByUserId = createdByUserId; }

    public List<String> getTeamMemberUserIds() { return teamMemberUserIds; }
    public void setTeamMemberUserIds(List<String> teamMemberUserIds) { this.teamMemberUserIds = teamMemberUserIds; }
}
