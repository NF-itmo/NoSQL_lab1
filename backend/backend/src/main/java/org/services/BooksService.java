package org.services;

import org.exceptions.ConflictException;
import org.exceptions.NotFoundException;
import org.models.Book;
import org.repository.BooksRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BooksService {
    private final BooksRepository booksRepository;

    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> getAll(String genre, Boolean available) {
        return booksRepository.getAll().join().stream()
                .filter(book -> genre == null || genre.equalsIgnoreCase(book.getGenre()))
                .filter(book -> available == null || available.equals(book.getAvailable()))
                .toList();
    }

    public Book getById(Integer bookId) {
        return booksRepository.getById(bookId).join()
                .orElseThrow(() -> new NotFoundException("Book not found"));
    }

    public Book create(Book book) {
        if (!booksRepository.create(book).join()) {
            throw new ConflictException("Book with this id already exists");
        }

        return book;
    }

    public void delete(Integer bookId) {
        getById(bookId);
        booksRepository.deleteById(bookId).join();
    }

    public void release(Integer bookId) {
        if (!booksRepository.release(bookId).join()) {
            throw new NotFoundException("Book not found");
        }
    }
}
