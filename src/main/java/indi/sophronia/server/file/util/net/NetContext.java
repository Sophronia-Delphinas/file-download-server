package indi.sophronia.server.file.util.net;

import com.sun.net.httpserver.HttpExchange;
import indi.sophronia.server.file.util.ThrowUtil;

import java.io.UnsupportedEncodingException;

public class NetContext {
    private static final ThreadLocal<HttpExchange> EXCHANGE = new ThreadLocal<>();
    private static final ThreadLocal<UriQuery> QUERY = new ThreadLocal<>();

    public static void setExchange(HttpExchange exchange) {
        EXCHANGE.set(exchange);
    }

   public static UriQuery getQuery() {
        if (QUERY.get() == null) {
            try {
                UriQuery uriQuery = new UriQuery(EXCHANGE.get().getRequestURI());
                QUERY.set(uriQuery);
            } catch (UnsupportedEncodingException e) {
                throw ThrowUtil.sneakyThrow(e);
            }
        }
        return QUERY.get();
    }

    public static void clear() {
        EXCHANGE.remove();
        QUERY.remove();
    }
}
