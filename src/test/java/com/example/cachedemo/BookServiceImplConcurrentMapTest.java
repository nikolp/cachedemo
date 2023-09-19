package com.example.cachedemo;

import com.example.cachedemo.model.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@Slf4j
// Same config as BookServiceImplTest
// Except for the property override below
// Trying to see that despite all Redis availability,
// the finally chosen implementation is in-memory simple HashMap
@SpringBootTest(properties = "spring.cache.type=simple")
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.Random.class)  // ensure sequential order since shared cache
// @Import(TestConfigurationEmbeddedRedis.class)  // this has to be turned off unless we find way to start on random port
class BookServiceImplConcurrentMapTest {
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
    void whenCallingFromOutside_UsesConcurrentHashMap() {
        log.info("This should appear once:");
        bookService.getBook("1");
        bookService.getBook("1");
        Cache cache = cacheManager.getCache("book-cache");
        ConcurrentMapCache mapCache = (ConcurrentMapCache) cache;
        assertThat(mapCache.getNativeCache().keySet(), contains("1"));
    }
}