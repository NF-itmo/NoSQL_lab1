package org.controllers;

import org.controllers.DTO.GetBookItemResponse;
import org.controllers.DTO.GetBookResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {
    @GetMapping
    public List<GetBookItemResponse> getBooks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Boolean available
    ) {
        throw new RuntimeException("Not implemented");
    }

    @GetMapping("/{bookId}")
    public GetBookResponse getBook(
            @PathVariable String bookId
    ) {
        throw new RuntimeException("Not implemented");
    }
}
