package ru.otus.cache;


import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.otus.cache.ds.DoubleLinkedList;
import ru.otus.cache.ds.Node;

import java.io.*;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.*;

@ToString
@Slf4j
public class LruCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {

    private final Long capacity;
    private final Long maxLifetimeBeforePersistInSec;
    private final ObjectOutputStream persistentStorageOutputStream;
    private final Map<K, Node<CacheElement<K, V>>> storage = new ConcurrentHashMap<>();
    private final DoubleLinkedList<CacheElement<K, V>> linkedList = new DoubleLinkedList<>();
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private final Queue<CacheElement<K, V>> storagePutsHistory = new ConcurrentLinkedQueue<>();

    public LruCache(Long capacity, Long maxSoftLifeTimeInSec, ObjectInputStream persistentStorageInputStream, ObjectOutputStream persistentStorageOutputStream) throws IOException {
        this.capacity = capacity;
        this.maxLifetimeBeforePersistInSec = maxSoftLifeTimeInSec;
        this.persistentStorageOutputStream = persistentStorageOutputStream;
        if (persistentStorageInputStream != null) {
            initialize(persistentStorageInputStream);
        }
        scheduledExecutor.scheduleAtFixedRate(this::persistData, maxSoftLifeTimeInSec, maxSoftLifeTimeInSec, TimeUnit.SECONDS);
    }

    @Override
    public Optional<V> get(K key) {
        if (storage.containsKey(key)) {
            var node = storage.get(key);
            synchronized (linkedList) {
                linkedList.moveNodeToBeginning(node);
            }
            return Optional.of(node.getItem().getValue());
        }
        return Optional.empty();
    }

    @Override
    public void put(K key, V value) {
        var cacheElement = new CacheElement<>(key, value);
        if (storage.containsKey(key)) {
            var oldNode = storage.get(key);
            Node<CacheElement<K, V>> updatedNode;
            synchronized (linkedList) {
                updatedNode = linkedList.updateNodeValueAndMoveToBeginning(oldNode, cacheElement);
            }
            storage.put(key, updatedNode);
            storagePutsHistory.add(cacheElement);
            return;
        }
        if (capacity < storage.size() + 1) {
            // Remove element least recent used element
            Node<CacheElement<K, V>> removedNode;
            synchronized (linkedList) {
                removedNode = linkedList.deleteFromTail();
            }
            var removedCacheElem = removedNode.getItem();
            storage.remove(removedCacheElem.getKey());
        }
        Node<CacheElement<K, V>> newNode;
        synchronized (linkedList) {
            newNode = linkedList.insertAtHead(cacheElement);
        }
        storage.put(key, newNode);
        storagePutsHistory.add(cacheElement);
    }

    @Override
    public long getSize() {
        return storage.size();
    }

    @Override
    public synchronized void close() {
        scheduledExecutor.shutdown();
        try {
            persistentStorageOutputStream.writeInt(0);
            persistentStorageOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close the stream to the persistent storage", e);
        }
    }

    private synchronized void persistData() {
        int storagePutsSize = storagePutsHistory.size();
        log.info(String.format("Started the process of persisting the data. The amount of puts to be processed: %d", storagePutsSize));

        try {
            persistentStorageOutputStream.writeInt(storagePutsSize);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write the amount of puts to the persistent storage", e);
        }

        for (int i = 0; i < storagePutsSize; i++) {
            var cacheElement = storagePutsHistory.poll();
            try {
                persistentStorageOutputStream.writeObject(cacheElement);
            } catch (IOException e) {
                throw new RuntimeException(String.format(
                        "Failed to persis data for key = %s and value = %s", cacheElement.getKey(), cacheElement.getValue()
                ), e);
            }
        }

        try {
            persistentStorageOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to flush data to the persistent storage", e);
        }
        log.info("Finished the process of persisting the data. The data is flushed.");
    }

    @SuppressWarnings("unchecked")
    private synchronized void initialize(ObjectInputStream persistentStorageInputStream) throws IOException {
        try (persistentStorageInputStream) {
            log.info("Started cache initialization");
            while (true) {
                int amountOfElements = persistentStorageInputStream.readInt();
                for (int i = 0; i < amountOfElements; i++) {
                    CacheElement<K, V> cacheElement = (CacheElement<K, V>) persistentStorageInputStream.readObject();
                    log.info(String.format("Read cache entry. Key: %s. Value: %s", cacheElement.getKey().toString(), cacheElement.getValue().toString()));
                    put(cacheElement.getKey(), cacheElement.getValue());
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to init the cache-data", e);
        } catch (EOFException e) {
            // Ignore exception
            log.info("Finished cache initialization");
        }
    }
}
