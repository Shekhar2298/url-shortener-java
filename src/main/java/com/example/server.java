package com.example;

import com.example.handlers.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class server {
    public static HttpServer createServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Serve frontend files
        server.createContext("/", new StaticFileHandler("src/main/resources/static"));

        // API endpoints
        server.createContext("/shorten", new ShortenHandler());
        server.createContext("/r", new RedirectHandler());
        server.createContext("/register", new RegisterHandler());
        server.createContext("/login", new LoginHandler());

        return server;
    }
}
