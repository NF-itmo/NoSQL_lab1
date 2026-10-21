import { API_V1_PATH } from "@/06-shared/api/request/config";
import { request } from "@/06-shared/api/request";
import type { TitleFilterSettings } from "../model/types";

const TITLE_FILTER_SETTINGS_API_PATH = `${API_V1_PATH}/settings/title-filter`;

export const getTitleFilterSettings = (): Promise<TitleFilterSettings> =>
  request<TitleFilterSettings>(TITLE_FILTER_SETTINGS_API_PATH);

export const updateTitleFilterSettings = (title: string): Promise<TitleFilterSettings> =>
  request<TitleFilterSettings>(TITLE_FILTER_SETTINGS_API_PATH, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ title }),
  });