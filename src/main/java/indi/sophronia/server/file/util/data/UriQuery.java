package indi.sophronia.server.file.util.data;

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
}
