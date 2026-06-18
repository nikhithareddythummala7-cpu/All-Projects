package com.streamy.repository;

import com.streamy.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {
    Optional<Movie> findByTitle(String title);
    List<Movie> findByGenreIn(List<String> genres);
    List<Movie> findByIsPremium(boolean isPremium);
}
