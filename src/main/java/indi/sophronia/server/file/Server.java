package indi.sophronia.server.file;

import com.sun.net.httpserver.HttpServer;
import indi.sophronia.server.file.config.PropertyHandler;
import indi.sophronia.server.file.controller.DownloadHandler;
import indi.sophronia.server.file.controller.ShutdownHandler;
import indi.sophronia.server.file.util.net.NetContext;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void start() throws IOException {
        start(() -> System.out.println("default on exit: " + NetContext.getExitMessage()));
    }

    public static void start(Runnable onExit) throws IOException {
        System.out.println("current port: " + PropertyHandler.getPort());
        System.out.println("current access user: " + PropertyHandler.userInfoQuery());
        HttpServer httpServer = HttpServer.
                create(new InetSocketAddress(PropertyHandler.getPort()), 0);
        httpServer.createContext("/file", new DownloadHandler());
        httpServer.createContext("/shutdown", new ShutdownHandler(
                () -> {
                    httpServer.removeContext("/file");
                    httpServer.removeContext("/shutdown");
                    httpServer.stop(0);
                    onExit.run();

                    NetContext.clear();
                }));
        httpServer.start();
    }
}
