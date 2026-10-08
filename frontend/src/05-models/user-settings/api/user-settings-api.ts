import { API_V1_PATH } from "@/06-shared/api/request/config";
import { request } from "@/06-shared/api/request";
import type { GenreFilterSettings } from "../model/types";

const GENRE_FILTER_SETTINGS_API_PATH = `${API_V1_PATH}/settings/genre-filter`;

export const getGenreFilterSettings = (): Promise<GenreFilterSettings> =>
  request<GenreFilterSettings>(GENRE_FILTER_SETTINGS_API_PATH);

export const updateGenreFilterSettings = (genre: string): Promise<GenreFilterSettings> =>
  request<GenreFilterSettings>(GENRE_FILTER_SETTINGS_API_PATH, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ genre }),
  });