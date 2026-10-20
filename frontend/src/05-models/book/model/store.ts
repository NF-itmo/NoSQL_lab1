import { useSyncExternalStore } from "react";
import { getBooks } from "../api/books-api";
import type { Book, BookFilters } from "./types";

let books: Book[] = [];
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

export const loadBooks = async (
  filters: BookFilters,
  start: number,
  limit: number,
): Promise<void> => {
  const loadedBooks = await getBooks(filters, start, limit);

  books = loadedBooks;
  notify();
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
