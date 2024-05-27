package org.example.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HtmlHandler implements HttpHandler {
    private static final String HTML_FILE_PATH = "src/main/resources/index.html";
    private static final String CONTENT_TYPE_HTML = "text/html";
    private static final int STATUS_OK = 200;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            byte[] response = Files.readAllBytes(Paths.get(HTML_FILE_PATH));
            exchange.getResponseHeaders().set("Content-Type", CONTENT_TYPE_HTML);
            exchange.sendResponseHeaders(STATUS_OK, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } else {
            exchange.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, -1);
        }
    }
}
