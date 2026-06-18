package com.example.online_voting.controller;

import com.example.online_voting.model.User;
import com.example.online_voting.model.Election;
import com.example.online_voting.model.Candidate;
import com.example.online_voting.service.UserService;
import com.example.online_voting.service.ElectionService;
import com.example.online_voting.service.CandidateService;
import com.example.online_voting.service.VoteService;
import com.example.online_voting.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ElectionService electionService;
    
    @Autowired
    private CandidateService candidateService;
    
    @Autowired
    private VoteService voteService;
    
    @Autowired
    private AuditService auditService;
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email).orElse(null);
    }
    
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        List<User> allVoters = userService.getPendingVoters();
        List<Election> allElections = electionService.getAllElections();
        long totalVotes = 0;
        for (Election e : allElections) {
            totalVotes += voteService.getVoteCountByElection(e.getId());
        }
        
        model.addAttribute("admin", admin);
        model.addAttribute("totalVoters", userService.getAllElectionOfficers().size());
        model.addAttribute("totalElections", allElections.size());
        model.addAttribute("totalVotes", totalVotes);
        model.addAttribute("voterTurnout", calculateTurnout(allVoters.size(), totalVotes));
        
        return "admin/dashboard";
    }
    
    @GetMapping("/elections")
    public String manageElections(Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        List<Election> elections = electionService.getAllElections();
        model.addAttribute("elections", elections);
        
        return "admin/elections";
    }

    @GetMapping("/officers")
    public String manageOfficers(Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";

        List<User> officers = userService.getAllElectionOfficers();
        model.addAttribute("officers", officers);
        return "admin/officers";
    }

    @GetMapping("/officer/create")
    public String createOfficerPage(Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";

        model.addAttribute("officer", new User());
        return "admin/create-officer";
    }

    @PostMapping("/officer/create")
    public String createOfficer(@ModelAttribute User officer, HttpServletRequest request, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";

        try {
            userService.createElectionOfficer(officer);
            auditService.logAction(admin.getId(), "ADMIN", "OFFICER_CREATED",
                    "Officer created: " + officer.getEmail(), request.getRemoteAddr());
            return "redirect:/admin/officers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("officer", officer);
            return "admin/create-officer";
        }
    }

    @PostMapping("/officer/{id}/activate")
    public String activateOfficer(@PathVariable String id, HttpServletRequest request, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        try {
            userService.activateOfficer(id);
            auditService.logAction(admin.getId(), "ADMIN", "OFFICER_ACTIVATED",
                    "Officer activated: " + id, request.getRemoteAddr());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/officers";
    }

    @PostMapping("/officer/{id}/deactivate")
    public String deactivateOfficer(@PathVariable String id, HttpServletRequest request, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        try {
            userService.deactivateOfficer(id);
            auditService.logAction(admin.getId(), "ADMIN", "OFFICER_DEACTIVATED",
                    "Officer deactivated: " + id, request.getRemoteAddr());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/officers";
    }
    
    @GetMapping("/election/create")
    public String createElectionPage(Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        model.addAttribute("election", new Election());
        return "admin/create-election";
    }
    
    @PostMapping("/election/create")
    public String createElection(@ModelAttribute Election election, HttpServletRequest request, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        try {
            election.setCreatedBy(admin.getId());
            electionService.createElection(election);
            auditService.logAction(admin.getId(), "ADMIN", "ELECTION_CREATED",
                    "Election created: " + election.getName(), request.getRemoteAddr());
            return "redirect:/admin/elections";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("election", election);
            return "admin/create-election";
        }
    }
    
    @GetMapping("/election/{id}/edit")
    public String editElectionPage(@PathVariable String id, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        Election election = electionService.findById(id).orElse(null);
        if (election == null) return "redirect:/admin/elections";
        
        model.addAttribute("election", election);
        return "admin/edit-election";
    }
    
    @PostMapping("/election/{id}/edit")
    public String editElection(@PathVariable String id, @ModelAttribute Election election, 
                              HttpServletRequest request, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        try {
            election.setId(id);
            electionService.updateElection(election);
            auditService.logAction(admin.getId(), "ADMIN", "ELECTION_UPDATED",
                    "Election updated: " + election.getName(), request.getRemoteAddr());
            return "redirect:/admin/elections";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/edit-election";
        }
    }
    
    @GetMapping("/election/{id}/candidates")
    public String manageCandidates(@PathVariable String id, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        Election election = electionService.findById(id).orElse(null);
        if (election == null) return "redirect:/admin/elections";
        
        List<Candidate> candidates = candidateService.getCandidatesByElection(id);
        model.addAttribute("election", election);
        model.addAttribute("candidates", candidates);
        
        return "admin/manage-candidates";
    }
    
    @GetMapping("/election/{electionId}/candidate/add")
    public String addCandidatePage(@PathVariable String electionId, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        Election election = electionService.findById(electionId).orElse(null);
        if (election == null) return "redirect:/admin/elections";
        
        model.addAttribute("election", election);
        model.addAttribute("candidate", new Candidate());
        
        return "admin/add-candidate";
    }
    
    @PostMapping("/election/{electionId}/candidate/add")
    public String addCandidate(@PathVariable String electionId, @ModelAttribute Candidate candidate,
                              HttpServletRequest request, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        try {
            candidate.setElectionId(electionId);
            candidateService.addCandidate(candidate);
            
            Election election = electionService.findById(electionId).orElse(null);
            if (election != null) {
                election.addCandidate(candidate.getId());
                electionService.updateElection(election);
            }
            
            auditService.logAction(admin.getId(), "ADMIN", "CANDIDATE_ADDED",
                    "Candidate added: " + candidate.getName(), request.getRemoteAddr());
            
            return "redirect:/admin/election/" + electionId + "/candidates";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/add-candidate";
        }
    }
    
    @GetMapping("/election/{electionId}/results")
    public String viewResults(@PathVariable String electionId, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        Election election = electionService.findById(electionId).orElse(null);
        if (election == null) return "redirect:/admin/elections";
        
        List<Candidate> candidates = candidateService.getCandidatesByElectionSorted(electionId);
        long totalVotes = voteService.getVoteCountByElection(electionId);
        
        model.addAttribute("election", election);
        model.addAttribute("candidates", candidates);
        model.addAttribute("totalVotes", totalVotes);
        
        return "admin/results";
    }
    
    @PostMapping("/election/{electionId}/declare-results")
    public String declareResults(@PathVariable String electionId, HttpServletRequest request, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        try {
            electionService.declareResults(electionId, admin.getId());
            auditService.logAction(admin.getId(), "ADMIN", "RESULTS_DECLARED",
                    "Results declared for election: " + electionId, request.getRemoteAddr());
            return "redirect:/admin/election/" + electionId + "/results";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/results";
        }
    }
    
    @PostMapping("/election/{electionId}/lock")
    public String lockElection(@PathVariable String electionId, HttpServletRequest request, Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        try {
            electionService.lockElection(electionId);
            auditService.logAction(admin.getId(), "ADMIN", "ELECTION_LOCKED",
                    "Election locked: " + electionId, request.getRemoteAddr());
            return "redirect:/admin/elections";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/elections";
        }
    }
    
    @GetMapping("/voting-activity")
    public String viewVotingActivity(Model model) {
        try {
            User admin = getCurrentUser();
            if (admin == null) return "redirect:/login";
            
            // Get recent activities (last 50)
            List<com.example.online_voting.model.AuditLog> recentActivities = auditService.getRecentVotingActivities(50);
            if (recentActivities == null) recentActivities = new java.util.ArrayList<>();
            
            // Calculate overall statistics
            List<User> allVoters = userService.findByRole(com.example.online_voting.model.UserRole.VOTER);
            if (allVoters == null) allVoters = new java.util.ArrayList<>();
            
            List<Election> allElections = electionService.getAllElections();
            if (allElections == null) allElections = new java.util.ArrayList<>();
            
            List<Election> activeElections = electionService.getActiveElections();
            if (activeElections == null) activeElections = new java.util.ArrayList<>();
            
            List<Election> completedElections = electionService.getCompletedElections();
            if (completedElections == null) completedElections = new java.util.ArrayList<>();
            
            long totalVotes = 0;
            long resultsDeclaredCount = 0;
            for (Election e : allElections) {
                long votes = voteService.getVoteCountByElection(e.getId());
                totalVotes += votes;
                if (e.getStatus() != null && e.getStatus().toString().equals("RESULTS_DECLARED")) {
                    resultsDeclaredCount++;
                }
            }
            
            // Get election-wise activities
            List<java.util.Map<String, Object>> electionActivities = new java.util.ArrayList<>();
            int totalVotersCount = allVoters.size();
            
            for (Election election : allElections) {
                long voteCount = voteService.getVoteCountByElection(election.getId());
                double turnoutPercent = 0.0;
                
                // Prevent division by zero
                if (totalVotersCount > 0) {
                    turnoutPercent = (voteCount * 100.0) / totalVotersCount;
                }
                
                java.util.Map<String, Object> activityMap = new java.util.HashMap<>();
                activityMap.put("electionName", election.getName() != null ? election.getName() : "Unknown");
                activityMap.put("status", election.getStatus() != null ? election.getStatus() : "DRAFT");
                activityMap.put("voteCount", voteCount);
                activityMap.put("totalVoters", totalVotersCount);
                activityMap.put("turnoutPercent", String.format("%.2f", turnoutPercent));
                activityMap.put("lastActivity", java.time.LocalDateTime.now());
                
                electionActivities.add(activityMap);
            }
            
            // Count pending voters (safe call)
            List<User> pendingVoters = userService.getPendingVoters();
            if (pendingVoters == null) pendingVoters = new java.util.ArrayList<>();
            
            // Calculate overall turnout (prevent division by zero)
            double overallTurnout = 0.0;
            if (totalVotersCount > 0) {
                overallTurnout = (totalVotes * 100.0) / totalVotersCount;
            }
            
            model.addAttribute("recentActivities", recentActivities);
            model.addAttribute("electionActivities", electionActivities);
            model.addAttribute("totalVotes", totalVotes);
            model.addAttribute("activeElections", activeElections.size());
            model.addAttribute("totalVoters", totalVotersCount);
            model.addAttribute("overallTurnout", String.format("%.2f", overallTurnout));
            model.addAttribute("pendingVoters", pendingVoters.size());
            model.addAttribute("completedElections", completedElections.size());
            model.addAttribute("resultsDeclared", resultsDeclaredCount);
            
            return "admin/voting-activity";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/dashboard?error=Failed to load voting activity";
        }
    }
    
    @GetMapping("/audit-logs")
    public String viewAuditLogs(Model model) {
        User admin = getCurrentUser();
        if (admin == null) return "redirect:/login";
        
        List<com.example.online_voting.model.AuditLog> logs = auditService.getAllAuditLogs();
        model.addAttribute("logs", logs);
        
        return "admin/audit-logs";
    }
    
    private double calculateTurnout(int totalVoters, long totalVotes) {
        if (totalVoters == 0) return 0;
        return (totalVotes * 100.0) / totalVoters;
    }
}
