package indi.sophronia.server.file;

import indi.sophronia.server.file.config.PropertyHandler;
import indi.sophronia.server.file.util.ThrowUtil;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) {
        try {
            PropertyHandler.loadDirectory(".");
        } catch (IOException e) {
            throw ThrowUtil.sneakyThrow(e);
        }
        try {
            Server.start();
        } catch (IOException e) {
            throw ThrowUtil.sneakyThrow(e);
        }
    }
}
