package com.ptit.internship.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ptit.internship.model.Movie;
import com.ptit.internship.util.DatabaseManager;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MovieRepository {

    private static final String FIND_BY_SOURCE_URL_SQL = """
            SELECT source_url, title, production_year, country,
                   genres_json, directors_json, actors_json
            FROM movies
            WHERE source_url = ?
            """;

    private static final Type STRING_LIST_TYPE = new TypeToken<List<String>>() { }.getType();

    private final DatabaseManager databaseManager;
    private final Gson gson;

    public MovieRepository(DatabaseManager databaseManager, Gson gson) {
        this.databaseManager = databaseManager;
        this.gson = gson;
    }

    public Optional<Movie> findBySourceUrl(String sourceUrl) throws SQLException {
        try (Connection connection = databaseManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_SOURCE_URL_SQL)) {

            statement.setString(1, sourceUrl);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapMovie(resultSet));
            }
        }
    }

    private Movie mapMovie(ResultSet resultSet) throws SQLException {
        int yearValue = resultSet.getInt("production_year");
        Integer productionYear = resultSet.wasNull() ? null : yearValue;

        return new Movie(
                resultSet.getString("source_url"),
                resultSet.getString("title"),
                productionYear,
                resultSet.getString("country"),
                parseList(resultSet.getString("genres_json")),
                parseList(resultSet.getString("directors_json")),
                parseList(resultSet.getString("actors_json"))
        );
    }

    private List<String> parseList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        List<String> values = gson.fromJson(json, STRING_LIST_TYPE);
        return values == null ? List.of() : values;
    }
}
