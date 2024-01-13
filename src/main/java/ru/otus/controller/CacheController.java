package ru.otus.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.cache.Cache;
import ru.otus.controller.dto.CacheGetResponse;
import ru.otus.controller.dto.CacheStoreRequest;

@RestController
@RequestMapping("/cache")
@AllArgsConstructor
public class CacheController {

    private final Cache<String, String> cache;

    @GetMapping(consumes="application/json", produces ="application/json")
    public CacheGetResponse getValue(@RequestParam(name = "key") String key) {
        return new CacheGetResponse(cache.get(key).orElse(null));
    }

    @PostMapping(consumes="application/json", produces ="application/json")
    public void putValue(@RequestBody CacheStoreRequest cacheStoreRequest) {
        cache.put(cacheStoreRequest.getKey(), cacheStoreRequest.getValue());
    }

}
