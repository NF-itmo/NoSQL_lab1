export type Book = {
  id: number;
  title: string;
  author: string;
  genre: string;
  description: string;
  available: boolean;
  views: number;
};

export type BookListItem = Omit<Book, "description">;

export type BookFilters = {
  genre?: string;
  available?: boolean;
};

export type CreateBookRequest = Omit<Book, "available" | "views">;
