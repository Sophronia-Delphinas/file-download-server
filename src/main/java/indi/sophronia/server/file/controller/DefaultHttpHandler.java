package indi.sophronia.server.file.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import indi.sophronia.server.file.util.io.ChunkOutputBuffer;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class DefaultHttpHandler implements HttpHandler {
    protected abstract ChunkOutputBuffer responseBuffer(HttpExchange exchange) throws IOException;

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        ChunkOutputBuffer buffer;
        try {
            buffer = responseBuffer(exchange);
        } catch (Exception e) {
            buffer = new ChunkOutputBuffer();
            e.printStackTrace(new PrintWriter(buffer, true));
            e.printStackTrace();
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "text/plain; charset=utf-8");
        }
        exchange.sendResponseHeaders(200, buffer.totalLength());
        buffer.write(exchange.getResponseBody());
        exchange.getResponseBody().close();
    }
}
