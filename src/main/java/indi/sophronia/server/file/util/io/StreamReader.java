package indi.sophronia.server.file.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Util class for stream reading
 * @author Sophronia
 */
public class StreamReader {
    /**
     * Read all bytes from input stream and write to a string
     */
    public static String readAll(InputStream in, Charset charset) throws IOException {
        Chunk head = new Chunk(null);
        Chunk tail = head;

        while (!tail.readToFull(in)) {
            tail = new Chunk(tail);
        }

        return Chunk.toString(head, charset);
    }

    /**
     * Read one line ending with system default line separator
     */
    public static String readLine(InputStream in, Charset charset) throws IOException {
        return readLine(in, charset, LineSeparator.osDefault());
    }

    /**
     * Read one line ending with specified line separator
     */
    public static String readLine(InputStream in, Charset charset,
                                  LineSeparator ls) throws IOException {
        Chunk head = new Chunk(null);
        Chunk tail = head;

        boolean eof = false;
        while (true) {
            int v = tail.readByte(in);
            if (v == -1) {
                eof = true;
                break;
            }
            if (lineEnd(tail, ls)) {
                break;
            }
            if (tail.isFull()) {
                tail = new Chunk(tail);
            }
        }

        if (!eof) {
            tail.removeTail(ls.stringValue().length());
        }
        return Chunk.toString(head, charset, eof);
    }

    private static boolean lineEnd(Chunk chunk, LineSeparator ls) {
        int tail = chunk.atReverseIndex(0);
        if (tail != '\r' && tail != '\n') {
            return false;
        }

        if (ls == LineSeparator.CRLF) {
            if (tail != '\n') {
                return false;
            }
            int tail2 = chunk.atReverseIndex(1);
            return tail2 == '\r';
        } else {
            return tail == ls.stringValue().charAt(0);
        }
    }

    /**
     * Read all bytes from input stream, and write to the output stream without
     * persistent storage in byte buffer
     */
    public static void readAll(InputStream in, OutputStream out) throws IOException {
        Chunk chunk = new Chunk(null);
        while (true) {
            boolean finished = chunk.readToFull(in);
            out.write(chunk.getBytes(), 0, chunk.getLength());
            if (finished) {
                break;
            }
            chunk.clear();
        }
    }
}
