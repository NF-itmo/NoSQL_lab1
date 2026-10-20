import { useEffect, useRef, useState } from "react";
import {
  getBook,
  loadBooks,
  useBooks,
  type Book,
  type BookFilters
} from "@/05-models/book";
import { getGenreFilterSettings, updateGenreFilterSettings } from "@/05-models/user-settings";
import { RequestError } from "@/06-shared/api/request";
import { useErrorNotify } from "@/06-shared/lib/errorNotifier";
import { BooksFilter } from "@/04-features/books-filter";
import { BookPopup } from "@/04-features/book-popup";
import { CreateBook } from "@/04-features/create-book";
import { ShowBookItem } from "@/04-features/show-book-item";
import styles from "./index.module.css";
import { Pagination } from "@/06-shared/ui/pagination";
import { BOOKS_PER_PAGE } from "../config/paginationConfig";

export const BooksFeed = () => {
  const books = useBooks();
  const [filters, setFilters] = useState<BookFilters>({});
  const [page, setPage] = useState<number>(0);
  const [selectedBook, setSelectedBook] = useState<Book | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const genreWasChanged = useRef<boolean>(false);
  const showError = useErrorNotify();

  useEffect(() => {
    getGenreFilterSettings()
      .then((settings) => {
        if (!genreWasChanged.current) {
          setFilters((current) => ({
            ...current,
            genre: settings.genre || undefined,
          }));
        }
      })
      .catch((error) => {
        if (!(error instanceof RequestError && (error.status === 401 || error.status === 403))) {
          showError(error);
        }
      });
  }, []);

  useEffect(() => {
    if (!genreWasChanged.current) return;

    const timeout = window.setTimeout(() => {
      updateGenreFilterSettings(filters.genre ?? "")
        .catch((error) => {
          if (!(error instanceof RequestError && (error.status === 401 || error.status === 403))) {
            showError(error);
          }
        });
    }, 500);

    return () => window.clearTimeout(timeout);
  }, [filters.genre]);

  useEffect(() => {
    let active = true;
    setIsLoading(true);

    const timeout = window.setTimeout(() => {
      loadBooks(
        filters,
        page * BOOKS_PER_PAGE,
        BOOKS_PER_PAGE + 1,
      )
        .catch((error) => {
          if (active){
            showError(error);
          }
        })
        .finally(() => {
          if (active){
            setIsLoading(false);
          }
        });
    }, 300);

    return () => {
      active = false;
      window.clearTimeout(timeout);
    };
  }, [filters, page]);

  const changeFilters = (newFilters: BookFilters) => {
    if (newFilters.genre !== filters.genre) {
      genreWasChanged.current = true;
    }

    setFilters(newFilters);
    setPage(0);
  };

  const showBookDetails = async (bookId: number) => {
    try {
      setSelectedBook(await getBook(bookId));
    } catch (error) {
      showError(error);
    }
  };

  const hasNextPage = books.length > BOOKS_PER_PAGE;
  const visibleBooks = books.slice(0, BOOKS_PER_PAGE);

  return (
    <section className={styles.feed}>
      <div className={styles.header}>
        <h1 className={styles.title}>Книги</h1>
        <CreateBook/>
      </div>

      <BooksFilter filters={filters} onChange={changeFilters}/>

      <div className={styles.content}>
        {
          isLoading && (
            <p className={styles.message}>Загрузка книг...</p>
          )
        }
        {
          (!isLoading && visibleBooks.length === 0) && (
            <p className={styles.message}>Книги не найдены</p>
          )
        }

        {
          !isLoading && visibleBooks.map((book) => (
            <ShowBookItem
              key={book.id}
              book={book}
              onClick={() => showBookDetails(book.id)}
            />
          ))
        }
      </div>

      {
        (page > 0 || hasNextPage) && (
          <Pagination
            page={page}
            hasNextPage={hasNextPage}
            onPrevious={() => setPage((current) => Math.max(current - 1, 0))}
            onNext={() => setPage((current) => current + 1)}
          />
        )
      }

      <BookPopup
        book={selectedBook}
        onClose={() => setSelectedBook(null)}
      />
    </section>
  );
};
