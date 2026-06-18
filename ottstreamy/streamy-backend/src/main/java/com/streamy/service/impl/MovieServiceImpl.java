package com.streamy.service.impl;

import com.streamy.model.Movie;
import com.streamy.repository.MovieRepository;
import com.streamy.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie getMovieById(String id) {
        return movieRepository.findById(id).orElse(null);
    }

    @Override
    public List<Movie> searchMovies(String query) {
        return movieRepository.findAll().stream()
                .filter(m -> m.getTitle().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    @Override
    public List<Movie> filterByGenre(List<String> genres) {
        return movieRepository.findByGenreIn(genres);
    }

    @Override
    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie updateMovie(String id, Movie movie) {
        movie.setId(id);
        return movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(String id) {
        movieRepository.deleteById(id);
    }
}
