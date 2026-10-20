import { API_V1_PATH } from "@/06-shared/api/request/config";
import { request } from "@/06-shared/api/request";
import type { Book, BookFilters, CreateBookRequest } from "../model/types";

const BOOKS_API_PATH = `${API_V1_PATH}/books`;

export const getBooks = async (
  filters: BookFilters = {},
  start: number = 0,
  limit: number = 20
): Promise<Book[]> => {
  const searchParams = new URLSearchParams();

  searchParams.set("start", String(start));
  searchParams.set("limit", String(limit));

  if (filters.genre) {
    searchParams.set("genre", filters.genre);
  }

  if (filters.available !== undefined) {
    searchParams.set("available", String(filters.available));
  }

  return request<Book[]>(`${BOOKS_API_PATH}?${searchParams.toString()}`);
};

export const getBook = async (bookId: number): Promise<Book> => {
  return request<Book>(`${BOOKS_API_PATH}/${bookId}`);
};

export const createBook = async (payload: CreateBookRequest): Promise<Book> => {
  return request<Book>(BOOKS_API_PATH, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
};

export const deleteBook = async (bookId: number): Promise<void> => {
  await request<void>(`${BOOKS_API_PATH}/${bookId}`, {
    method: "DELETE"
  });
};

export const releaseBook = async (bookId: number): Promise<void> => {
  await request<void>(`${BOOKS_API_PATH}/${bookId}/release`, {
    method: "POST"
  });
};
