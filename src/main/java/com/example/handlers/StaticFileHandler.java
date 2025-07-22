package com.example.handlers;

import com.sun.net.httpserver.*;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class StaticFileHandler implements HttpHandler {

    private final String baseDir;
    private static final Map<String, String> mimeTypes = new HashMap<>();

    static {
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
    }

    public StaticFileHandler(String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/") || path.equals("/register")) {
            path = "/register.html";
        } else if (path.equals("/register")) {
            path = "/register.html";
        } else if (path.equals("/login")) {
            path = "/login.html";
        } else if (path.equals("/dashboard")) {
            path = "/dashboard.html";
        }

        path = URLDecoder.decode(path, "UTF-8");
        File file = new File(baseDir + path).getCanonicalFile();

        System.out.println("Requested path: " + path);
        System.out.println("Serving file: " + file.getAbsolutePath());

        if (!file.exists() || !file.getPath().startsWith(new File(baseDir).getCanonicalPath())) {
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().close();
            return;
        }

        String ext = getFileExtension(file.getName());
        String mimeType = mimeTypes.getOrDefault(ext, "application/octet-stream");
        exchange.getResponseHeaders().set("Content-Type", mimeType);

        byte[] bytes = Files.readAllBytes(file.toPath());
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private String getFileExtension(String name) {
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex == -1) ? "" : name.substring(dotIndex + 1);
    }

}
