package com.example.cachedemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "keep-going-when-cache-fails",
        havingValue = "true",
        matchIfMissing = true
)
public class CustomCachingConfigurerSupport extends CachingConfigurerSupport {
    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }

    private static class CustomCacheErrorHandler implements CacheErrorHandler {
        private static final Logger log = LoggerFactory.getLogger(CustomCacheErrorHandler.class);

        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            log.error("Error in Get data from cache '{}' for key: {}", cache.getName(), key, exception);
        }

        @Override
        public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
            log.error("Error in Put data from cache {} for key: {}", cache.getName(), key, exception);
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
            log.error("Error in Evict data from cache {} for key: {}", cache.getName(), key, exception);
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, Cache cache) {
            log.error("Error in Clear data from cache {}", cache.getName(), exception);
        }
    }
}

