package com.example.cachedemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@SpringBootConfiguration
@EnableCaching
public class ApplicationConfiguration {
    @Bean
    public RedisCacheConfiguration cacheConfiguration(@Value("${redis.defaultTtlSec}") int ttlSec) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ttlSec))
                .disableCachingNullValues()
                // Need this to do Json serialization. But otherwise just annotate your POJO with "implements Serializable"
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                  .disableCreateOnMissingCache()
                  // the below handled ok by property file
//                .withCacheConfiguration("book-cache",
//                        cacheConfiguration().entryTtl(Duration.ofSeconds(20))
                ;
      }
}
