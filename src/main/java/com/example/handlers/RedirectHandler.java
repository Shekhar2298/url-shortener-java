package com.example.handlers;

import com.example.db.DatabaseManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RedirectHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath(); // "/r/8014fd"

        // Remove "/r/" prefix
        if (!path.startsWith("/r/")) {
            send404(exchange);
            return;
        }

        String shortPath = path.substring("/r/".length()); // "8014fd"
        String originalUrl = null;

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT long_url FROM urls WHERE short_url = ?");
            ps.setString(1, shortPath);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                originalUrl = rs.getString("long_url");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (originalUrl != null) {
            exchange.getResponseHeaders().set("Location", originalUrl);
            exchange.sendResponseHeaders(302, -1); // Redirect
        } else {
            send404(exchange);
        }
    }

    private void send404(HttpExchange exchange) throws IOException {
        String notFound = "404 - URL not found";
        exchange.sendResponseHeaders(404, notFound.length());
        exchange.getResponseBody().write(notFound.getBytes());
        exchange.getResponseBody().close();
    }
}
