package ru.otus.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import ru.otus.cache.Cache;
import ru.otus.cache.LruCache;

import java.io.*;

@Configuration
public class CacheConfiguration {

    private final Long capacity;
    private final Long maxLifetimeBeforePersistInSec;
    private final String persistDataFilePath;
    private final Resource initDataFilePath;

    public CacheConfiguration(@Value("${cache.capacity}") Long capacity,
                              @Value("${cache.persist.time}") Long maxLifetimeBeforePersistInSec,
                              @Value("${cache.persist.path}") String persistDataFilePath,
                              @Value("classpath:${cache.init.path}") Resource initDataFilePath) {
        this.capacity = capacity;
        this.maxLifetimeBeforePersistInSec = maxLifetimeBeforePersistInSec;
        this.persistDataFilePath = persistDataFilePath;
        this.initDataFilePath = initDataFilePath;
    }

    @Bean
    // Cache<String, String> since String implements Serializable
    public Cache<String, String> cache() throws IOException {
        InputStream fileInputStream = initDataFilePath.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(persistDataFilePath);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        return new LruCache<>(capacity, maxLifetimeBeforePersistInSec, objectInputStream, objectOutputStream);
    }

}
