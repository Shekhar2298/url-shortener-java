


package com.example.handlers;

import com.example.db.DatabaseManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class ShortenHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(ShortenHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.info("Received request: {}", exchange.getRequestMethod());

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            logger.warn("Unsupported method: {}", exchange.getRequestMethod());
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        JsonObject jsonResponse = new JsonObject();

        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(isr).getAsJsonObject();

            String longUrl = json.get("longUrl").getAsString();
            String customShort = json.has("shortUrl") ? json.get("shortUrl").getAsString() : "";

            logger.info("Long URL received: {}", longUrl);
            if (!customShort.isEmpty()) {
                logger.info("Custom short URL requested: {}", customShort);
            }

            String shortUrl = customShort.isEmpty() ? UUID.randomUUID().toString().substring(0, 6) : customShort;

            try (Connection conn = DatabaseManager.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO urls (short_url, long_url) VALUES (?, ?)");
                ps.setString(1, shortUrl);
                ps.setString(2, longUrl);
                ps.executeUpdate();

                logger.info("Short URL successfully stored: {}", shortUrl);

                jsonResponse.addProperty("shortUrl", "r/" + shortUrl);
            } catch (Exception e) {
                logger.error("Database error while inserting URL: {}", e.getMessage(), e);
                jsonResponse.addProperty("error", "Unable to shorten URL. Try again.");
                sendJsonResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, jsonResponse);
                return;
            }

            sendJsonResponse(exchange, HttpURLConnection.HTTP_OK, jsonResponse);

        } catch (Exception e) {
            logger.error("Invalid request or JSON parsing failed: {}", e.getMessage(), e);
            jsonResponse.addProperty("error", "Invalid input.");
            sendJsonResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, jsonResponse);
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, JsonObject response) throws IOException {
        String jsonString = response.toString();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonString.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonString.getBytes());
        }
    }
}
