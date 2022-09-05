package indi.sophronia.server.file.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import indi.sophronia.server.file.util.io.ChunkOutputBuffer;
import indi.sophronia.server.file.util.net.NetContext;
import indi.sophronia.server.file.util.net.UriQuery;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class ShutdownHandler extends DefaultHttpHandler {
    private final HttpServer httpServer;
    private final Consumer<HttpServer> callback;

    public ShutdownHandler(HttpServer httpServer, Consumer<HttpServer> callback) {
        this.httpServer = httpServer;
        this.callback = callback;
    }

    @Override
    protected ChunkOutputBuffer responseBuffer(HttpExchange exchange) {
        UriQuery uriQuery = NetContext.getQuery();
        System.out.println(uriQuery);

        callback.accept(httpServer);
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "text/plain; charset=utf8");
        ChunkOutputBuffer buffer = new ChunkOutputBuffer();
        buffer.write("shutdown success".getBytes(StandardCharsets.UTF_8));
        return buffer;
    }
}
