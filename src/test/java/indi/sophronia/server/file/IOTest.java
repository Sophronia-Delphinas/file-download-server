package indi.sophronia.server.file;

import indi.sophronia.server.file.util.io.StreamReader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class IOTest {
    public static void main(String[] args) throws IOException {
        try (InputStream in = new BufferedInputStream(new FileInputStream("pom.xml"))) {
            System.out.println(StreamReader.readAll(in, StandardCharsets.UTF_8));
        }
        try (InputStream in = new BufferedInputStream(new FileInputStream("pom.xml"))) {
            while (true) {
                String line = StreamReader.readLine(in, StandardCharsets.UTF_8);
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
        }
    }
}
