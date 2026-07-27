package com.ptit.internship.web;

import com.google.gson.Gson;
import com.ptit.internship.model.Movie;
import com.ptit.internship.service.MovieService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class MovieHandler implements HttpHandler {

    private static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";

    private final MovieService movieService;
    private final Gson gson;

    public MovieHandler(MovieService movieService, Gson gson) {
        this.movieService = movieService;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");
                sendJson(exchange, 405, new ApiError(405, "METHOD_NOT_ALLOWED", "Only GET is supported"));
                return;
            }

            Map<String, String> query = QueryParameters.parse(exchange.getRequestURI().getRawQuery());
            String sourceUrl = query.get("url");

            if (sourceUrl == null || sourceUrl.isBlank()) {
                sendJson(exchange, 400,
                        new ApiError(400, "MISSING_URL", "Query parameter 'url' is required"));
                return;
            }

            if (!isValidMovieUrl(sourceUrl)) {
                sendJson(exchange, 400,
                        new ApiError(400, "INVALID_URL", "The url must be an absolute http/https URL"));
                return;
            }

            Optional<Movie> movie = movieService.findBySourceUrl(sourceUrl);
            if (movie.isEmpty()) {
                sendJson(exchange, 404,
                        new ApiError(404, "MOVIE_NOT_FOUND", "No crawled movie matches the supplied URL"));
                return;
            }

            sendJson(exchange, 200, movie.get());
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, new ApiError(400, "BAD_REQUEST", e.getMessage()));
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, 500,
                    new ApiError(500, "DATABASE_ERROR", "Could not read movie data"));
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500,
                    new ApiError(500, "INTERNAL_ERROR", "Unexpected server error"));
        } finally {
            exchange.close();
        }
    }

    private boolean isValidMovieUrl(String value) {
        try {
            URI uri = URI.create(value);
            String scheme = uri.getScheme();
            return uri.isAbsolute()
                    && uri.getHost() != null
                    && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] response = gson.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", CONTENT_TYPE_JSON);
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
