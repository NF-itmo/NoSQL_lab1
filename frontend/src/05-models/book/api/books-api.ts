import { API_V1_PATH } from "@/06-shared/api/request/config";
import { request } from "@/06-shared/api/request";
import type { Book, BookFilters, CreateBookRequest } from "../model/types";

const BOOKS_API_PATH = `${API_V1_PATH}/books`;

export const getBooks = async (filters: BookFilters = {}): Promise<Book[]> => {
  const searchParams = new URLSearchParams();

  if (filters.genre) {
    searchParams.set("genre", filters.genre);
  }

  if (filters.available !== undefined) {
    searchParams.set("available", String(filters.available));
  }

  const query = searchParams.size > 0 ? `?${searchParams.toString()}` : "";
  return request<Book[]>(`${BOOKS_API_PATH}${query}`);
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
