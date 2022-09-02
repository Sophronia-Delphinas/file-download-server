package indi.sophronia.server.file.controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import indi.sophronia.server.file.config.PropertyHandler;
import indi.sophronia.server.file.util.data.UriQuery;
import indi.sophronia.server.file.util.io.ChunkOutputBuffer;
import indi.sophronia.server.file.util.io.StreamReader;

import java.io.*;

public class DownloadHandler extends DefaultHttpHandler {
    @Override
    public ChunkOutputBuffer responseBuffer(HttpExchange exchange) throws IOException {
        UriQuery query = new UriQuery(exchange.getRequestURI());
        String path = query.getFirstValue("path");

        if (path == null) {
            throw new IllegalArgumentException("File path is not specified");
        }

        String basePath = PropertyHandler.getBasePath();
        StringBuilder pathBuilder = new StringBuilder(basePath.length() + path.length() + 1);
        pathBuilder.append(basePath);
        if (!path.startsWith("\\") || !path.startsWith("/")) {
            pathBuilder.append("\\");
        }
        pathBuilder.append(path);

        File file = new File(pathBuilder.toString());
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
