package com.example.cachedemo;

import com.example.cachedemo.model.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/book/{id}")
    Book getBook(@PathVariable int id) {
        Book book = new Book();
        book.setId(id);
        book.setTitle("Book" + id);
        book.setYear(2000 + id);
        return book;
    }
}
