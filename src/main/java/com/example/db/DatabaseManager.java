package com.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:~/testdb";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, "sa", "");
    }

    public static void initDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS urls (
                    id IDENTITY PRIMARY KEY,
                    short_url VARCHAR(20) UNIQUE,
                    long_url VARCHAR(2048),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """;
            String userTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id IDENTITY PRIMARY KEY,
                    username VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL
                );
            """;
            stmt.execute(userTable);
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}