package indi.sophronia.server.file.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import indi.sophronia.server.file.config.PropertyHandler;
import indi.sophronia.server.file.util.io.ChunkOutputBuffer;
import indi.sophronia.server.file.util.io.StreamReader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DownloadHandler extends DefaultHttpHandler {
    @Override
    public ChunkOutputBuffer response(HttpExchange exchange) throws IOException {
        String path;
        try (InputStream in = exchange.getRequestBody()) {
            path = StreamReader.readAll(in, StandardCharsets.UTF_8);
        }

        if (path == null) {
            throw new IllegalArgumentException("File path is not specified");
        }

        String basePath = PropertyHandler.getFileBasePath();
        StringBuilder pathBuilder = new StringBuilder(basePath.length() + path.length() + 1);
        pathBuilder.append(basePath);
        if (!path.startsWith("\\") || !path.startsWith("/")) {
            pathBuilder.append("\\");
        }
        pathBuilder.append(path);
        String pathValue = pathBuilder.toString();
        if (pathValue.startsWith(PropertyHandler.getCurrentPath().toString())) {
            throw new IllegalArgumentException("Cannot download protected file");
        }

        File file = new File(pathValue);
        ChunkOutputBuffer chunkOutputBuffer = new ChunkOutputBuffer();
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            StreamReader.readAll(in, chunkOutputBuffer);
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/octet-stream");
            headers.add("Content-DisPosition", "attachment;filename=" + file.getName());
        }
        return chunkOutputBuffer;
    }
}
