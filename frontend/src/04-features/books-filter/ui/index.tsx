import type { BookFilters } from "@/05-models/book";
import styles from "./index.module.css";

type props = {
  filters: BookFilters;
  onChange: (filters: BookFilters) => void;
};

export const BooksFilter = (
  {
    filters,
    onChange
  }: props
) => {
  const changeTitle = (title: string) => {
    onChange({ ...filters, title: title || undefined });
  };

  const changeAvailability = (available: string) => {
    onChange({
      ...filters,
      available: available === "" ? undefined : available === "true",
    });
  };

  return (
    <div className={styles.filters}>
      <label className={styles.field}>
        <span>Название</span>
        <input
          placeholder="Любое"
          value={filters.title ?? ""}
          onChange={(event) => changeTitle(event.target.value)}
        />
      </label>

      <label className={styles.field}>
        <span>Статус</span>
        <select
          value={filters.available === undefined ? "" : String(filters.available)}
          onChange={(event) => changeAvailability(event.target.value)}
        >
          <option value="">Все книги</option>
          <option value="true">Доступные</option>
          <option value="false">Выданные</option>
        </select>
      </label>
    </div>
  );
};
