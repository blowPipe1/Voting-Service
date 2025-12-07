package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;

public class StaticFileHandler implements HttpHandler {
    private final String basePath;

    public StaticFileHandler(String basePath) {
        if (basePath.endsWith(File.separator)) {
            this.basePath = basePath;
        } else {
            this.basePath = basePath + File.separator;
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String requestPath = uri.getPath();
        Path filePath = Paths.get(basePath, requestPath).normalize();
        File file = filePath.toFile();

        if (file.exists() && !file.isDirectory() && filePath.startsWith(Paths.get(basePath).normalize())) {
            String contentType = Files.probeContentType(filePath);
            if (contentType != null) {
                exchange.getResponseHeaders().set("Content-Type", contentType);
            }

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, file.length());
            try (OutputStream os = exchange.getResponseBody()) {
                Files.copy(filePath, os);
            }
        } else {
            String response = "404 Not Found";
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
