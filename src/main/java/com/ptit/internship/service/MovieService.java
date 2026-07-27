package com.ptit.internship.service;

import java.sql.SQLException;
import java.util.Optional;

import com.ptit.internship.model.Movie;
import com.ptit.internship.repository.MovieRepository;

public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Optional<Movie> findBySourceUrl(String sourceUrl) throws SQLException {
        Optional<Movie> movie = movieRepository.findBySourceUrl(sourceUrl);
        movie.ifPresent(this::inspectActorsForDebug);
        return movie;
    }

    private void inspectActorsForDebug(Movie movie) {
        for (String actor : movie.actors()) {
            String actorName = actor.trim();
            int actorNameLength = actorName.length();
        }
    }
}