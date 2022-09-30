package indi.sophronia.server.file.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import indi.sophronia.server.file.util.io.ChunkOutputBuffer;
import indi.sophronia.server.file.util.io.StreamReader;
import indi.sophronia.server.file.util.net.NetContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ShutdownHandler extends DefaultHttpHandler {
    public ShutdownHandler(Runnable callback) {
        super(callback);
    }

    @Override
    protected ChunkOutputBuffer response(HttpExchange exchange) throws IOException {
        NetContext.setExitMessage(StreamReader.
                readAll(exchange.getRequestBody(), StandardCharsets.UTF_8));
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "text/plain; charset=utf8");
        ChunkOutputBuffer buffer = new ChunkOutputBuffer();
        buffer.write("shutdown success".getBytes(StandardCharsets.UTF_8));
        return buffer;
    }
}
