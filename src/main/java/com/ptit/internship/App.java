package com.ptit.internship;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ptit.internship.config.AppConfig;
import com.ptit.internship.repository.MovieRepository;
import com.ptit.internship.service.MovieService;
import com.ptit.internship.util.DatabaseManager;
import com.ptit.internship.web.HealthHandler;
import com.ptit.internship.web.MovieHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class App {

    public static void main(String[] args) {
        try {
            AppConfig config = AppConfig.fromArgs(args);
            DatabaseManager databaseManager = new DatabaseManager(config.databasePath());
            databaseManager.validate();

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create();

            MovieRepository repository = new MovieRepository(databaseManager, gson);
            MovieService service = new MovieService(repository);

            HttpServer server = HttpServer.create(new InetSocketAddress(config.port()), 0);
            server.createContext("/movie", new MovieHandler(service, gson));
            server.createContext("/health", new HealthHandler());
            server.setExecutor(Executors.newFixedThreadPool(4));
            server.start();

            System.out.println("Movie webservice started");
            System.out.println("Port: " + config.port());
            System.out.println("Database: " + databaseManager.getDatabasePath());
            System.out.println("Health: http://localhost:" + config.port() + "/health");
            System.out.println("Movie API: GET /movie?url=<encoded-movie-url>");
        } catch (Exception e) {
            System.err.println("Application failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
