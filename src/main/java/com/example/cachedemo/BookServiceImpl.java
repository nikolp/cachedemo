package com.example.cachedemo;

import com.example.cachedemo.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames = "book-cache")
public class BookServiceImpl implements BookService {
    private Book makeBook(int id) {
        if (id == BookService.INVALID_BOOK_ID) {
            return null;
        }
        Book book = new Book();
        book.setId(id);
        book.setTitle("Book" + id);
        book.setYear(2000 + id);
        return book;
    }

    @Override
    @Cacheable
    public Book getBook(int id) {
        log.info("Expensive call for " + id);
        return makeBook(id);
    }

    @Override
    public Book getBookIndirectCall(int id) {
        return getBook(id);
    }

    @Cacheable(unless="#result == null")
    public Optional<Book> getBookOptional(int id) {
        log.info("Expensive getBookOptional: " + id);
        Book book = makeBook(id);
        if (book == null) {
            return Optional.empty();
        }
        return Optional.of(book);
    }

    @Override
    @Cacheable("different-cache")
    public Book getBookDifferentCache(int id) {
        return getBook(id);
    }
}
