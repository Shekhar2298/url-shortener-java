package com.example.handlers;

import com.example.db.DatabaseManager;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShortenHandlerTest {

    private ShortenHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ShortenHandler();
    }

    @Test
    void testHandle_SuccessfullyShortensUrl() throws Exception {
        // Mock HttpExchange
        HttpExchange exchange = mock(HttpExchange.class);

        // Setup input JSON
        String inputJson = "{\"longUrl\":\"https://example.com\",\"shortUrl\":\"77434d\"}";
        InputStream is = new ByteArrayInputStream(inputJson.getBytes());
        when(exchange.getRequestBody()).thenReturn(is);
        when(exchange.getRequestMethod()).thenReturn("POST");

        // Mock output stream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);

        Headers headers = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(headers);

        // Mock database connection and statement
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(conn.prepareStatement(any(String.class))).thenReturn(stmt);

        // Mock static method (DatabaseManager.getConnection)
        try (var mocked = Mockito.mockStatic(DatabaseManager.class)) {
            mocked.when(DatabaseManager::getConnection).thenReturn(conn);

            // Execute handler
            handler.handle(exchange);

            // Verify DB interaction
            verify(conn).prepareStatement("INSERT INTO urls (short_url, long_url) VALUES (?, ?)");
            verify(stmt).setString(1, "77434d");
            verify(stmt).setString(2, "https://example.com");
            verify(stmt).executeUpdate();

            // Output check
            String result = os.toString();
            assertTrue(result.contains("77434d"));
            assertTrue(result.contains("shortUrl"));
        }
    }

    @Test
    void testHandle_MethodNotAllowed() throws Exception {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("GET");

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(405, -1); // Method Not Allowed
    }
}
