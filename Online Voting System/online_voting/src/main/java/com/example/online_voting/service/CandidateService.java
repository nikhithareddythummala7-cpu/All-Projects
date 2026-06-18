package com.example.online_voting.service;

import com.example.online_voting.model.Candidate;
import com.example.online_voting.repository.CandidateRepository;
import com.example.online_voting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private VoteRepository voteRepository;
    
    public Candidate addCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }
    
    public Optional<Candidate> findById(String id) {
        return candidateRepository.findById(id);
    }
    
    public Candidate updateCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }
    
    public void deleteCandidate(String id) {
        candidateRepository.deleteById(id);
    }
    
    public List<Candidate> getCandidatesByElection(String electionId) {
        return candidateRepository.findByElectionId(electionId);
    }
    
    public List<Candidate> getCandidatesByElectionSorted(String electionId) {
        return candidateRepository.findByElectionIdOrderByVoteCountDesc(electionId);
    }
    
    public Candidate incrementVoteCount(String candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        candidate.incrementVoteCount();
        return candidateRepository.save(candidate);
    }
    
    public long getVoteCount(String candidateId) {
        return voteRepository.countByCandidateId(candidateId);
    }
}
