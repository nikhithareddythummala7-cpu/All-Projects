package com.SmartBridge.Task_Management.repository;

import com.SmartBridge.Task_Management.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByCreatedByUserId(String userId);
    List<Project> findByTeamMemberUserIdsContains(String userId);
}
