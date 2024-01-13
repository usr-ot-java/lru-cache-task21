package ru.otus.cache;

import java.util.Optional;

public interface Cache<K, V> extends AutoCloseable {
    Optional<V> get(K key);
    void put(K key, V value);

    long getSize();

    void close();
}
