package com.drew.truelayerservice.util;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
public class RedisService {

    private final ConcurrentMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public void cacheToken(String userId, String accessToken, long expirationTimeSeconds) {
        String key = "token:" + userId;
        cache.put(key, new CacheEntry(accessToken, Instant.now().plusSeconds(expirationTimeSeconds)));
    }

    public String getCachedToken(String userId) {
        String key = "token:" + userId;
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (Instant.now().isAfter(entry.expiresAt())) {
            cache.remove(key);
            return null;
        }
        return entry.token();
    }

    private record CacheEntry(String token, Instant expiresAt) {
    }
}
