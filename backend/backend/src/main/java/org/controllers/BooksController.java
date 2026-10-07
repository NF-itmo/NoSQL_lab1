package org.controllers;

import jakarta.validation.Valid;
import org.controllers.DTO.CreateBookRequest;
import org.controllers.DTO.GetBookResponse;
import org.models.Book;
import org.services.BooksService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GetBookResponse> createBook(
            @RequestBody @Valid final CreateBookRequest request
    ) {
        final Book book = booksService.create(Book.builder()
                .id(request.getId())
                .title(request.getTitle())
                .author(request.getAuthor())
                .genre(request.getGenre())
                .description(request.getDescription())
                .available(true)
                .build());

        return ResponseEntity.ok(
                toResponse(book)
        );
    }

    @GetMapping
    public List<GetBookResponse> getBooks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Boolean available
    ) {
        return booksService.getAll(genre, available).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{bookId}")
    public GetBookResponse getBook(
            @PathVariable Integer bookId
    ) {
        final Book book = booksService.getById(bookId);

        return toResponse(book);
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Integer bookId
    ) {
        booksService.delete(bookId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookId}/release")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> releaseBook(
            @PathVariable Integer bookId
    ) {
        booksService.release(bookId);

        return ResponseEntity.noContent().build();
    }

    private GetBookResponse toResponse(Book book) {
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
