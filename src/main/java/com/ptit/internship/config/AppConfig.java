package com.ptit.internship.config;

import java.nio.file.Path;

public record AppConfig(int port, Path databasePath) {

    private static final int DEFAULT_PORT = 8080;
    private static final Path DEFAULT_DATABASE_PATH = Path.of("data", "movies.db");

    public static AppConfig fromArgs(String[] args) {
        int port = DEFAULT_PORT;
        Path databasePath = DEFAULT_DATABASE_PATH;

        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                port = parsePort(arg.substring("--port=".length()));
            } else if (arg.startsWith("--db=")) {
                String value = arg.substring("--db=".length()).trim();
                if (value.isEmpty()) {
                    throw new IllegalArgumentException("Database path must not be empty");
                }
                databasePath = Path.of(value);
            } else {
                throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }

        return new AppConfig(port, databasePath);
    }

    private static int parsePort(String value) {
        try {
            int port = Integer.parseInt(value);
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Port must be between 1 and 65535");
            }
            return port;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port: " + value, e);
        }
    }
}
