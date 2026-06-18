package com.streamy.repository;

import com.streamy.model.WatchHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WatchHistoryRepository extends MongoRepository<WatchHistory, String> {
    List<WatchHistory> findByUserId(String userId);
    List<WatchHistory> findByMovieId(String movieId);
}
