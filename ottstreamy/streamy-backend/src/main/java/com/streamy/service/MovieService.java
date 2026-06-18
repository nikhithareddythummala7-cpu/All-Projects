package com.streamy.service;

import com.streamy.model.Movie;
import java.util.List;

public interface MovieService {
    List<Movie> getAllMovies();
    Movie getMovieById(String id);
    List<Movie> searchMovies(String query);
    List<Movie> filterByGenre(List<String> genres);
    Movie addMovie(Movie movie);
    Movie updateMovie(String id, Movie movie);
    void deleteMovie(String id);
}
