package com.example;

import com.example.server;
import org.h2.tools.Server;
import com.example.db.DatabaseManager;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {

        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
        System.out.println("H2 Console is running at http://localhost:8082");


        Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();

        DatabaseManager.initDatabase();

        HttpServer server1 = server.createServer(8000);
        server1.start();
        System.out.println("Server running at http://localhost:8000");
    }
}
