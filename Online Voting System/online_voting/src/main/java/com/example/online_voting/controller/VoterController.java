package com.example.online_voting.controller;

import com.example.online_voting.model.User;
import com.example.online_voting.model.Vote;
import com.example.online_voting.model.Election;
import com.example.online_voting.model.Candidate;
import com.example.online_voting.service.UserService;
import com.example.online_voting.service.ElectionService;
import com.example.online_voting.service.VoteService;
import com.example.online_voting.service.CandidateService;
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
@RequestMapping("/voter")
public class VoterController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ElectionService electionService;
    
    @Autowired
    private VoteService voteService;
    
    @Autowired
    private CandidateService candidateService;
    
    @Autowired
    private AuditService auditService;
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email).orElse(null);
    }
    
    @GetMapping("/dashboard")
    public String voterDashboard(Model model) {
        User voter = getCurrentUser();
        if (voter == null) return "redirect:/login";
        
        List<Election> activeElections = electionService.getActiveElections();
        List<Election> upcomingElections = electionService.getUpcomingElections();
        List<Election> completedElections = electionService.getCompletedElections();
        
        model.addAttribute("voter", voter);
        model.addAttribute("activeElections", activeElections);
        model.addAttribute("upcomingElections", upcomingElections);
        model.addAttribute("completedElections", completedElections);
        model.addAttribute("votedElectionIds", voter.getVotedElectionIds());
        
        return "voter/dashboard";
    }
    
    @GetMapping("/elections")
    public String viewElections(Model model) {
        User voter = getCurrentUser();
        if (voter == null) return "redirect:/login";
        
        List<Election> elections = electionService.getActiveElections();
        model.addAttribute("elections", elections);
        model.addAttribute("votedElectionIds", voter.getVotedElectionIds());
        
        return "voter/elections";
    }
    
    @GetMapping("/election/{id}")
    public String viewElectionDetails(@PathVariable String id, Model model) {
        User voter = getCurrentUser();
        if (voter == null) return "redirect:/login";
        
        Election election = electionService.findById(id).orElse(null);
        if (election == null) {
            return "redirect:/voter/elections";
        }
        
        List<Candidate> candidates = candidateService.getCandidatesByElection(id);
        boolean hasVoted = voteService.hasVoted(voter.getId(), id);
        boolean canVote = electionService.canVote(id) && !hasVoted;
        
        model.addAttribute("election", election);
        model.addAttribute("candidates", candidates);
        model.addAttribute("hasVoted", hasVoted);
        model.addAttribute("canVote", canVote);
        
        return "voter/election-details";
    }
    
    @GetMapping("/vote/{electionId}/{candidateId}")
    public String voteConfirmation(@PathVariable String electionId, @PathVariable String candidateId, Model model) {
        User voter = getCurrentUser();
        if (voter == null) return "redirect:/login";
        
        Election election = electionService.findById(electionId).orElse(null);
        Candidate candidate = candidateService.findById(candidateId).orElse(null);
        
        if (election == null || candidate == null) {
            return "redirect:/voter/elections";
        }
        
        if (voteService.hasVoted(voter.getId(), electionId)) {
            model.addAttribute("error", "You have already voted in this election");
            return "voter/election-details";
        }
        
        model.addAttribute("election", election);
        model.addAttribute("candidate", candidate);
        model.addAttribute("voter", voter);
        
        return "voter/vote-confirmation";
    }
    
    @PostMapping("/vote/submit")
    public String submitVote(@RequestParam String electionId, @RequestParam String candidateId, 
                            Model model, HttpServletRequest request) {
        User voter = getCurrentUser();
        if (voter == null) return "redirect:/login";
        
        try {
            if (!electionService.canVote(electionId)) {
                throw new RuntimeException("Election is not active for voting");
            }
            
            if (voteService.hasVoted(voter.getId(), electionId)) {
                throw new RuntimeException("You have already voted in this election");
            }
            
            Vote vote = new Vote();
            vote.setVoterId(voter.getId());
            vote.setElectionId(electionId);
            vote.setCandidateId(candidateId);
            vote.setIpAddress(request.getRemoteAddr());
            vote.setDeviceInfo(request.getHeader("User-Agent"));
            
            voteService.castVote(vote);
            candidateService.incrementVoteCount(candidateId);
            voter.addVotedElection(electionId);
            userService.updateLastLogin(voter.getId());
            
            auditService.logAction(voter.getId(), "VOTER", "VOTE_CAST",
                    "Vote cast in election: " + electionId + " for candidate: " + candidateId,
                    request.getRemoteAddr());
            
            model.addAttribute("message", "Vote submitted successfully!");
            return "voter/vote-success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "voter/vote-error";
        }
    }
    
    @GetMapping("/results/{electionId}")
    public String viewResults(@PathVariable String electionId, Model model) {
        Election election = electionService.findById(electionId).orElse(null);
        if (election == null) {
            return "redirect:/voter/elections";
        }
        
        List<Candidate> candidates = candidateService.getCandidatesByElectionSorted(electionId);
        long totalVotes = voteService.getVoteCountByElection(electionId);
        
        model.addAttribute("election", election);
        model.addAttribute("candidates", candidates);
        model.addAttribute("totalVotes", totalVotes);
        
        return "voter/results";
    }
    
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        User voter = getCurrentUser();
        if (voter == null) return "redirect:/login";
        
        model.addAttribute("voter", voter);
        return "voter/profile";
    }
    
    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "voter/change-password";
    }
    
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String confirmPassword, 
                                Model model) {
        User voter = getCurrentUser();
        if (voter == null) return "redirect:/login";
        
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "voter/change-password";
        }
        
        try {
            userService.changePassword(voter.getId(), newPassword);
            model.addAttribute("message", "Password changed successfully");
            return "voter/change-password";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "voter/change-password";
        }
    }
}
