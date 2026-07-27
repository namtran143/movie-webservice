package com.ptit.internship.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private final Path databasePath;

    public DatabaseManager(Path databasePath) {
        this.databasePath = databasePath.toAbsolutePath().normalize();
    }

    public void validate() {
        if (!Files.isRegularFile(databasePath)) {
            throw new IllegalArgumentException("Database file not found: " + databasePath);
        }
    }

    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + databasePath);
    }

    public Path getDatabasePath() {
        return databasePath;
    }
}
