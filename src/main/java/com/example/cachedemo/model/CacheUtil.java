package com.example.cachedemo.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class CacheUtil {
    CacheUtil(CacheManager cacheManager) {this.cacheManager = cacheManager;}
    private CacheManager cacheManager;
    public void clearAll() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            RedisCache redisCache = (RedisCache) cache;
            Duration expiry = redisCache.getCacheConfiguration().getTtl();
            log.info("Examining cache: " + cacheName + " with expiry " + expiry);
            redisCache.clear();
            redisCache.clearStatistics();
            log.info("Cleared it");
        }
    }
}
