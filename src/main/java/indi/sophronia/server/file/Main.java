package indi.sophronia.server.file;

import com.sun.net.httpserver.HttpServer;
import indi.sophronia.server.file.config.PropertyHandler;
import indi.sophronia.server.file.controller.DownloadHandler;
import indi.sophronia.server.file.controller.ShutdownHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(PropertyHandler.userInfoQuery());
        HttpServer httpServer = HttpServer.
                create(new InetSocketAddress(PropertyHandler.getPort()), 0);
        httpServer.createContext("/file", new DownloadHandler());
        httpServer.createContext("/shutdown", new ShutdownHandler(httpServer,
                (server) -> {
                    server.removeContext("/file");
                    server.removeContext("/shutdown");
                    server.stop(0);
                }));
        httpServer.start();
    }
}
