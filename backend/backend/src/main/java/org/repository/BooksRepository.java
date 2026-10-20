package org.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.models.Book;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class BooksRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Book> getById(Integer id) {
        return Optional.ofNullable(entityManager.find(Book.class, id));
    }

    public List<Book> get(Integer start, Integer limit) {
        return entityManager
                .createQuery(
                        "SELECT book FROM Book book ORDER BY book.id ASC",
                        Book.class
                )
                .setFirstResult(start)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Book> getByNameFragment(String nameFragment, Integer start, Integer limit) {
        return entityManager
                .createQuery("""
                        SELECT book FROM Book book
                        WHERE LOWER(book.title) LIKE CONCAT('%', LOWER(:fragment), '%')
                        ORDER BY book.id ASC
                """, Book.class)
                .setParameter("fragment", nameFragment)
                .setFirstResult(start)
                .setMaxResults(limit)
                .getResultList();
    }

    @Transactional
    public void create(Book book) {
        entityManager.persist(book);
    }

    @Transactional
    public void delete(Integer id) {
        entityManager
                .createQuery("DELETE FROM Book book WHERE book.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Transactional
    public void reserve(Integer id) {
        entityManager
                .createQuery(
                        "UPDATE Book book SET book.available = false WHERE book.id = :id"
                )
                .setParameter("id", id)
                .executeUpdate();
    }

    @Transactional
    public void release(Integer id) {
        entityManager
                .createQuery(
                        "UPDATE Book book SET book.available = true WHERE book.id = :id"
                )
                .setParameter("id", id)
                .executeUpdate();
    }
}
