package indi.sophronia.server.file.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * <p>Use linked list of byte chunks in order to avoid array resizing operations.
 * <p>{@link java.util.LinkedList} is not used here because I need to operate the
 * linked nodes directly
 * @author Sophronia
 */
public class Chunk {
    private static final int BUFFER_SIZE = 1024;

    private final byte[] bytes;
    private int length;

    private final Chunk prev;
    private Chunk next;

    public Chunk(Chunk prev) {
        bytes = new byte[BUFFER_SIZE];
        length = 0;

        this.prev = prev;
        if (prev != null) {
            prev.next = this;
        }
    }

    public boolean isFull() {
        return length == BUFFER_SIZE;
    }

    public int getLength() {
        return length;
    }

    public void clear() {
        length = 0;
    }

    public int readByte(InputStream in) throws IOException {
        int v = in.read();
        if (v != -1) {
            addByte(v);
        }
        return v;
    }

    public void addByte(int b) {
        bytes[length++] = (byte) b;
    }

    /**
     * @return Tail chunk of the chain after addition
     */
    public Chunk addBytes(byte[] b, int offset, int length) {
        int margin = BUFFER_SIZE - this.length;
        if (length < margin) {
            System.arraycopy(b, offset, bytes, this.length, length);
            this.length += length;
            return this;
        } else {
            System.arraycopy(b, offset, bytes, this.length, margin);
            this.length = BUFFER_SIZE;
            return new Chunk(this).addBytes(b, offset + length, length - margin);
        }
    }

    /**
     * Read bytes as much as possible, until buffer is full or the stream is ended
     * @return true if all bytes are read
     */
    public boolean readToFull(InputStream in) throws IOException {
        int v = in.read(bytes, length, BUFFER_SIZE - length);
        if (v == -1) {
            return true;
        }

        length += v;
        return false;
    }

    public byte[] getBytes() {
        return bytes;
    }

    /**
     * @return -1 if out of range. Otherwise, return the
     * byte at reverse index of the sub-chain ending with 'this'
     */
    public int atReverseIndex(int index) {
        if (index < length) {
            return bytes[index];
        }
        if (prev == null) {
            return -1;
        }
        return prev.atReverseIndex(index - length);
    }

    /**
     * Remove bytes from tail of the chain. Make sure 'this' refers to the tail node
     * @param rSize size of bytes to remove
     * @return Tail node of the chain after deletion
     */
    public Chunk removeTail(int rSize) {
        if (next != null) {
            throw new IllegalStateException("Current chunk must be tail node of list");
        }
        if (rSize < length) {
            length -= rSize;
        } else {
            if (prev == null) {
                length = 0;
            } else {
                prev.next = null;
                return prev.removeTail(rSize - length);
            }
        }
        return this;
    }

    /**
     * @return Total length of the sub-chain beginning with head
     */
    public static int totalLength(Chunk head) {
        int length = 0;
        Chunk fast = head;
        Chunk current = head;

        while (current != null) {
            length += current.length;
            current = current.next;
            if (fast != null) {
                fast = fast.next;
            }
            if (fast != null) {
                fast = fast.next;
            }
            if (current != null && current == fast) {
                throw new IllegalStateException("Current list of chunks is circular");
            }
        }

        return length;
    }

    public static String toString(Chunk head, Charset charset) {
        if (head.next == null && head.length == 0) {
            return null;
        }

        byte[] bytes = new byte[totalLength(head)];
        int total = 0;
        for (Chunk chunk = head; chunk != null; chunk = chunk.next) {
            System.arraycopy(chunk.bytes, 0, bytes, total, chunk.length);
            total += chunk.length;
        }

        return new String(bytes, charset);
    }

    public static void writeBytes(Chunk head, OutputStream out) throws IOException {
        Chunk fast = head;
        Chunk current = head;

        while (current != null) {
            current = current.next;
            if (fast != null) {
                fast = fast.next;
            }
            if (fast != null) {
                fast = fast.next;
            }
            if (current != null && current == fast) {
                throw new IllegalStateException("Current list of chunks is circular");
            }
        }

        for (Chunk chunk = head; chunk != null; chunk = chunk.next) {
            out.write(chunk.bytes, 0, chunk.length);
        }
    }
}
