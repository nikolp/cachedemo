package com.example.cachedemo;

import com.example.cachedemo.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@CacheConfig(cacheNames = "book-cache")
public class BookServiceImpl implements BookService {
    @Override
    @Cacheable
    public Book getBook(int id) {
        log.info("Expensive call for " + id);
        Book book = new Book();
        book.setId(id);
        book.setTitle("Book" + id);
        book.setYear(2000 + id);
        return book;
    }

    @Override
    public Book getBookIndirectCall(int id) {
        return getBook(id);
    }

    @Override
    @Cacheable("different-cache")
    public Book getBookDifferentCache(int id) {
        return getBook(id);
    }
}
