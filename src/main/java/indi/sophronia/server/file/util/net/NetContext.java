package indi.sophronia.server.file.util.net;

import com.sun.net.httpserver.HttpExchange;
import indi.sophronia.server.file.util.ThrowUtil;

import java.io.UnsupportedEncodingException;

public class NetContext {
    private static final ThreadLocal<HttpExchange> EXCHANGE = new ThreadLocal<>();
    private static final ThreadLocal<UriQuery> QUERY = new ThreadLocal<>();
    private static final ThreadLocal<String> EXIT_MESSAGE = new ThreadLocal<>();

    public static void setExchange(HttpExchange exchange) {
        EXCHANGE.set(exchange);
    }

    public static UriQuery getQuery() {
        if (QUERY.get() == null) {
            UriQuery uriQuery;
            try {
                uriQuery = new UriQuery(EXCHANGE.get().getRequestURI());
            } catch (UnsupportedEncodingException e) {
                throw ThrowUtil.sneakyThrow(e);
            }
            QUERY.set(uriQuery);
        }
        return QUERY.get();
    }

    public static void setExitMessage(String exitMessage) {
        EXIT_MESSAGE.set(exitMessage);
    }

    public static String getExitMessage() {
        return EXIT_MESSAGE.get();
    }

    public static void clear() {
        EXCHANGE.remove();
        QUERY.remove();
        EXIT_MESSAGE.remove();
    }
}
