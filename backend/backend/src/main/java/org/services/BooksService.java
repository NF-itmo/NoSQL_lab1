package org.services;

import org.exceptions.ConflictException;
import org.exceptions.NotFoundException;
import org.models.Book;
import org.repository.BooksCacheRepository;
import org.repository.BooksRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BooksService {
    private final BooksCacheRepository booksCacheRepository;
    private final BooksRepository booksRepository;

    public BooksService(
            BooksRepository booksRepository,
            BooksCacheRepository booksCacheRepository
    ) {
        this.booksCacheRepository = booksCacheRepository;
        this.booksRepository = booksRepository;
    }

    public List<Book> getAll(String genre, Boolean available) {
        return booksCacheRepository.getAll().join().stream()
                .filter(book -> genre == null || genre.equalsIgnoreCase(book.getGenre()))
                .filter(book -> available == null || available.equals(book.getAvailable()))
                .toList();
    }

    public Book getById(Integer bookId) {
        return booksCacheRepository.getById(bookId).join()
                .orElseThrow(() -> new NotFoundException("Book not found"));
    }

    public Book getDetailsById(Integer bookId) {
        final Book book = getById(bookId);
        book.setViews(booksCacheRepository.incrementViews(bookId).join());

        return book;
    }

    public Book create(Book book) {
        if (!booksCacheRepository.create(book).join()) {
            throw new ConflictException("Book with this id already exists");
        }

        return book;
    }

    public void delete(Integer bookId) {
        getById(bookId);
        booksCacheRepository.deleteById(bookId).join();
    }

    public void release(Integer bookId) {
        if (!booksCacheRepository.release(bookId).join()) {
            throw new NotFoundException("Book not found");
        }
    }
}
