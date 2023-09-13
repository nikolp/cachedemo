package com.example.cachedemo;

import com.example.cachedemo.model.Book;
import com.example.cachedemo.model.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.CacheStatistics;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
// Some great examples how to customize this: https://reflectoring.io/spring-boot-test/
@SpringBootTest(properties = "spring.cache.redis.enable-statistics=true")
@ActiveProfiles("idea")
@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.Random.class)
class BookServiceImplTest {
    @Autowired
    BookServiceImpl bookService;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    CacheUtil cacheUtil;

    @BeforeEach
    void SetUp() throws InterruptedException {
        cacheUtil.clearAll();
    }

    @Test
    void whenCallingFromOutside_Works() {
        log.info("This should appear once:");
        bookService.getBook(1);
        bookService.getBook(1);
        Cache cache = cacheManager.getCache("book-cache");
        RedisCache redisCache = (RedisCache) cache;
        CacheStatistics stats = redisCache.getStatistics();
        assertEquals(1, stats.getMisses());
        assertEquals(1, stats.getHits());
    }

    @Test
    void whenCallingFromWithinSameClass_NoCachingLogic() {
        log.info("This should appear twice:");
        bookService.getBookIndirectCall(2);
        bookService.getBookIndirectCall(2);
        Cache cache = cacheManager.getCache("book-cache");
        RedisCache redisCache = (RedisCache) cache;
        CacheStatistics stats = redisCache.getStatistics();
        // Not even a miss!
        assertEquals(0, stats.getMisses());
        assertEquals(0, stats.getHits());
    }

    @Test
    void ttlSetCorrectly() {
        Cache cache = cacheManager.getCache("book-cache");
        RedisCache redisCache = (RedisCache) cache;
        Duration expiry = redisCache.getCacheConfiguration().getTtl();
        assertEquals("PT20S", expiry.toString());
    }

    @Test
    void whenUnknownCacheName_ThrowError() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.getBookDifferentCache(3);
        });
        assertThat(exception.getMessage(),
                MatchesPattern.matchesPattern(".*Cannot find cache named.*"));
    }

    @Test
    void whenOptionalIsNotEmpty_CachingWorks() {
        log.info("This should appear once:");
        Book bookA = bookService.getBookOptional(4).orElseThrow();
        Book bookB = bookService.getBookOptional(4).orElseThrow();
        assertEquals(4, bookA.getId());
        assertEquals(4, bookA.getId());
        Cache cache = cacheManager.getCache("book-cache");
        RedisCache redisCache = (RedisCache) cache;
        CacheStatistics stats = redisCache.getStatistics();
        assertEquals(1, stats.getMisses());
        assertEquals(1, stats.getHits());
    }

    @Test
    void whenOptionalIsEmpty_CachingWorks() {
        log.info("This should appear twice:");
        Optional<Book> bookA = bookService.getBookOptional(BookService.INVALID_BOOK_ID);
        Optional<Book> bookB = bookService.getBookOptional(BookService.INVALID_BOOK_ID);
        assertTrue(bookA.isEmpty());
        assertTrue(bookB.isEmpty());
        Cache cache = cacheManager.getCache("book-cache");
        RedisCache redisCache = (RedisCache) cache;
        CacheStatistics stats = redisCache.getStatistics();
        assertEquals(2, stats.getMisses());
        assertEquals(0, stats.getHits());
    }
}