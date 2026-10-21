import type { Book } from "@/05-models/book";
import styles from "./index.module.css";

type Props = {
  book: Book;
  onClick: () => void;
};

export const ShowBookItem = ({ book, onClick }: Props) => {
  return (
    <button className={styles.book} onClick={onClick}>
      <p className={styles.bookTitle}>
        (#{book.id}) {book.title}
      </p >
      <p>Автор: {book.author}</p>
      <div className={styles.bookFooter}>
        {book.available ? "доступна" : "выдана"}
      </div>
    </button>
  );
};