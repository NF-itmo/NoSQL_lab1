import { useState } from "react";
import { addBook, createBook } from "@/05-models/book";
import { useErrorNotify } from "@/06-shared/lib/errorNotifier";
import { PopupButton, PopupInput, PopupTextarea, PopupWindow } from "@/06-shared/ui/popups";
import styles from "./index.module.css";

export const CreateBook = () => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  
  const [id, setId] = useState<string>("");
  const [title, setTitle] = useState<string>("");
  const [author, setAuthor] = useState<string>("");
  const [genre, setGenre] = useState<string>("");
  const [description, setDescription] = useState<string>("");

  const showError = useErrorNotify();

  const reset = () => {
    setId("");
    setTitle("");
    setAuthor("");
    setGenre("");
    setDescription("");
  };

  const submit = async () => {
    const bookId = Number(id);

    if (!Number.isInteger(bookId) || bookId <= 0) {
      showError("ID книги должен быть положительным целым числом");
      return;
    }

    if (!title.trim() || !author.trim() || !genre.trim() || !description.trim()) {
      showError("Заполните все поля книги");
      return;
    }

    try {
      const book = await createBook({
        id: bookId,
        title: title.trim(),
        author: author.trim(),
        genre: genre.trim(),
        description: description.trim(),
      });
      addBook(book);
      reset();
      setIsOpen(false);
    } catch (error) {
      showError(error);
    }
  };

  return (
    <>
      <button className={styles.openButton} onClick={() => setIsOpen(true)}>
        [+]
      </button>

      {isOpen && (
        <PopupWindow name="Новая книга" onClose={() => setIsOpen(false)}>
          <PopupInput name="ID" value={id} setInput={setId} autoFocus={true}/>
          <PopupInput name="Название" value={title} setInput={setTitle}/>
          <PopupInput name="Автор" value={author} setInput={setAuthor}/>
          <PopupInput name="Жанр" value={genre} setInput={setGenre}/>
          <PopupTextarea name="Описание" value={description} setInput={setDescription}/>
          <PopupButton name="Добавить" onClick={submit}/>
        </PopupWindow>
      )}
    </>
  );
};
