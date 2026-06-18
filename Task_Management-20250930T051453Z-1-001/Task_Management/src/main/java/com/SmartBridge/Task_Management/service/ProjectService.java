package com.SmartBridge.Task_Management.service;

import com.SmartBridge.Task_Management.model.Project;
import com.SmartBridge.Task_Management.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> listForUser(String userId, boolean isAdmin) {
        if (isAdmin) {
            return projectRepository.findAll();
        }
        // union of creator and member
        Set<Project> out = new HashSet<>();
        out.addAll(projectRepository.findByCreatedByUserId(userId));
        out.addAll(projectRepository.findByTeamMemberUserIdsContains(userId));
        return out.stream().toList();
    }

    public Optional<Project> getAccessibleById(String id, String userId, boolean isAdmin) {
        Optional<Project> p = projectRepository.findById(id);
        if (p.isEmpty()) return Optional.empty();
        if (isAdmin) return p;
        Project pr = p.get();
        boolean member = pr.getCreatedByUserId() != null && pr.getCreatedByUserId().equals(userId)
                || (pr.getTeamMemberUserIds() != null && pr.getTeamMemberUserIds().contains(userId));
        return member ? p : Optional.empty();
    }

    public Project create(Project project, String creatorUserId) {
        project.setId(null);
        project.setCreatedByUserId(creatorUserId);
        if (project.getTeamMemberUserIds() == null) {
            project.setTeamMemberUserIds(new java.util.ArrayList<>());
        }
        return projectRepository.save(project);
    }

    public Optional<Project> update(Project updates, String userId, boolean isAdmin) {
        Optional<Project> existing = projectRepository.findById(updates.getId());
        if (existing.isEmpty()) return Optional.empty();
        Project pr = existing.get();
        if (!isAdmin && !(pr.getCreatedByUserId() != null && pr.getCreatedByUserId().equals(userId)
                || (pr.getTeamMemberUserIds() != null && pr.getTeamMemberUserIds().contains(userId)))) {
            return Optional.empty();
        }
        // Preserve creator
        updates.setCreatedByUserId(pr.getCreatedByUserId());
        return Optional.of(projectRepository.save(updates));
    }

    public boolean delete(String id, String userId, boolean isAdmin) {
        Optional<Project> p = projectRepository.findById(id);
        if (p.isEmpty()) return false;
        Project pr = p.get();
        if (!isAdmin && !(pr.getCreatedByUserId() != null && pr.getCreatedByUserId().equals(userId)
                || (pr.getTeamMemberUserIds() != null && pr.getTeamMemberUserIds().contains(userId)))) {
            return false;
        }
        projectRepository.deleteById(id);
        return true;
    }
}
