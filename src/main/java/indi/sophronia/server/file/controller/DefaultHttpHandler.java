package indi.sophronia.server.file.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import indi.sophronia.server.file.config.PropertyHandler;
import indi.sophronia.server.file.util.io.ChunkOutputBuffer;
import indi.sophronia.server.file.util.net.NetContext;
import indi.sophronia.server.file.util.net.UriQuery;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public abstract class DefaultHttpHandler implements HttpHandler {
    private final Runnable callback;

    protected DefaultHttpHandler() {
        this.callback = () -> {};
    }
    protected DefaultHttpHandler(Runnable callback) {
        this.callback = callback;
    }

    protected boolean authenticate() {
        UriQuery uriQuery = NetContext.getQuery();
        String u = uriQuery.getFirstValue("u");
        String p = uriQuery.getFirstValue("p");
        String username = PropertyHandler.username();
        String password = PropertyHandler.password();
        return username.equals(u) && password.equals(p);
    }

    protected abstract ChunkOutputBuffer response(HttpExchange exchange) throws IOException;

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        NetContext.setExchange(exchange);

        boolean authenticated = authenticate();
        if (authenticated) {
            ChunkOutputBuffer buffer;
            try {
                buffer = response(exchange);
            } catch (Exception e) {
                buffer = new ChunkOutputBuffer();
                e.printStackTrace(new PrintWriter(buffer, true));
                e.printStackTrace();
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "text/plain; charset=utf-8");
            }
            exchange.sendResponseHeaders(200, buffer.totalLength());
            buffer.write(exchange.getResponseBody());
        } else {
            byte[] bytes = "authenticate failed".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(401, bytes.length);
            exchange.getResponseBody().write(bytes);
        }
        exchange.getResponseBody().close();

        if (authenticated) {
            callback.run();
        }
    }
}
