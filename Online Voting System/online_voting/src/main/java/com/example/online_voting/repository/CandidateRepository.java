package com.example.online_voting.repository;

import com.example.online_voting.model.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends MongoRepository<Candidate, String> {
    List<Candidate> findByElectionId(String electionId);
    List<Candidate> findByElectionIdOrderByVoteCountDesc(String electionId);
}
