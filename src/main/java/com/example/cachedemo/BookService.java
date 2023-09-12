package com.example.cachedemo;

import com.example.cachedemo.model.Book;

public interface BookService {
    Book getBook(int id);
    Book getBookIndirectCall(int id);
}
