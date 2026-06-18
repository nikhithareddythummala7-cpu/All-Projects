package com.example.online_voting.repository;

import com.example.online_voting.model.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends MongoRepository<Vote, String> {
    Optional<Vote> findByVoterIdAndElectionId(String voterId, String electionId);
    List<Vote> findByElectionId(String electionId);
    List<Vote> findByCandidateId(String candidateId);
    long countByElectionId(String electionId);
    long countByCandidateId(String candidateId);
}
