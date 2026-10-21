package org.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.models.Book;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public List<Book> search(
            String titleFragment,
            Boolean availabilityStatus,
            Integer start,
            Integer limit
    ) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Book> query = builder.createQuery(Book.class);
        final Root<Book> book = query.from(Book.class);
        final List<Predicate> filters = new ArrayList<>();

        if (titleFragment != null && !titleFragment.isBlank()) {
            filters.add(builder.like(
                    builder.lower(book.get("title")),
                    "%" + titleFragment.trim().toLowerCase() + "%"
            ));
        }
        if (availabilityStatus != null) {
            filters.add(builder.equal(book.get("available"), availabilityStatus));
        }
        query.where(filters);

        query.orderBy(builder.asc(book.get("id")));

        return entityManager
                .createQuery(query)
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
