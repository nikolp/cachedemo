package com.example.cachedemo;

import com.example.cachedemo.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MainController {

    @Autowired
    private BookService bookService;

    @GetMapping("/book/{id}")
    Book getBook(@PathVariable int id) {
        return bookService.getBook(id);
    }
}
