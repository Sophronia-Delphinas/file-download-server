package indi.sophronia.server.file.util;

import java.util.Objects;
import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private volatile boolean resolved;
    private T value;

    public Lazy(Supplier<T> supplier) {
        this.supplier = Objects.requireNonNull(supplier);
        resolved = false;
        value = null;
    }

    @Override
    public T get() {
        if (!resolved) {
            synchronized (this) {
                if (!resolved) {
                    value = supplier.get();
                    resolved = true;
                }
            }
        }
        return value;
    }
}
