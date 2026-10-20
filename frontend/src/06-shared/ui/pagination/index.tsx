import styles from "./index.module.css";

type props = {
  page: number;
  hasNextPage: boolean;
  onPrevious: () => void;
  onNext: () => void;
};

export const Pagination = ({
  page,
  hasNextPage,
  onPrevious,
  onNext,
}: props) => {
  return (
    <nav className={styles.pagination}>
      <button
        className={styles.button}
        type="button"
        disabled={page === 0}
        onClick={onPrevious}
      >
        {"[<]"}
      </button>
      
      <p className={styles.currentPage}>
        {page + 1}
      </p>
      
      <button
        className={styles.button}
        type="button"
        disabled={!hasNextPage}
        onClick={onNext}
      >
        {"[>]"}
      </button>
    </nav>
  );
};
