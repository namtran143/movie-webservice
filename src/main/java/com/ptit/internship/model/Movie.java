package com.ptit.internship.model;

import java.util.List;

public record Movie(
        String sourceUrl,
        String title,
        Integer productionYear,
        String country,
        List<String> genres,
        List<String> directors,
        List<String> actors
) {
    public Movie {
        genres = genres == null ? List.of() : List.copyOf(genres);
        directors = directors == null ? List.of() : List.copyOf(directors);
        actors = actors == null ? List.of() : List.copyOf(actors);
    }
}
