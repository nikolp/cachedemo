package com.example.cachedemo;

import com.example.cachedemo.model.Book;
import org.springframework.cache.annotation.Cacheable;

public interface BookService {
    // A useful constant for creating null books
    public static String INVALID_BOOK_ID = "99";

    Book getBook(String id);
    Book getBookIndirectCall(String id);
    Book getBookDifferentCache(String id);
}
