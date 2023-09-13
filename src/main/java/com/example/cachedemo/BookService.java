package com.example.cachedemo;

import com.example.cachedemo.model.Book;
import org.springframework.cache.annotation.Cacheable;

public interface BookService {
    // A useful constant for creating null books
    public static int INVALID_BOOK_ID = 99;

    Book getBook(int id);
    Book getBookIndirectCall(int id);
    Book getBookDifferentCache(int id);
}
