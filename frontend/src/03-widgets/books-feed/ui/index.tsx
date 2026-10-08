import { useEffect, useRef, useState } from "react";
import {
  getBook,
  initializeBooks,
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

export const BooksFeed = () => {
  const books = useBooks();
  const [filters, setFilters] = useState<BookFilters>({});
  const [selectedBook, setSelectedBook] = useState<Book | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const genreWasChanged = useRef<boolean>(false);
  const showError = useErrorNotify();

  useEffect(() => {
    initializeBooks()
      .catch(showError)
      .finally(() => setIsLoading(false));
  }, []);

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

  const changeFilters = (newFilters: BookFilters) => {
    if (newFilters.genre !== filters.genre) {
      genreWasChanged.current = true;
    }

    setFilters(newFilters);
  };

  const showBookDetails = async (bookId: number) => {
    try {
      setSelectedBook(await getBook(bookId));
    } catch (error) {
      showError(error);
    }
  };

  const filteredBooks = books
    .filter(
      (book) => !filters.genre || book.genre.toLowerCase().includes(filters.genre.toLowerCase())
    )
    .filter(
      (book) => filters.available === undefined || book.available === filters.available
    );

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
          (!isLoading && filteredBooks.length === 0) && (
            <p className={styles.message}>Книги не найдены</p>
          )
        }

        {
          filteredBooks.map((book) => (
            <ShowBookItem
              key={book.id}
              book={book}
              onClick={() => showBookDetails(book.id)}
            />
          ))
        }
      </div>

      <BookPopup
        book={selectedBook}
        onClose={() => setSelectedBook(null)}
      />
    </section>
  );
};
