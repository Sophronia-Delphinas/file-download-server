package indi.sophronia.server.file.config;

import indi.sophronia.server.file.util.Lazy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class PropertyHandler {
    private static final Properties PROPERTIES = new Properties();

    public static void load(String path) throws IOException {
        Properties properties = new Properties();
        properties.load(new BufferedInputStream(new FileInputStream(path)));
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String stringValue = String.valueOf(entry.getValue());
            for (int i = 0; i < stringValue.length(); i++) {
                if (Character.isWhitespace(stringValue.charAt(i))) {
                    throw new IllegalArgumentException("property value contains white space: " + entry.getKey() + "=" + stringValue);
                }
            }
        }
        PROPERTIES.putAll(properties);
    }

    public static void loadDirectory(String path) throws IOException {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            throw new IOException("directory not exists: " + path);
        }
        for (File listFile : files) {
            if (listFile.isDirectory()) {
                loadDirectory(listFile.getPath());
            } else if (listFile.getPath().endsWith(".properties")) {
                load(listFile.getPath());
            }
        }
    }

    private static final Lazy<Integer> PORT =
            new Lazy<>(() -> Integer.parseInt(getRequiredProperty("server.port")));
    public static int getPort() {
        return PORT.get();
    }

    private static final Lazy<String> FILE_BASE_PATH =
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
    public static String getFileBasePath() {
        return FILE_BASE_PATH.get();
    }

    private static final Lazy<String> USER_NAME =
            new Lazy<>(() -> {
                String v = PROPERTIES.getProperty("username");
                if (v == null || v.length() == 0) {
                    throw new IllegalStateException("Username is not specified");
                }
                return v;
            });
    public static String getUserName() {
        return USER_NAME.get();
    }

    private static final Lazy<String> PASSWORD =
            new Lazy<>(() -> {
                String v = PROPERTIES.getProperty("password");
                if (v == null || v.length() == 0) {
                    throw new IllegalStateException("Password is not specified");
                }
                return v;
            });
    public static String getPassword() {
        return PASSWORD.get();
    }

    public static String username() {
        return String.format("%08X", getUserName().hashCode());
    }

    public static String password() {
        return String.format("%08X", getPassword().hashCode());
    }

    public static String userInfoQuery() {
        return "u=" + username() + "&p=" + password();
    }

    private static final Lazy<String> CURRENT_PATH = new Lazy<>(() -> {
        File file = new File(".");
        String stringValue = file.getAbsolutePath();
        return stringValue.endsWith("\\.") ? stringValue.substring(0, stringValue.length() - 2) : stringValue;
    });

    public static String getCurrentPath() {
        return CURRENT_PATH.get();
    }

    private static final Lazy<Integer> TIMEOUT = new Lazy<>(() -> {
        String value = PROPERTIES.getProperty("server.timeout");
        if (value == null || value.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(value);
    });
    public static int getTimeout() {
        return TIMEOUT.get();
    }

    public static void setProperty(String key, String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i))) {
                throw new IllegalArgumentException("property value contains white space: " + key + "=" + value);
            }
        }
        PROPERTIES.setProperty(key, value);
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String getRequiredProperty(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("required property not defined: " + key);
        }
        return value;
    }
}
