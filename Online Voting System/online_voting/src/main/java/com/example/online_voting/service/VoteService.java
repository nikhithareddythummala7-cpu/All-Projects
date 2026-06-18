package com.example.online_voting.service;

import com.example.online_voting.model.Vote;
import com.example.online_voting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VoteService {
    
    @Autowired
    private VoteRepository voteRepository;
    
    public Vote castVote(Vote vote) {
        // Check if voter has already voted in this election
        Optional<Vote> existingVote = voteRepository.findByVoterIdAndElectionId(
                vote.getVoterId(), 
                vote.getElectionId()
        );
        
        if (existingVote.isPresent()) {
            throw new RuntimeException("Voter has already voted in this election");
        }
        
        vote.setCastDateTime(LocalDateTime.now());
        vote.setVerified(false);
        return voteRepository.save(vote);
    }
    
    public Optional<Vote> findByVoterAndElection(String voterId, String electionId) {
        return voteRepository.findByVoterIdAndElectionId(voterId, electionId);
    }
    
    public boolean hasVoted(String voterId, String electionId) {
        return voteRepository.findByVoterIdAndElectionId(voterId, electionId).isPresent();
    }
    
    public List<Vote> getVotesByElection(String electionId) {
        return voteRepository.findByElectionId(electionId);
    }
    
    public List<Vote> getVotesByCandidate(String candidateId) {
        return voteRepository.findByCandidateId(candidateId);
    }
    
    public long getVoteCountByElection(String electionId) {
        return voteRepository.countByElectionId(electionId);
    }
    
    public long getVoteCountByCandidate(String candidateId) {
        return voteRepository.countByCandidateId(candidateId);
    }
    
    public Vote verifyVote(String voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote not found"));
        vote.setVerified(true);
        return voteRepository.save(vote);
    }
}
