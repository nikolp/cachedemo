package com.example.cachedemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.CacheStatistics;
import org.springframework.data.redis.cache.RedisCache;


@SpringBootTest
class BookServiceImplTest {
    @Autowired
    BookServiceImpl bookService;
    @Autowired
    CacheManager cacheManager;

    @Test
    void foo() {
        bookService.getBook(5);
        bookService.getBook(5);
        bookService.getBook(10);
        // This is the one where caching does not work
        bookService.getBookIndirectCall(11);
        bookService.getBookIndirectCall(11);
        Cache cache = cacheManager.getCache("books");
        RedisCache redisCache = (RedisCache) cache;
        // TODO: can see caching works via log statements but cannot verify programmatically
        // Unfortunately this is a NoOpCacheStatisticsCollector that returns 0 for everything
        CacheStatistics stats = redisCache.getStatistics();
        stats.getMisses();
    }
}