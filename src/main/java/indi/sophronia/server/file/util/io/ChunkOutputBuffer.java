package indi.sophronia.server.file.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Output stream based on byte chunks
 * @author Sophronia
 */
public class ChunkOutputBuffer extends OutputStream {
    private final Chunk head;
    private Chunk tail;

    {
        head = tail = new Chunk(null);
    }

    @Override
    public void write(byte[] b) {
        tail = tail.addBytes(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        tail = tail.addBytes(b, off, len);
    }

    @Override
    public void write(int b) {
        tail.addByte(b);
        if (tail.isFull()) {
            tail = new Chunk(tail);
        }
    }

    /**
     * Write bytes to another output stream
     */
    public void write(OutputStream out) throws IOException {
        if (out == this) {
            throw new IllegalStateException("Cannot write to this");
        }
        Chunk.writeBytes(head, out);
    }

    public int totalLength() {
        return Chunk.totalLength(head);
    }

    public String outputToString(Charset charset) {
        return Chunk.toString(head, charset);
    }
}
