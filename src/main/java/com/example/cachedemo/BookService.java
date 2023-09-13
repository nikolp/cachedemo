package com.example.cachedemo;

import com.example.cachedemo.model.Book;
import org.springframework.cache.annotation.Cacheable;

public interface BookService {
    Book getBook(int id);
    Book getBookIndirectCall(int id);
    Book getBookDifferentCache(int id);
}
