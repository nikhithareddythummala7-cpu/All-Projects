package com.example.online_voting.controller;

import com.example.online_voting.model.User;
import com.example.online_voting.model.Election;
import com.example.online_voting.model.Candidate;
import com.example.online_voting.service.UserService;
import com.example.online_voting.service.AuditService;
import com.example.online_voting.service.ElectionService;
import com.example.online_voting.service.VoteService;
import com.example.online_voting.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/officer")
public class OfficerController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private ElectionService electionService;
    
    @Autowired
    private VoteService voteService;
    
    @Autowired
    private CandidateService candidateService;
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email).orElse(null);
    }
    
    @GetMapping("/dashboard")
    public String officerDashboard(Model model) {
        try {
            User officer = getCurrentUser();
            if (officer == null) return "redirect:/login";
            
            List<User> pendingVoters = userService.getPendingVoters();
            if (pendingVoters == null) pendingVoters = new java.util.ArrayList<>();
            
            List<Election> activeElections = electionService.getActiveElections();
            if (activeElections == null) activeElections = new java.util.ArrayList<>();
            
            // Calculate live voting stats
            List<User> voterList = userService.findByRole(com.example.online_voting.model.UserRole.VOTER);
            if (voterList == null) voterList = new java.util.ArrayList<>();
            
            long totalVoters = voterList.size();
            long votesCount = 0;
            for (Election election : activeElections) {
                votesCount += voteService.getVoteCountByElection(election.getId());
            }
            
            model.addAttribute("officer", officer);
            model.addAttribute("pendingVoters", pendingVoters);
            model.addAttribute("pendingVotersCount", pendingVoters.size());
            model.addAttribute("activeElectionsCount", activeElections.size());
            model.addAttribute("totalVoters", totalVoters);
            model.addAttribute("totalVotes", votesCount);
            
            return "officer/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load dashboard");
            return "officer/dashboard";
        }
    }
    
    @GetMapping("/verify-voters")
    public String verifyVoters(Model model) {
        try {
            User officer = getCurrentUser();
            if (officer == null) return "redirect:/login";
            
            List<User> pendingVoters = userService.getPendingVoters();
            if (pendingVoters == null) pendingVoters = new java.util.ArrayList<>();
            
            model.addAttribute("pendingVoters", pendingVoters);
            
            return "officer/verify-voters";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load voters");
            return "officer/verify-voters";
        }
    }
    
    @PostMapping("/approve-voter/{voterId}")
    public String approveVoter(@PathVariable String voterId, HttpServletRequest request, Model model) {
        User officer = getCurrentUser();
        if (officer == null) return "redirect:/login";
        
        try {
            User voter = userService.approveVoter(voterId);
            auditService.logAction(officer.getId(), "ELECTION_OFFICER", "VOTER_APPROVED",
                    "Voter approved: " + voter.getEmail(), request.getRemoteAddr());
            model.addAttribute("message", "Voter approved successfully");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        
        return "redirect:/officer/verify-voters";
    }
    
    @PostMapping("/reject-voter/{voterId}")
    public String rejectVoter(@PathVariable String voterId, HttpServletRequest request, Model model) {
        User officer = getCurrentUser();
        if (officer == null) return "redirect:/login";
        
        try {
            User voter = userService.rejectVoter(voterId);
            auditService.logAction(officer.getId(), "ELECTION_OFFICER", "VOTER_REJECTED",
                    "Voter rejected: " + voter.getEmail(), request.getRemoteAddr());
            model.addAttribute("message", "Voter rejected successfully");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        
        return "redirect:/officer/verify-voters";
    }
    
    @GetMapping("/monitor-elections")
    public String monitorElections(Model model) {
        try {
            User officer = getCurrentUser();
            if (officer == null) return "redirect:/login";
            
            List<Election> activeElections = electionService.getActiveElections();
            if (activeElections == null) activeElections = new java.util.ArrayList<>();
            
            List<Election> completedElections = electionService.getCompletedElections();
            if (completedElections == null) completedElections = new java.util.ArrayList<>();
            
            model.addAttribute("activeElections", activeElections);
            model.addAttribute("completedElections", completedElections);
            
            return "officer/monitor-elections";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load elections");
            return "officer/monitor-elections";
        }
    }
    
    @GetMapping("/election-progress/{electionId}")
    public String viewElectionProgress(@PathVariable String electionId, Model model) {
        try {
            User officer = getCurrentUser();
            if (officer == null) return "redirect:/login";
            
            Election election = electionService.findById(electionId).orElse(null);
            if (election == null) {
                return "redirect:/officer/monitor-elections";
            }
            
            List<Candidate> candidates = candidateService.getCandidatesByElectionSorted(electionId);
            if (candidates == null) candidates = new java.util.ArrayList<>();
            
            long totalVotes = voteService.getVoteCountByElection(electionId);
            
            List<User> voterList = userService.findByRole(com.example.online_voting.model.UserRole.VOTER);
            if (voterList == null) voterList = new java.util.ArrayList<>();
            long totalVoters = voterList.size();
            
            double turnout = totalVoters > 0 ? (totalVotes * 100.0) / totalVoters : 0;
            
            model.addAttribute("election", election);
            model.addAttribute("candidates", candidates);
            model.addAttribute("totalVotes", totalVotes);
            model.addAttribute("totalVoters", totalVoters);
            model.addAttribute("turnout", String.format("%.2f", turnout));
            
            auditService.logAction(officer.getId(), "ELECTION_OFFICER", "ELECTION_MONITORED",
                    "Monitored election: " + election.getName(), "");
            
            return "officer/election-progress";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load election progress");
            return "officer/election-progress";
        }
    }
    
    @GetMapping("/turnout-report")
    public String viewTurnoutReport(Model model) {
        try {
            User officer = getCurrentUser();
            if (officer == null) return "redirect:/login";
            
            List<Election> allElections = electionService.getAllElections();
            if (allElections == null) allElections = new java.util.ArrayList<>();
            
            List<Election> activeElections = electionService.getActiveElections();
            if (activeElections == null) activeElections = new java.util.ArrayList<>();
            
            List<User> voterList = userService.findByRole(com.example.online_voting.model.UserRole.VOTER);
            if (voterList == null) voterList = new java.util.ArrayList<>();
            long totalVoters = voterList.size();
            
            List<Map<String, Object>> electionStats = new java.util.ArrayList<>();
            
            for (Election election : allElections) {
                long voteCount = voteService.getVoteCountByElection(election.getId());
                double turnoutPercent = totalVoters > 0 ? (voteCount * 100.0) / totalVoters : 0;
                
                Map<String, Object> stat = new HashMap<>();
                stat.put("electionName", election.getName());
                stat.put("electionId", election.getId());
                stat.put("status", election.getStatus());
                stat.put("voteCount", voteCount);
                stat.put("totalVoters", totalVoters);
                stat.put("turnoutPercent", turnoutPercent);
                
                electionStats.add(stat);
            }
            
            // Calculate overall turnout
            long totalVotes = 0;
            for (Election election : allElections) {
                totalVotes += voteService.getVoteCountByElection(election.getId());
            }
            double overallTurnout = totalVoters > 0 ? (totalVotes * 100.0) / totalVoters : 0;
            
            model.addAttribute("electionStats", electionStats);
            model.addAttribute("totalVoters", totalVoters);
            model.addAttribute("totalVotes", totalVotes);
            model.addAttribute("overallTurnout", String.format("%.2f", overallTurnout));
            model.addAttribute("activeElectionsCount", activeElections.size());
            
            auditService.logAction(officer.getId(), "ELECTION_OFFICER", "REPORT_GENERATED",
                    "Voter turnout report generated", "");
            
            return "officer/turnout-report";
        } catch (Exception e) {
            return "redirect:/officer/dashboard?error=Failed to load turnout report";
        }
    }
}
