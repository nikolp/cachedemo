package com.example.cachedemo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

/**
 * Based on https://www.baeldung.com/spring-embedded-redis
 * @Import this in @SpringBootTest that require real cache server to be up
 * This seems more reusable than the following, which also works, but must be done
 * in each test class:
private static RedisServer redisServer;

 @BeforeAll
 static void redisStart() {
 redisServer = new RedisServer(6379);
 redisServer.start();
 }

 @AfterAll
 static void redisStop() {
 redisServer.stop();
 }
 */
@TestConfiguration
public class TestConfigurationEmbeddedRedis {

    private RedisServer redisServer;

    // RedisProperties auto-magically injected by auto configuration
    public TestConfigurationEmbeddedRedis(RedisProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getPort());
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
