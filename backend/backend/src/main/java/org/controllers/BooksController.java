package org.controllers;

import org.controllers.DTO.GetBookItemResponse;
import org.controllers.DTO.GetBookResponse;
import org.models.Book;
import org.services.BooksService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {
    private final BooksService booksService;

    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping
    public List<GetBookItemResponse> getBooks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Boolean available
    ) {
        return booksService.getAll(genre, available).stream()
                .map(book -> new GetBookItemResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getAvailable()
                ))
                .toList();
    }

    @GetMapping("/{bookId}")
    public GetBookResponse getBook(
            @PathVariable Integer bookId
    ) {
        final Book book = booksService.getById(bookId);

        return new GetBookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getDescription(),
                book.getAvailable()
        );
    }
}
