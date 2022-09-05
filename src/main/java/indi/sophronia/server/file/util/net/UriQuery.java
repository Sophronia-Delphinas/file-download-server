package indi.sophronia.server.file.util.net;

import indi.sophronia.server.file.util.io.ChunkOutputBuffer;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UriQuery {
    private final Map<String, List<String>> queries = new HashMap<>();

    public UriQuery(URI uri) throws UnsupportedEncodingException {
        String raw = uri.getRawQuery();
        if (raw == null) {
            return;
        }

        String[] pairs = raw.split("&");

        for (String pair : pairs) {
            String[] kv = pair.split("=");
            String key = kv[0];
            String value = kv[1];
            addPair(URLDecoder.decode(key, StandardCharsets.UTF_8.name()),
                    URLDecoder.decode(value, StandardCharsets.UTF_8.name()));
        }
    }

    private void addPair(String key, String value) {
        queries.compute(key, (s, strings) -> {
            List<String> list = strings == null ? new ArrayList<>() : strings;
            list.add(value);
            return list;
        });
    }

    public List<String> getValues(String key) {
        return queries.getOrDefault(key, Collections.emptyList());
    }

    public String getFirstValue(String key) {
        List<String> values = getValues(key);
        return values.isEmpty() ? null : values.get(0);
    }

    @Override
    public String toString() {
        ChunkOutputBuffer chunkOutputBuffer = new ChunkOutputBuffer();
        queries.forEach((k, vs) -> {
            chunkOutputBuffer.write(k.getBytes(StandardCharsets.UTF_8));
            chunkOutputBuffer.write('=');
            chunkOutputBuffer.write('[');
            Iterator<String> iterator = vs.iterator();
            while (iterator.hasNext()) {
                String v = iterator.next();
                chunkOutputBuffer.write(v.getBytes(StandardCharsets.UTF_8));
                if (iterator.hasNext()) {
                    chunkOutputBuffer.write(',');
                }
            }
            chunkOutputBuffer.write(']');
            chunkOutputBuffer.write('\n');
        });
        return chunkOutputBuffer.outputToString(StandardCharsets.UTF_8);
    }
}
