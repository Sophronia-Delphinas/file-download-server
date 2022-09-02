package indi.sophronia.server.file.util.io;

public enum LineSeparator {
    CR("\r"),
    LF("\n"),
    CRLF("\r\n");

    private static LineSeparator defaultValue;

    public static LineSeparator osDefault() {
        if (defaultValue == null) {
            String s = System.getProperty("line.separator");
            for (LineSeparator value : values()) {
                if (value.stringValue.equals(s)) {
                    defaultValue = value;
                    break;
                }
            }
            if (defaultValue == null) {
                throw new IllegalStateException("Unsupported line separator: " + s);
            }
        }

        return defaultValue;
    }

    private final String stringValue;

    LineSeparator(String stringValue) {
        this.stringValue = stringValue;
    }

    public String stringValue() {
        return stringValue;
    }
}
