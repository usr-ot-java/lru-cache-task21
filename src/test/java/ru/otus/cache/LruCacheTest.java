package ru.otus.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LruCacheTest {

    private ByteArrayOutputStream byteArrayOutputStream;
    private ObjectOutputStream persistentObjectOutputStream;

    @BeforeEach
    public void init() throws IOException {
        byteArrayOutputStream = new ByteArrayOutputStream();
        persistentObjectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    }

    @Test
    public void testCacheGet() throws IOException {
        try (Cache<String, Long> cache = new LruCache<>(3L, 1L, null, persistentObjectOutputStream)) {
            String key = "1";
            Long number = 1L;

            cache.put(key, number);

            assertEquals(number, cache.get(key).get());
            assertEquals(Optional.empty(), cache.get("nonExistingKey"));
        }
    }

    @Test
    public void testCacheGetSize() throws IOException {
        try (Cache<String, Long> cache = new LruCache<>(3L, 1L, null, persistentObjectOutputStream)) {
            cache.put("1", 1L);
            cache.put("2", 2L);
            cache.put("3", 3L);

            assertEquals(cache.getSize(), 3);
        }
    }

    @Test
    public void testCacheEvictionPolicy1() throws IOException {
        try (Cache<String, Long> cache = new LruCache<>(3L, 1L, null, persistentObjectOutputStream)) {
            cache.put("1", 1L);
            cache.put("2", 2L);
            cache.put("3", 3L);
            cache.put("4", 4L);

            assertEquals(cache.getSize(), 3);
            assertEquals(2L, cache.get("2").get());
            assertEquals(3L, cache.get("3").get());
            assertEquals(4L, cache.get("4").get());
        }
    }

    @Test
    public void testCacheEvictionPolicy2() throws IOException {
        try (Cache<String, Long> cache = new LruCache<>(3L, 1L, null, persistentObjectOutputStream)) {
            cache.put("1", 1L);
            cache.put("2", 2L);
            cache.put("3", 3L);

            cache.get("1");
            cache.put("4", 4L);

            // Key "2" should be evicted from the cache-data
            assertEquals(cache.getSize(), 3);
            assertEquals(1L, cache.get("1").get());
            assertEquals(3L, cache.get("3").get());
            assertEquals(4L, cache.get("4").get());
        }
    }

    @Test
    public void testCacheEvictionPolicy3() throws IOException {
        try (Cache<String, Long> cache = new LruCache<>(3L, 1L, null, persistentObjectOutputStream)) {
            cache.put("1", 1L);
            cache.put("3", 3L);
            cache.put("2", 2L);

            cache.get("1");
            cache.put("4", 4L);

            // Key "3" should be evicted from the cache-data
            assertEquals(cache.getSize(), 3);
            assertEquals(1L, cache.get("1").get());
            assertEquals(2L, cache.get("2").get());
            assertEquals(4L, cache.get("4").get());
        }
    }

    @Test
    public void testCacheInitialization() throws IOException {
        InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("cache-test");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        try (Cache<String, String> cache = new LruCache<>(3L, 3L, objectInputStream, persistentObjectOutputStream)) {
            assertEquals(2L, cache.getSize());
            assertEquals(Optional.of("1"), cache.get("1"));
            assertEquals(Optional.of("2"), cache.get("2"));
        }
    }

    @Test
    public void testCachePersistence() throws IOException, InterruptedException {
        long maxSoftLifeTimeInSec = 5L;
        try (Cache<String, Long> cache = new LruCache<>(3L, maxSoftLifeTimeInSec, null, persistentObjectOutputStream)) {
            cache.put("1", 1L);
            cache.put("2", 2L);
            cache.put("3", 3L);
            Thread.sleep(maxSoftLifeTimeInSec * 2 * 1000);
        }


        byte[] outputBytes = byteArrayOutputStream.toByteArray();
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(outputBytes));

        byteArrayOutputStream = new ByteArrayOutputStream();
        persistentObjectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        try (Cache<String, Long> cache = new LruCache<>(3L, maxSoftLifeTimeInSec, objectInputStream, persistentObjectOutputStream)) {
            assertEquals(3L, cache.getSize());
            assertEquals(Optional.of(1L), cache.get("1"));
            assertEquals(Optional.of(2L), cache.get("2"));
            assertEquals(Optional.of(3L), cache.get("3"));
        }
    }

}
