package com.example.online_voting.service;

import com.example.online_voting.model.Election;
import com.example.online_voting.model.ElectionStatus;
import com.example.online_voting.repository.ElectionRepository;
import com.example.online_voting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ElectionService {
    
    @Autowired
    private ElectionRepository electionRepository;
    
    @Autowired
    private VoteRepository voteRepository;
    
    public Election createElection(Election election) {
        election.setStatus(ElectionStatus.DRAFT);
        election.setCreatedDate(LocalDateTime.now());
        election.setLocked(false);
        return electionRepository.save(election);
    }
    
    public Optional<Election> findById(String id) {
        return electionRepository.findById(id);
    }
    
    public Election updateElection(Election election) {
        Election existing = electionRepository.findById(election.getId())
                .orElseThrow(() -> new RuntimeException("Election not found"));
        
        if (existing.isLocked()) {
            throw new RuntimeException("Cannot modify locked election");
        }
        
        return electionRepository.save(election);
    }
    
    public List<Election> getActiveElections() {
        LocalDateTime now = LocalDateTime.now();
        List<Election> active = electionRepository.findByStatusOrderByStartDateDesc(ElectionStatus.ACTIVE);
        // also include draft elections whose start/end window contains 'now' so voters can see and vote
        List<Election> drafts = electionRepository.findByStatus(ElectionStatus.DRAFT);
        for (Election draft : drafts) {
            if (draft.getStartDate() != null && draft.getEndDate() != null &&
                !now.isBefore(draft.getStartDate()) && !now.isAfter(draft.getEndDate())) {
                active.add(draft);
            }
        }
        return active;
    }
    
    public List<Election> getUpcomingElections() {
        return electionRepository.findByStartDateAfterAndStatusOrderByStartDateAsc(LocalDateTime.now(), ElectionStatus.DRAFT);
    }
    
    public List<Election> getCompletedElections() {
        return electionRepository.findByStatus(ElectionStatus.COMPLETED);
    }
    
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }
    
    public Election activateElection(String electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        election.setStatus(ElectionStatus.ACTIVE);
        return electionRepository.save(election);
    }
    
    public Election completeElection(String electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        election.setStatus(ElectionStatus.COMPLETED);
        election.setTotalVotesCast(voteRepository.countByElectionId(electionId));
        return electionRepository.save(election);
    }
    
    public Election declareResults(String electionId, String adminId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        election.setStatus(ElectionStatus.RESULTS_DECLARED);
        election.setResultsDeclaredBy(adminId);
        election.setResultsDeclaredDate(LocalDateTime.now());
        return electionRepository.save(election);
    }
    
    public Election lockElection(String electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        election.setLocked(true);
        return electionRepository.save(election);
    }
    
    public boolean canVote(String electionId) {
        Optional<Election> election = electionRepository.findById(electionId);
        if (election.isEmpty()) return false;
        
        Election e = election.get();
        LocalDateTime now = LocalDateTime.now();
        if (e.getStartDate() == null || e.getEndDate() == null) return false;
        boolean withinWindow = !now.isBefore(e.getStartDate()) && !now.isAfter(e.getEndDate());
        // allow voting when election is ACTIVE or when it is still DRAFT but its window contains now
        return (e.getStatus() == ElectionStatus.ACTIVE && withinWindow) || (e.getStatus() == ElectionStatus.DRAFT && withinWindow);
    }
}
