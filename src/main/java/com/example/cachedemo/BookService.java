package com.example.cachedemo;

import com.example.cachedemo.model.Book;
import org.springframework.web.bind.annotation.PathVariable;

public interface BookService {
    Book getBook(int id);
}
