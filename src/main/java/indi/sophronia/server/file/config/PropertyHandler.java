package indi.sophronia.server.file.config;

import indi.sophronia.server.file.util.Lazy;
import indi.sophronia.server.file.util.ThrowUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyHandler {
    private static final Properties PROPERTIES = new Properties();
    static {
        try {
            PROPERTIES.load(new FileInputStream("server.properties"));
        } catch (IOException e) {
            throw ThrowUtil.sneakyThrow(e);
        }
    }

    private static final Lazy<Integer> port =
            new Lazy<>(() -> Integer.parseInt(PROPERTIES.getProperty("server.port")));
    public static int getPort() {
        return port.get();
    }

    private static final Lazy<String> basePath =
            new Lazy<>(() -> {
                String v = PROPERTIES.getProperty("path.base");
                if (v == null || v.length() == 0) {
                    throw new IllegalStateException("Base file path is not specified");
                }
                if (v.endsWith("\\") || v.endsWith("/")) {
                    return v.substring(0, v.length() - 1);
                } else {
                    return v;
                }
            });
    public static String getBasePath() {
        return basePath.get();
    }
}
