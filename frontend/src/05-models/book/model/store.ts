import { useSyncExternalStore } from "react";
import { getBooks } from "../api/books-api";
import type { Book } from "./types";

let books: Book[] = [];
let initialization: Promise<void> | null = null;
const listeners = new Set<() => void>();

const notify = () => {
  listeners.forEach((listener) => listener());
};

const subscribe = (listener: () => void) => {
  listeners.add(listener);
  
  return () => {
    listeners.delete(listener);
  };
};

const getSnapshot = () => books;

export const useBooks = () => useSyncExternalStore(subscribe, getSnapshot);

export const initializeBooks = (): Promise<void> => {
  if (!initialization) {
    initialization = getBooks()
      .then((loadedBooks) => {
        books = loadedBooks;
        notify();
      })
      .catch((error) => {
        initialization = null;
        throw error;
      });
  }

  return initialization;
};

export const addBook = (book: Book) => {
  books = [...books.filter((item) => item.id !== book.id), book];
  notify();
};

export const removeBook = (bookId: number) => {
  books = books.filter((book) => book.id !== bookId);
  notify();
};

export const setBookAvailability = (bookId: number, available: boolean) => {
  books = books.map((book) => book.id === bookId ? { ...book, available } : book);
  notify();
};
